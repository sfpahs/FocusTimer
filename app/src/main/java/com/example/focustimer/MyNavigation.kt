package com.example.focustimer
import com.example.focustimer.Page.MainPage
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focustimer.Page.DualStopwatchApp
import com.example.focustimer.Page.LoginScreen
import com.example.focustimer.Page.signupPage
import com.example.focustimer.Page.weekHistoryApp
import com.example.focustimer.survery.SurveyScreen
import com.example.focustimer.test.ExplanationPager
import com.example.focustimer.test.ScheduleContainerPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


val LocalNavController = compositionLocalOf<NavHostController> { error("No NavController found!") }


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun MyBottomNavi(navController: NavHostController = rememberNavController()) {

    val user = FirebaseAuth.getInstance().currentUser
    //FirebaseAuth.getInstance().signOut()
    val startPage = if(user != null) "main" else "signin"
    CompositionLocalProvider(LocalNavController provides navController) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Scaffold(
            bottomBar ={
                if(currentRoute in listOf("main","history", "date"))
                {
                    NavigationBar (
                        containerColor = colorResource(R.color.myGray)
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        //아이콘 , 텍스트 루트,
                        NavigationBarItem(
                            icon = { Icon(imageVector =  Icons.Filled.DateRange, contentDescription = "Date") },
                            label = { Text("Date") },
                            selected = currentDestination?.hierarchy?.any { it.route == "date" } == true,
                            onClick = {
                                navController.navigate("date") {
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
                            icon = { Icon(imageVector =  Icons.Filled.Home, contentDescription = "Home") },
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
                composable("date") { ScheduleContainerPage() }
                composable("survey") { SurveyScreen() }
                composable("history") { weekHistoryApp() }
                composable("signup") { signupPage() }
                composable("signin") { LoginScreen() }
                composable("timer") { DualStopwatchApp() }
                composable("explanation"){ExplanationPager()}

            }
        }
    }

}

