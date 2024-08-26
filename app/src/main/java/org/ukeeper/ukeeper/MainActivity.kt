package org.ukeeper.ukeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import org.ukeeper.ukeeper.ui.theme.UkeeperTheme


class MainActivity : ComponentActivity() {
    sealed class Screen(val route: String, val destination: String) {
        data object Home : Screen("home", "/home")
        data object Profile : Screen("profile", "/profile")
        data object Analyze : Screen("analyze", "/analyze")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UkeeperTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                                selected = currentDestination?.hierarchy?.any { it.route == "analyze" } == true,
                                onClick = { navController.navigate(Screen.Analyze.route) }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                                selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                                onClick = { navController.navigate(Screen.Home.route) }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                                selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                                onClick = { navController.navigate(Screen.Profile.route) }
                            )
                        }
                    }

                ) { innerPadding ->
                    NavHost(navController, startDestination = Screen.Profile.route, Modifier.padding(innerPadding)) {
                        composable(Screen.Profile.route) { Profile(navController) }
                        composable(Screen.Home.route) { Home(navController) }
                        composable(Screen.Analyze.route) { Analyze(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun Profile(navController: NavHostController) {
//    Text(text = "Profile")
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) { modelProducer.runTransaction { lineSeries { series(4, 12, 8, 16) } } }
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        ),
        modelProducer,
    )
}

@Composable
fun Home(navController: NavHostController) {
    Text(text = "Home")
}

@Composable
fun Analyze(navController: NavHostController) {
    Text(text = "Analyze")
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    UkeeperTheme {
//        Greeting("Android")
//    }
//}