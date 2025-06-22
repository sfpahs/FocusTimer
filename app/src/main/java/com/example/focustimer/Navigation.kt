package com.example.focustimer
import com.example.focustimer.Page.main.MainPage
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focustimer.Page.timer.DualStopwatchApp
import com.example.focustimer.Page.LoginScreen
import com.example.focustimer.Page.signupPage
import com.example.focustimer.Page.TodoPage
import com.example.focustimer.survery.SurveyCronotypeScreen
import com.example.focustimer.Page.main.ExplanationPager
import com.example.focustimer.Page.Date.ScheduleContainerPage
import com.example.focustimer.Page.timer.ChangeTimerOption
import com.example.focustimer.Page.main.EditBoxScreen
import com.example.focustimer.test.SurveyMstiScreen
import com.example.focustimer.utils.AppRoute
import com.google.firebase.auth.FirebaseAuth


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
                if(currentRoute in listOf(AppRoute.MAIN.route,AppRoute.DATE.route, AppRoute.TODO.route))
                {
                    NavigationBar (
                        containerColor = colorResource(R.color.myGray)
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        //아이콘 , 텍스트 루트,
                        NavigationBarItem(
                            icon = { Icon(imageVector =  Icons.Filled.DateRange, contentDescription = "History") },
                            label = { Text("기록") },
                            selected = currentDestination?.hierarchy?.any { it.route == AppRoute.DATE.route } == true,
                            onClick = {
                                navController.navigate(AppRoute.DATE.route) {
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
                            icon = { Icon(imageVector =  ImageVector.vectorResource(R.drawable.ic_clock), contentDescription = "Home") },
                            label = { Text("타이머") },
                            selected = currentDestination?.hierarchy?.any { it.route == AppRoute.MAIN.route } == true,
                            onClick = {
                                navController.navigate(AppRoute.MAIN.route) {
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
                            icon = { Icon(Icons.Filled.List, contentDescription = "TODO") },
                            label = { Text("Todo") },
                            selected = currentDestination?.hierarchy?.any { it.route == AppRoute.TODO.route } == true,
                            onClick = {
                                navController.navigate("todo") {
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
                composable(AppRoute.CHANGE_OPTION.route) { ChangeTimerOption() }
                composable(AppRoute.MAIN.route) { MainPage() }
                composable(AppRoute.DATE.route) { ScheduleContainerPage() }
                composable(AppRoute.SURVEY.route) { SurveyCronotypeScreen() }
                composable(AppRoute.TODO.route) { TodoPage() }
                composable(AppRoute.SIGNUP.route) { signupPage() }
                composable(AppRoute.SIGNIN.route) { LoginScreen() }
                composable(AppRoute.TIMER.route) { DualStopwatchApp() }
                composable(AppRoute.EXPLANATION.route) { ExplanationPager() }
                composable(AppRoute.EDIT.route) { EditBoxScreen() }
                composable(AppRoute.MSTI.route) { SurveyMstiScreen() }
            }
        }
    }

}

