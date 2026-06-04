package com.zgp.speakpal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zgp.speakpal.ui.screens.HomeScreen
import com.zgp.speakpal.ui.screens.PracticeScreen
import com.zgp.speakpal.ui.screens.ProfileScreen
import com.zgp.speakpal.ui.screens.RecordsScreen
import com.zgp.speakpal.ui.screens.ResultScreen
import com.zgp.speakpal.ui.screens.WordListScreen

@Composable
fun SpeakPalNavHost(navController: NavHostController) {
    // 应用主导航集中管理页面入口和参数传递。
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
    ) {
        composable(Routes.Home) {
            HomeScreen(
                onStartPractice = { navController.navigateTopLevel(Routes.Words) },
                onViewRecords = { navController.navigateTopLevel(Routes.Records) },
                onOpenProfile = { navController.navigateTopLevel(Routes.Profile) },
            )
        }
        composable(Routes.Words) {
            WordListScreen(
                onBack = { navController.popBackStack() },
                onWordSelected = { wordId -> navController.navigate(Routes.practice(wordId)) },
                onOpenHome = { navController.navigateTopLevel(Routes.Home) },
                onOpenRecords = { navController.navigateTopLevel(Routes.Records) },
                onOpenProfile = { navController.navigateTopLevel(Routes.Profile) },
            )
        }
        composable(
            route = Routes.PracticeWithWord,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType }),
        ) { entry ->
            // 练习页通过单词 ID 读取本地示例单词。
            val wordId = entry.arguments?.getString("wordId").orEmpty()
            PracticeScreen(
                wordId = wordId,
                onBack = { navController.popBackStack() },
                onSubmit = { score -> navController.navigate(Routes.result(wordId, score)) },
            )
        }
        composable(
            route = Routes.ResultWithArgs,
            arguments = listOf(
                navArgument("wordId") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType },
            ),
        ) { entry ->
            // 结果页暂用路由参数承载模拟评分结果。
            val wordId = entry.arguments?.getString("wordId").orEmpty()
            val score = entry.arguments?.getInt("score") ?: 0
            ResultScreen(
                wordId = wordId,
                score = score,
                onBackToPractice = { navController.popBackStack() },
                onViewRecords = { navController.navigate(Routes.Records) },
            )
        }
        composable(Routes.Records) {
            RecordsScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = { navController.navigateTopLevel(Routes.Home) },
                onOpenTraining = { navController.navigateTopLevel(Routes.Words) },
                onOpenProfile = { navController.navigateTopLevel(Routes.Profile) },
            )
        }
        composable(Routes.Profile) {
            ProfileScreen(
                onOpenHome = { navController.navigateTopLevel(Routes.Home) },
                onOpenTraining = { navController.navigateTopLevel(Routes.Words) },
                onOpenRecords = { navController.navigateTopLevel(Routes.Records) },
            )
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    // 底部导航使用单实例跳转，避免重复点击堆叠同一页面。
    navigate(route) {
        popUpTo(Routes.Home) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
