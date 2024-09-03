package org.ukeeper.ukeeper.ui

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import org.ukeeper.ukeeper.ColBox
import org.ukeeper.ukeeper.R
import org.ukeeper.ukeeper.animatedBorder

@Composable
public fun DeviceList(navController: NavHostController) {
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
    Row (
        Modifier
            .padding(vertical = 50.dp, horizontal = 0.dp)
            .fillMaxSize()
    ) {
        ColBox(
            background = Color(0xFF1F1F1F),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, Modifier.width(45.dp).height(35.dp).padding(end = 10.dp), tint = Color.White)
                Text("U-Door", fontSize = TextUnit(9F, TextUnitType.Em), fontWeight = FontWeight.W500)
                Text("(v1)", modifier = Modifier.padding(start=5.dp), color = Color(0xFF9E9E9E))
            }
            Spacer(
                Modifier.padding(vertical = 20.dp)
            )
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text("Click to initialize", color = Color(0xFF29E5FF))
            }
        }
    }
}

@Composable
fun SetDevice(navController: NavHostController) {
    ColBox(
        background = Color(0xFF1F1F1F),
        Modifier.fillMaxWidth()
    ) {
        val configuration = LocalConfiguration.current
        Column (Modifier.fillMaxWidth().height(configuration.screenHeightDp.dp - (configuration.screenHeightDp*0.3).dp)) {
            Column (
                Modifier.fillMaxWidth()
                    .padding(vertical = 50.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(painter = painterResource(R.drawable.udoor), contentDescription = null, Modifier.width(100.dp).height(100.dp))
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