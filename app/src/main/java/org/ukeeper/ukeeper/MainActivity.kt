package org.ukeeper.ukeeper

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import org.ukeeper.ukeeper.ui.DeviceList
import org.ukeeper.ukeeper.ui.SetDevice
import org.ukeeper.ukeeper.ui.theme.UkeeperTheme


class MainActivity : ComponentActivity() {
    sealed class Screen(val route: String, val destination: String) {
        data object Home : Screen("home", "/home")
        data object Profile : Screen("profile", "/profile")
        data object Analyze : Screen("analyze", "/analyze")
        data object ListDevices : Screen("device/scan", "/device/scan")
        data object SetDevice : Screen("device/set", "/device/set")
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConnectionHandler(this, applicationContext).scanDevices()
        enableEdgeToEdge()
        setContent {
            UkeeperTheme {
                val navController = rememberNavController()
                var title by remember { mutableStateOf("U-Keeper") }

                LaunchedEffect(navController.currentBackStackEntryFlow) {
                    navController.currentBackStackEntryFlow.collect {
                        title = it.destination.route ?: ""
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column(
                                    Modifier.padding(
                                        vertical = 40.dp
                                    )
                                ) {
                                    Log.v("NAV", title);
                                    Text(pathToTitle(title), fontSize = TextUnit(8F, TextUnitType.Em))
                                }
                            }
                        )


                    },
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = Color.Black
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White) },
                                selected = currentDestination?.hierarchy?.any { it.route == "analyze" } == true,
                                onClick = { navController.navigate(Screen.Analyze.route) }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = Color.White) },
                                selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                                onClick = { navController.navigate(Screen.Home.route) }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White) },
                                selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                                onClick = { navController.navigate(Screen.Profile.route) }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController, startDestination = Screen.SetDevice.route,
                        Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp)
                            .height(IntrinsicSize.Max)
                            .background(Color(0xFF121212))
                    ) {
                        composable(Screen.Profile.route) { Profile(navController) }
                        composable(Screen.Home.route) { Home(navController) }
                        composable(Screen.Analyze.route) { Analyze(navController) }
                        composable(Screen.ListDevices.route) { DeviceList(navController) }
                        composable(Screen.SetDevice.route) { SetDevice(navController) }
                    }
                }
            }
        }
    }
}

fun pathToTitle(path:String): String {
    return when (path) {
        "home" -> "U-Keeper"
        "profile" -> "사용자 정보"
        "analyze" -> "분석"
        "device/scan" -> "기기 찾기"
        "device/set" -> "기기 설정"
        else -> path
    }
}

@Composable
fun Profile(navController: NavHostController) {
    Column {
        ColBox(
            Color.Black,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("정은수님", fontSize = TextUnit(10F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W900, color = Color.White))
        }
    }
}

@Composable
fun Home(navController: NavHostController) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) { modelProducer.runTransaction { lineSeries { series(4, 12, 8, 16) } } }


    Column {
        stat(hour = 7.1f, name = "활동", description = "지난 주 보다 20% ↑", modelProducer = modelProducer, dynamicShader = DynamicShader.verticalGradient(arrayOf(Color(0xff66FF7E), Color(0x0066FF7E))), background = Color(0xff00BB1E))
        Spacer(modifier = Modifier.height(20.dp))
        stat(hour = 6.9f, name = "휴식", description = "지난 주 보다 20% ↑", modelProducer = modelProducer, dynamicShader = DynamicShader.verticalGradient(arrayOf(Color(0xffFF8383), Color(0x00FF8383))), background = Color(0xffD5374A))
    }
}

@Composable
fun stat(hour: Float, name: String, description: String, modelProducer: CartesianChartModelProducer, dynamicShader: DynamicShader, background: Color) {
    ColBox (
        background
    ) {
        Column (
            Modifier
                .padding(horizontal = 10.dp)
                .padding(bottom = 20.dp)
        ) {
            Row (
                verticalAlignment = Alignment.Bottom
            ) {
                Text("%.1f 시간".format(hour), fontSize = TextUnit(10F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W900, color = Color.Black))
                Text(name, Modifier.padding(start = 8.dp, bottom = 3.dp), fontSize = TextUnit(6F, TextUnitType.Em),  style = TextStyle(color = Color.Black))
            }
            Text(description, style = TextStyle(color = Color.Black), fontSize = TextUnit(4F, TextUnitType.Em),)
        }

        graph(modelProducer = modelProducer, dynamicShader = dynamicShader)
    }
}

@Composable
fun graph(modelProducer: CartesianChartModelProducer, dynamicShader: DynamicShader) {
    return CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill.Transparent),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill(
                                dynamicShader
                            )
                        )
                    )
                )
            )
        ),
        modelProducer,
    )
}

@Composable
fun Analyze(navController: NavHostController) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                series(4, 12, 8, 16)
                series(4, 12, 8, 16)
            }
        }
    }

    ColBox (
        background = Color.Black,
        Modifier
            .height(IntrinsicSize.Max)
    ){
        TextRow("활동")
        TextRow("휴식")
        val configuration = LocalConfiguration.current
        CartesianChartHost(
            rememberCartesianChart(
                rememberColumnCartesianLayer()
            ),
            modelProducer,
            modifier = Modifier
                .padding(top=(configuration.screenHeightDp*0.1).dp)
                .height(configuration.screenHeightDp.dp - (configuration.screenHeightDp*0.45).dp)
        )
    }
}

@Composable
fun TextRow(name:String) {
    Row (
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(horizontal = 15.dp)
    ) {
        Text("00시간 00분 00초", fontSize = TextUnit(9F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W900, color = Color.White))
        Text(name, Modifier.padding(start = 8.dp, bottom = 3.dp), fontSize = TextUnit(6F, TextUnitType.Em),  style = TextStyle(color = Color.White))
    }
}