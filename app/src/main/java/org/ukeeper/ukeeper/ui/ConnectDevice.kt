package org.ukeeper.ukeeper.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.ukeeper.ukeeper.ColBox
import org.ukeeper.ukeeper.ConnectionHandler
import org.ukeeper.ukeeper.MainActivity
import org.ukeeper.ukeeper.R
import org.ukeeper.ukeeper.animatedBorder
import org.ukeeper.ukeeper.db.DataManager
import java.net.URLEncoder

@RequiresApi(Build.VERSION_CODES.S)
@Composable
public fun DeviceList(activity: MainActivity, context: Context, navController: NavHostController) {
    val con:ConnectionHandler = ConnectionHandler(activity, context)
    val scanned = remember { mutableStateListOf<BluetoothDevice>() };
    con.scanDevices(scanned)

    Row (
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("탐색된 기기")
        Row {
           Text("⟳ 새로고침", style = TextStyle(color = Color(0xFF8B8B8B)))
        }
    }
    Column (
        Modifier
            .padding(vertical = 50.dp, horizontal = 0.dp)
            .fillMaxSize()
    ) {
        for (device:BluetoothDevice in scanned) {
            Spacer(Modifier.padding(15.dp))
            ColBox(
                background = Color(0xFF1F1F1F),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        run {
                            navController.navigate("device/set?id=${URLEncoder.encode(device.address)}")
                        }
                    }
            ) {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null,
                        Modifier
                            .width(45.dp)
                            .height(35.dp)
                            .padding(end = 10.dp), tint = Color.White)
                    Text(deviceToName(device), fontSize = TextUnit(9F, TextUnitType.Em), fontWeight = FontWeight.W500)
                    Text("(v1)", modifier = Modifier.padding(start=5.dp), color = Color(0xFF9E9E9E))
                }
                Spacer(
                    Modifier.padding(vertical = 20.dp)
                )
                Row (
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text("Click to initialize", color = Color(0xFF29E5FF))
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun deviceToName(device: BluetoothDevice): String {
    return if(device.name.startsWith("U-Keeper_D")) "U-Door";
    else if(device.name.startsWith("U-Keeper_B")) "U-Bed";
    else "알 수 없음";
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SetDevice(dbm:DataManager, activity: MainActivity, context: Context, navController: NavHostController) {
    val con:ConnectionHandler = ConnectionHandler(activity, context)
    navController.currentBackStackEntry?.arguments?.keySet()?.forEachIndexed { index, s -> run {
        Log.v(
            "BLE",
            s + " = " + navController.currentBackStackEntry?.arguments?.get(s)!!
        )

    } }
    println()
    val l = navController.currentBackStackEntry?.arguments?.getString("id")!!
    val leScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.v("BLE", "CALLBACK")
            Log.v("BLE", callbackType.toString())
            Log.v("BLE", result?.device?.name.toString())

            con.read(dbm, result?.device!!)
            super.onScanResult(callbackType, result)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.v("BLE", "ERR")
            Log.v("BLE", errorCode.toString())
            super.onScanFailed(errorCode)
        }
    }

    con.findDevice(l, leScanCallback)

    ColBox(
        background = Color(0xFF1F1F1F),
        Modifier.fillMaxWidth()
    ) {
        val configuration = LocalConfiguration.current
        Column (
            Modifier
                .fillMaxWidth()
                .height(configuration.screenHeightDp.dp - (configuration.screenHeightDp * 0.3).dp)) {
            Column (
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(painter = painterResource(R.drawable.udoor), contentDescription = null,
                    Modifier
                        .width(100.dp)
                        .height(100.dp))
                Spacer(Modifier.padding(5.dp))
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ) {
                    Text("U-Door", fontSize = TextUnit(12F, TextUnitType.Em), fontWeight = FontWeight.W500)
                    Text("(v1)", modifier = Modifier.padding(start=5.dp), color = Color(0xFF9E9E9E))
                }
            }
            Spacer(Modifier.padding((configuration.screenHeightDp*0.025).dp))
            Column (
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier
                        .animatedBorder(
                            listOf(
                                Color(0xFF1F1F1F),
                                Color(0xFF4CD4FF)
                            ),
                            borderWidth = 6.dp,
                            backgroundColor = Color(0xFF1F1F1F)
                        )
                        .width(100.dp)
                        .height(100.dp),
                    color = Color(0x00000000),
                )
                Spacer(Modifier.padding(10.dp))
                Text("기기와 통신중", fontSize = TextUnit(6.5F, TextUnitType.Em), color = Color(0xFFC3C3C3))
            }
        }
    }
}