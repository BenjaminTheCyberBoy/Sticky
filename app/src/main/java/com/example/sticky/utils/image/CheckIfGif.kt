package com.example.sticky.utils.image

import android.content.Context
import android.net.Uri

fun isGifUri(context: Context, uri: Uri): Boolean {
    if (uri == Uri.EMPTY) return false
    // GIF magic bytes for "GIF8" (0x47 0x49 0x46 0x38)
    val gifSignature = byteArrayOf(
        0x47.toByte(), // G
        0x49.toByte(), // I
        0x46.toByte(), // F
        0x38.toByte()  // 8
    )
    val header = ByteArray(4)

    try {
        context.contentResolver.openInputStream(uri).use { inputStream ->
            if (inputStream == null) return false

            // Read the first 4 bytes of the file
            val bytesRead = inputStream.read(header)
            if (bytesRead < 4) return false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }

    // Verify if the first 4 bytes match "GIF8"
    return header.contentEquals(gifSignature)
}
