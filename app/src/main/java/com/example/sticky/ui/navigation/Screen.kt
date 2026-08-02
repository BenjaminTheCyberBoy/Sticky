package com.example.sticky.ui.navigation

import kotlin.uuid.Uuid

sealed class Screen(val route: String) {
    object StickerPackScreen : Screen("sticker_pack")
    object StickerScreen : Screen("sticker/{packId}") {
        fun createRoute(packId: Uuid) = "sticker/$packId"
    }
    object ImagePickerScreen : Screen("image_picker")
}
