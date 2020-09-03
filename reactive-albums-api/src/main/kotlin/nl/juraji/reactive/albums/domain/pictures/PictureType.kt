package nl.juraji.reactive.albums.domain.pictures

import org.springframework.http.MediaType

enum class PictureType(val contentType: String, val typeName: String, val mediaType: MediaType) {
    JPEG("image/jpeg", "jpeg", MediaType.IMAGE_JPEG),
    BMP("image/bmp", "bmp", MediaType("image", "bmp")),
    GIF("image/gif", "gif", MediaType.IMAGE_GIF),
    PNG("image/png", "png", MediaType.IMAGE_PNG),
    TIFF("image/tiff", "tiff", MediaType("image", "tiff"));

    companion object {
        fun of(contentType: String): PictureType? {
            return values().firstOrNull { pType -> pType.contentType == contentType }
        }

        fun of(mediaType: MediaType): PictureType? {
            return values().firstOrNull { pType -> pType.mediaType == mediaType }
        }
    }
}
