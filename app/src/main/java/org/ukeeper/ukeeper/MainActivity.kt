package org.ukeeper.ukeeper

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import org.ukeeper.ukeeper.db.DataManager
import org.ukeeper.ukeeper.ui.Contacts
import org.ukeeper.ukeeper.ui.DeviceList
import org.ukeeper.ukeeper.ui.SetDevice
import org.ukeeper.ukeeper.ui.theme.UkeeperTheme


class MainActivity : ComponentActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null

        fun get() : MainActivity {
            return instance!!
        }
    }

    sealed class Screen(val route: String, val destination: String) {
        data object Home : Screen("home", "home")
        data object Profile : Screen("profile", "profile")
        data object Analyze : Screen("analyze", "analyze")
        data object ListDevices : Screen("device/scan", "device/scan")
        data object SetDevice : Screen("device/set?address={address}", "device/set")
        data object Contacts : Screen("contacts", "contacts")
    }

    var prd: Predictor? = null

    val appBarConfiguration = setOf(
        Screen.Profile.route,
        Screen.Home.route,
        Screen.Analyze.route
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataManager(applicationContext)
        SocialManager(this, this.getSystemService(SmsManager::class.java))

        enableEdgeToEdge()
        setContent {
            UkeeperTheme (
                darkTheme = false,
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination
                var title by remember { mutableStateOf("U-Keeper") }

                LaunchedEffect(navController.currentBackStackEntryFlow) {
                    navController.currentBackStackEntryFlow.collect {
                        title = (it.destination.route ?: "").split("?")[0]
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
                                    Text(pathToTitle(title), fontSize = TextUnit(8F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W500))
                                }
                            }
                        )


                    },
                    bottomBar = {
                        AnimatedVisibility(
                            appBarConfiguration.contains(currentDestination?.route),
                            enter = EnterTransition.None,
                            exit = ExitTransition.None
                        ) {
                            BottomNavigation(
                                backgroundColor = Color(0xFFFEFEFE)
                            ) {
                                BottomNavigationItem(
                                    icon = {
                                        Icon(
                                            painterResource(R.drawable.graph),
                                            modifier = Modifier.requiredWidth(25.dp),
                                            contentDescription = null,
                                            tint = if(currentDestination?.route == Screen.Analyze.route) Color(0xFF676767) else Color(0xFFBDBDBD)
                                        )
                                    },
                                    selected = currentDestination?.route == Screen.Analyze.route,
                                    onClick = { navController.navigate(Screen.Analyze.route) }
                                )
                                BottomNavigationItem(
                                    icon = {
                                        Icon(
                                            painterResource(R.drawable.home),
                                            modifier = Modifier.requiredWidth(25.dp),
                                            contentDescription = null,
                                            tint = if(currentDestination?.route == Screen.Home.route) Color(0xFF676767) else Color(0xFFBDBDBD)
                                        )
                                    },
                                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                                    onClick = { navController.navigate(Screen.Home.route) }
                                )
                                BottomNavigationItem(
                                    icon = {
                                        Icon(
                                            painterResource(R.drawable.me),
                                            modifier = Modifier.requiredWidth(25.dp),
                                            contentDescription = null,
                                            tint = if(currentDestination?.route == Screen.Profile.route) Color(0xFF676767) else Color(0xFFBDBDBD)
                                        )
                                    },
                                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                                    onClick = { navController.navigate(Screen.Profile.route) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController, startDestination = Screen.Home.route,
                        Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp)
                            .height(IntrinsicSize.Max)
                    ) {
                        composable(Screen.Profile.route) { Profile(navController) }
                        composable(Screen.Home.route) { Home(navController) }
                        composable(Screen.Analyze.route) { Analyze(navController) }
                        composable(Screen.ListDevices.route) { DeviceList(this@MainActivity, applicationContext, navController) }
                        composable(Screen.SetDevice.route, arguments = listOf(
                            navArgument("address") {
                                type = NavType.StringType
                            }
                        )) { SetDevice(this@MainActivity, applicationContext,navController) }
                        composable(Screen.Contacts.route) { Contacts(this@MainActivity, navController) }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(this, KeeperService::class.java).run {
            stopService(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startKeeperService(device: BluetoothDevice, callback: Consumer<Boolean>): Intent {
        Intent(this, KeeperService::class.java).run {
            this.putExtra("device", device.address)
            startService(this)
            callback.accept(true)
            return this
        }
    }
}

fun pathToTitle(path:String): String {
    return when (path) {
        "home" -> "U-Keeper"
        "profile" -> "U-Keeper"
        "analyze" -> "U-Keeper"
        "device/scan" -> "기기 찾기"
        "device/set" -> "기기 설정"
        "contacts" -> "연락처"
        else -> path
    }
}

@Composable
fun Profile(navController: NavHostController) {
    Column {
        ColBox(
            Color(0xFFFEFEFE),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = RoundedCornerShape(16.dp))
        ) {
            Text("정지효님", fontSize = TextUnit(7F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W800))
            Text("총 1개의 기기가 등록되어있어요.", fontSize = TextUnit(4F, TextUnitType.Em), color = Color(0xFF929292))
        }
        Spacer(Modifier.padding(7.dp))
        ColBox(
            Color(0xFFFEFEFE),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = RoundedCornerShape(16.dp))
                .clickable {
                    navController.navigate("device/scan")
                }
        ) {
            Text("등록된 기기", fontSize = TextUnit(7F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W800))
            Text("총 1개의 기기가 등록되어있어요.", fontSize = TextUnit(4F, TextUnitType.Em), color = Color(0xFF929292))
        }
        Spacer(Modifier.padding(7.dp))
        ColBox(
            Color(0xFFFEFEFE),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = RoundedCornerShape(16.dp))
                .clickable {
                    navController.navigate("contacts")
                }
        ) {
            Text("연락처", fontSize = TextUnit(7F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W800))
            Text("총 1개의 연락처가 등록되어있어요.", fontSize = TextUnit(4F, TextUnitType.Em), color = Color(0xFF929292))
        }
        Spacer(Modifier.padding(7.dp))
        ColBox(
            Color(0xFFFFBEBE),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, shape = RoundedCornerShape(16.dp))
        ) {
            Text("로그아웃", fontSize = TextUnit(7F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W800))
            Text("다시 로그인해야해요!", fontSize = TextUnit(4F, TextUnitType.Em), color = Color(0xFF929292))
        }
    }
}

@Composable
fun Home(navController: NavHostController) {

}

@Composable
fun Analyze(navController: NavHostController) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) { modelProducer.runTransaction { lineSeries { series(4, 12, 8, 16) } } }

    Column {
        stat(hour = 7.1f, name = "활동", description = "지난 주 보다 20% ↑", modelProducer = modelProducer, dynamicShader = DynamicShader.verticalGradient(arrayOf(Color(0xff66FF7E), Color(0x0066FF7E))), background = Color(0xFFFEFEFE), text = Color(0xFF37D546))
        Spacer(modifier = Modifier.height(20.dp))
        stat(hour = 6.9f, name = "휴식", description = "지난 주 보다 20% ↑", modelProducer = modelProducer, dynamicShader = DynamicShader.verticalGradient(arrayOf(Color(0xffFF8383), Color(0x00FF8383))), background = Color(0xFFFEFEFE), text = Color(0xFFD5374A))
    }
}

@Composable
fun stat(hour: Float, name: String, description: String, modelProducer: CartesianChartModelProducer, dynamicShader: DynamicShader, background: Color, text: Color) {
    ColBox (
        background,
        modifier = Modifier.shadow(3.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Column (
            Modifier
                .padding(horizontal = 10.dp)
                .padding(bottom = 20.dp)
        ) {
            Row (
                verticalAlignment = Alignment.Bottom
            ) {
                Text("%.1f 시간".format(hour), fontSize = TextUnit(10F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W900, color = text))
                Text(name, Modifier.padding(start = 8.dp, bottom = 3.dp), fontSize = TextUnit(6F, TextUnitType.Em),  style = TextStyle(color = text))
            }
            Text(
                description,
                style = TextStyle(color = Color(0xFF9D9D9D)),
                fontSize = TextUnit(4F, TextUnitType.Em)
            )
        }

        Graph(modelProducer = modelProducer, dynamicShader = dynamicShader)
    }
}

@Composable
fun Graph(modelProducer: CartesianChartModelProducer, dynamicShader: DynamicShader) {
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