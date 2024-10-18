package com.example.androidapputility.typedef

enum class FileType(private val type: String) {

    IMAGE("image/*"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),

    VIDEO("video/*"),
    MP4("video/mp4"),
    AVI("video/x-msvideo"),
    QUICKTIME("video/quicktime"),

    AUDIO("audio/*"),
    MP3("audio/mpeg"),
    WAV("audio/wav"),
    M4A("audio/mp4"),

    PDF("application/pdf"),
    MS_DOC("application/msword"),
    MS_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    MS_XLS("application/vnd.ms-excel"),
    MS_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    TEXT("text/plain"),
    ZIP("application/zip"),
    RAR("text/x-rar-compressed");

    override fun toString(): String {
        return type
    }


}