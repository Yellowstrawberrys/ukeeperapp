package org.ukeeper.ukeeper.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import org.ukeeper.ukeeper.ColBox
import org.ukeeper.ukeeper.db.DataManager

@SuppressLint("UnrememberedMutableState")
@Composable
public fun Contacts(db: DataManager, navHostController: NavHostController) {
    var contacts = mutableStateOf(db.getContacts())
    val openAlertDialog = remember { mutableIntStateOf(0) }
    var clicked = remember { mutableStateOf("") };

    when {
        openAlertDialog.intValue == 1 -> {
            ReallyDialog(onDismissRequest = {
                clicked.value = ""
                openAlertDialog.intValue = 0
            }, onConfirmation = {
                db.removeContact(clicked.value);
                clicked.value = ""
                openAlertDialog.intValue = 0
                contacts.value = db.getContacts()
            }, clicked.value)

        }
        openAlertDialog.intValue == 2 -> {
            AddContact(onDismissRequest = {
                clicked.value = ""
                openAlertDialog.intValue = 0
            }, onConfirmation = {
                    a ->
                run {
                    db.addContact(a[0].trim(), a[1].trim());
                    clicked.value = ""
                    openAlertDialog.intValue = 0
                    contacts.value = db.getContacts()
                }
            })
        }
    }

    Column (Modifier.fillMaxWidth()) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("저장된 연락처")
            Text("+ 추가하기", color = Color(0xff8B8B8B), modifier = Modifier.clickable {
                openAlertDialog.intValue = 2
            })
        }

        Spacer(Modifier.padding(5.dp))
        for(a in contacts.value) {
            Spacer(Modifier.padding(8.dp))
            ColBox(background = Color(0xFF1F1F1F), Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(a[0], fontSize = TextUnit(7F, TextUnitType.Em), style = TextStyle(fontWeight = FontWeight.W700))
                        Text(a[1], color = Color(0xffB5B5B5), modifier = Modifier.padding(start = 10.dp))
                    }
                    Icon(
                        Icons.Outlined.Close,
                        "",
                        tint = Color(0xffC65E5E),
                        modifier = Modifier.clickable {
                            clicked.value = a[0]
                            openAlertDialog.intValue = 1
                        })
                }
            }
        }
    }
}

@Composable
fun ReallyDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    name:String
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "정말로 '%s' 연락처를 삭제하시겠습니까?".format(name),
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("취소")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}

@Composable
fun AddContact(
    onDismissRequest: () -> Unit,
    onConfirmation: (Array<String>) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            var name by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "연락처 추가".format(name),
                    modifier = Modifier.padding(16.dp),
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    Modifier.fillMaxWidth()
                )
                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("취소")
                    }
                    TextButton(
                        onClick = { onConfirmation(arrayOf(name, phone)) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}