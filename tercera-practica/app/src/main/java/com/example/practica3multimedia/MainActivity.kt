package com.example.practica3multimedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practica3multimedia.data.local.database.AppDatabase
import com.example.practica3multimedia.ui.screens.AddGastoScreen
import com.example.practica3multimedia.ui.screens.HomeScreen
import com.example.practica3multimedia.ui.theme.BudgetBuddyTheme
import com.example.practica3multimedia.viewmodel.GastoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GastoViewModel(database.gastoDao()) as T
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[GastoViewModel::class.java]

        setContent {
            BudgetBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable(
                            route = "home",
                            exitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500))
                            },
                            popEnterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500))
                            }
                        ) {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToAdd = { gastoId ->
                                    navController.navigate("add/$gastoId")
                                }
                            )
                        }

                        composable(
                            route = "add/{gastoId}",
                            arguments = listOf(navArgument("gastoId") { type = NavType.IntType }),
                            enterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500))
                            },
                            popExitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500))
                            }
                        ) { backStackEntry ->
                            val gastoId = backStackEntry.arguments?.getInt("gastoId") ?: -1
                            AddGastoScreen(navController, viewModel, gastoId)
                        }
                    }
                }
            }
        }
    }
}