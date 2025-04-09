package com.example.focustimer
import MainPage
import android.content.BroadcastReceiver
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth


val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun MyBottomNavi(navController: NavHostController = rememberNavController()) {

    val user = FirebaseAuth.getInstance().currentUser
    val startPage = if(user != null) "main" else "signin"
    CompositionLocalProvider(LocalNavController provides navController) {
        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route

        Scaffold(
            bottomBar ={
                if(currentRoute in listOf("main","history" ))
                {
                    NavigationBar (
                        containerColor = colorResource(R.color.myGray)
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                            label = { Text("Main") },
                            selected = currentDestination?.hierarchy?.any { it.route == "main" } == true,
                            onClick = {
                                navController.navigate("main") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true

                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(R.color.black),
                                unselectedIconColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.List, contentDescription = "History") },
                            label = { Text("History") },
                            selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                            onClick = {
                                navController.navigate("history") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(R.color.black),
                                unselectedIconColor = Color.Gray
                            )
                        )
                    }


                }
                else null
            }

        ) { innerPadding ->

            NavHost(navController, startDestination = startPage, Modifier.padding(innerPadding)) {
                composable("main") { MainPage() }
                composable("history") {
                    weekHistoryApp() }
                composable("signup") { signupPage() }
                composable("signin") { LoginScreen() }
                composable("timer") { DualStopwatchApp() }

            }
        }
    }

}


