package com.example.sticky.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sticky.ui.scene.ImageSelector
import com.example.sticky.ui.scene.StickerPackListScreen
import kotlin.uuid.Uuid

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StickerPackScreen.route) {
        composable(route = Screen.StickerPackScreen.route) {
            StickerPackListScreen(navController = navController)
        }
        composable(
            route = Screen.StickerScreen.route,
            arguments = listOf(navArgument("packId") { type = NavType.StringType })
        ) { backStackEntry ->
            val packIdStr = backStackEntry.arguments?.getString("packId")
            val packId = try { packIdStr?.let { Uuid.parse(it) } } catch (e: Exception) { null }
            ImageSelector(packId = packId)
        }
        composable(Screen.ImagePickerScreen.route) {
        //    CenterHelloWorldScreen(navController)
            // Test navigation
        }
    }
}
