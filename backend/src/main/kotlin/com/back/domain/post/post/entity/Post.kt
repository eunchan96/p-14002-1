package com.back.domain.post.post.entity

import com.back.domain.member.member.entity.Member
import com.back.domain.post.genFile.entity.PostGenFile
import com.back.domain.post.postComment.entity.PostComment
import com.back.domain.post.postUser.entity.PostUser
import com.back.global.exception.ServiceException
import com.back.global.jpa.entity.BaseTime
import com.back.global.rsData.RsData
import com.back.standard.base.Empty
import com.back.standard.util.Ut
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.util.*
import java.util.stream.Collectors

@Entity
class Post(
    @field:ManyToOne(fetch = LAZY) val author: PostUser,
    var title: String,
    content: String
) : BaseTime() {
    @OneToOne(fetch = LAZY, cascade = [PERSIST, REMOVE])
    var body = PostBody(content)

    @OneToMany(
        mappedBy = "post",
        fetch = LAZY,
        cascade = [PERSIST, REMOVE],
        orphanRemoval = true
    )
    val comments: MutableList<PostComment> = mutableListOf()

    var content: String
        get() = body.content
        set(value) {
            if (body.content != value) {
                body.content = value
                updateModifyDate()
            }
        }

    @OneToMany(mappedBy = "post", cascade = [PERSIST, REMOVE], orphanRemoval = true)
    val genFiles: MutableList<PostGenFile> = mutableListOf()

    // OneToOne 은 레이지 로딩이 안된다.
    @ManyToOne(fetch = LAZY)
    var thumbnailGenFile: PostGenFile? = null

    fun modify(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun addComment(author: PostUser, content: String): PostComment {
        val postComment = PostComment(author, this, content)
        comments.add(postComment)

        author.incrementPostCommentsCount()

        return postComment
    }

    fun findCommentById(id: Int): PostComment? {
        return comments.firstOrNull { it.id == id }
    }

    fun deleteComment(postComment: PostComment): Boolean {
        author.decrementPostCommentsCount()

        return comments.remove(postComment)
    }

    fun checkActorCanModify(actor: PostUser) {
        if (author != actor) throw ServiceException("403-1", "${id}번 글 수정권한이 없습니다.")
    }

    fun checkActorCanDelete(actor: PostUser) {
        if (author != actor) throw ServiceException("403-2", "${id}번 글 삭제권한이 없습니다.")
    }

    private fun processGenFile(
        oldPostGenFile: PostGenFile?,
        typeCode: PostGenFile.TypeCode,
        fileNo: Int,
        filePath: String
    ): PostGenFile {
        val isModify = oldPostGenFile != null
        val originalFileName = Ut.file.getOriginalFileName(filePath)
        val metadataStrFromFileName = Ut.file.getMetadataStrFromFileName(filePath)
        val fileExt = Ut.file.getFileExt(filePath)
        val fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt)
        val fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt)

        var metadataStr = Ut.file.getMetadata(filePath).entries.stream()
            .map { it.key + "=" + it.value }
            .collect(Collectors.joining("&"))

        if (Ut.str.isNotBlank(metadataStrFromFileName)) {
            metadataStr = if (Ut.str.isNotBlank(metadataStr))
                "$metadataStr&$metadataStrFromFileName"
            else
                metadataStrFromFileName
        }

        val fileName = if (isModify) Ut.file.withNewExt(oldPostGenFile!!.fileName, fileExt) else UUID.randomUUID()
            .toString() + "." + fileExt
        val fileSize = Ut.file.getFileSize(filePath)
        val actualFileNo = if (fileNo == 0) getNextGenFileNo(typeCode) else fileNo

        val genFile = if (isModify) oldPostGenFile!! else PostGenFile(
            this,
            typeCode,
            actualFileNo
        )

        genFile.originalFileName = originalFileName
        genFile.metadata = metadataStr
        genFile.fileDateDir = Ut.date.getCurrentDateFormatted("yyyy_MM_dd")
        genFile.fileExt = fileExt
        genFile.fileExtTypeCode = fileExtTypeCode
        genFile.fileExtType2Code = fileExtType2Code
        genFile.fileName = fileName
        genFile.fileSize = fileSize

        if (!isModify) genFiles.add(genFile)

        if (isModify) {
            Ut.file.rm(genFile.filePath)
        }

        Ut.file.mv(filePath, genFile.filePath)

        return genFile
    }

    fun addGenFile(typeCode: PostGenFile.TypeCode, fileNo: Int = 0, filePath: String): PostGenFile {
        return processGenFile(null, typeCode, fileNo, filePath)
    }

    private fun getNextGenFileNo(typeCode: PostGenFile.TypeCode): Int {
        return genFiles.stream()
            .filter { genFile -> genFile.typeCode == typeCode }
            .mapToInt { genFile -> genFile.fileNo }
            .max()
            .orElse(0) + 1
    }

    fun getGenFileById(id: Int): Optional<PostGenFile> {
        return genFiles.stream()
            .filter { genFile -> genFile.id == id }
            .findFirst()
    }

    fun getGenFileByTypeCodeAndFileNo(typeCode: PostGenFile.TypeCode, fileNo: Int): Optional<PostGenFile> {
        return genFiles.stream()
            .filter { genFile -> genFile.typeCode == typeCode }
            .filter { genFile -> genFile.fileNo == fileNo }
            .findFirst()
    }

    fun deleteGenFile(typeCode: PostGenFile.TypeCode, fileNo: Int) {
        getGenFileByTypeCodeAndFileNo(typeCode, fileNo)
            .ifPresent { this.deleteGenFile(it) }
    }

    fun deleteGenFile(postGenFile: PostGenFile) {
        Ut.file.rm(postGenFile.filePath)
        genFiles.remove(postGenFile)
    }

    fun modifyGenFile(postGenFile: PostGenFile, filePath: String): PostGenFile {
        return processGenFile(postGenFile, postGenFile.typeCode, postGenFile.fileNo, filePath)
    }

    fun modifyGenFile(typeCode: PostGenFile.TypeCode, fileNo: Int, filePath: String): PostGenFile {
        val postGenFile = getGenFileByTypeCodeAndFileNo(
            typeCode,
            fileNo
        ).get()

        return modifyGenFile(postGenFile, filePath)
    }

    fun putGenFile(typeCode: PostGenFile.TypeCode, fileNo: Int, filePath: String): PostGenFile {
        val opPostGenFile = getGenFileByTypeCodeAndFileNo(
            typeCode,
            fileNo
        )

        return if (opPostGenFile.isPresent) {
            modifyGenFile(typeCode, fileNo, filePath)
        } else {
            addGenFile(typeCode, fileNo, filePath)
        }
    }

    fun checkActorCanMakeNewGenFile(actor: Member?) {
        Optional.of(
            getCheckActorCanMakeNewGenFileRs(actor)
        )
            .filter { rsData -> rsData.isFail }
            .ifPresent { rsData ->
                throw ServiceException(rsData.resultCode, rsData.msg)
            }
    }

    fun getCheckActorCanMakeNewGenFileRs(actor: Member?): RsData<Empty> {
        if (actor == null) return RsData("401-1", "로그인 후 이용해주세요.")

        if (actor == author) return RsData("200-1", "OK")

        return RsData("403-1", "작성자만 파일을 업로드할 수 있습니다.")
    }

    val thumbnailImgUrlOrDefault: String
        get() = Optional.ofNullable(thumbnailGenFile)
            .map { it.publicUrl }
            .orElse("https://placehold.co/1200x1200?text=POST $id&darkInvertible=1")
}