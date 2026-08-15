package com.example.sticky.utils.image

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import kotlin.uuid.Uuid

/**
 * Converts a GIF from a Uri to a 512x512 animated WebP sticker using FFmpeg.
 * Returns the relative path (e.g., "pack_id/1.webp") to be stored in the database.
 */
fun convertGifToSticker(
    context: Context,
    sourceUri: Uri?,
    packId: Uuid,
    stickerCount: Int,
    onComplete: (String?) -> Unit
) {
    if (sourceUri == null) {
        onComplete(null)
        return
    }

    // 1. Create a temporary file for the input GIF
    val tempInputFile = File(context.cacheDir, "temp_input_${System.currentTimeMillis()}.gif")
    try {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            tempInputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        Log.e("GifToSticker", "Failed to copy GIF to temp file", e)
        onComplete(null)
        return
    }

    // 2. Prepare the output file path
    val baseDir = File(context.filesDir, "packs")
    val packDir = File(baseDir, packId.toString())
    if (!packDir.exists()) packDir.mkdirs()

    val fileName = "${stickerCount + 1}.webp"
    val outputFile = File(packDir, fileName)
    val relativePath = "$packId/$fileName"

    // 3. FFmpeg command
    // scale to 480x480 then pad to 512x512 for a safe 16px margin
    val filter = "scale=480:480:force_original_aspect_ratio=decrease,pad=512:512:(ow-iw)/2:(oh-ih)/2:color=#00000000"
    // -y: Overwrite output file
    // -i: Input path
    // -t 10: Limit duration to 10 seconds (WhatsApp requirement)
    // -vf: Video filters (scale and pad)
    // -loop 0: Infinite loop for animated WebP
    // -vcodec libwebp: Use the WebP encoder
    // -lossless 0: Use lossy compression to stay under the 500KB limit
    // -q:v 50: Quality setting (0-100), reduced to 50 to ensure we stay under 500KB
    // -an: Remove audio track
    val command = "-y -i \"${tempInputFile.absolutePath}\" -t 10 -vf \"$filter\" -loop 0 -vcodec libwebp -lossless 0 -q:v 50 -an \"${outputFile.absolutePath}\""

    Log.d("GifToSticker", "Executing FFmpeg: $command")

    // Check available encoders just once for debugging
    FFmpegKit.executeAsync("-encoders") { session ->
        Log.d("GifToSticker", "Available Encoders:\n${session.allLogsAsString}")
    }

    FFmpegKit.executeAsync(command) { session ->
        val returnCode = session.returnCode
        val isSuccess = ReturnCode.isSuccess(returnCode)
        
        // Clean up temp file
        if (tempInputFile.exists()) {
            tempInputFile.delete()
        }

        if (isSuccess) {
            Log.d("GifToSticker", "Successfully converted GIF to WebP: $relativePath")
            // Check file size (WhatsApp limit is 500KB for animated stickers)
            if (outputFile.length() > 500 * 1024) {
                Log.w("GifToSticker", "File too large: ${outputFile.length()} bytes. Sticker might fail to load.")
            }
            onComplete(relativePath)
        } else {
            Log.e("GifToSticker", "FFmpeg failed with state ${session.state} and return code $returnCode")
            session.logs.forEach { log -> Log.e("GifToSticker", log.message) }
            onComplete(null)
        }
    }
}
