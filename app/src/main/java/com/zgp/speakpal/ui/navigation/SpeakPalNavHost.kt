package com.zgp.speakpal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.zgp.speakpal.ui.screens.HomeScreen
import com.zgp.speakpal.ui.screens.PracticeScreen
import com.zgp.speakpal.ui.screens.RecordsScreen
import com.zgp.speakpal.ui.screens.ResultScreen
import com.zgp.speakpal.ui.screens.WordListScreen

@Composable
fun SpeakPalNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
    ) {
        composable(Routes.Home) {
            HomeScreen(
                onStartPractice = { navController.navigate(Routes.Words) },
                onViewRecords = { navController.navigate(Routes.Records) },
            )
        }
        composable(Routes.Words) {
            WordListScreen(
                onBack = { navController.popBackStack() },
                onWordSelected = { wordId -> navController.navigate(Routes.practice(wordId)) },
            )
        }
        composable(
            route = Routes.PracticeWithWord,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType }),
        ) { entry ->
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
            RecordsScreen(onBack = { navController.popBackStack() })
        }
    }
}
