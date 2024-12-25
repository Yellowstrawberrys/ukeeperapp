package org.ukeeper.ukeeper

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.util.Consumer
import org.ukeeper.ukeeper.SocialManager
import org.ukeeper.ukeeper.db.DataManager
import org.ukeeper.ukeeper.kai_morich.simple_bluetooth_le_terminal.SerialListener
import org.ukeeper.ukeeper.kai_morich.simple_bluetooth_le_terminal.SerialSocket
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Calendar
import java.util.Date

class KeeperService : Service() {

    private var mBinder: IBinder = LocalBinder()
    private val dsf = SimpleDateFormat("yyyy-MM-dd")

    var isOk: Boolean = true
    var th:Thread? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        th = Thread {
            Log.e("KeeperService", "Keeper Service is running...");
            val device: BluetoothDevice? = ConnectionHandler(MainActivity.get(), MainActivity.get()).findDevice(intent?.getStringExtra("device")!!)
            SerialSocket(MainActivity.get().baseContext, device)
                .connect(object : SerialListener {
                    override fun onSerialConnect() {
                        Log.e("KeeperService", "Connected to device");
                    }

                    override fun onSerialConnectError(e: Exception?) {}

                    override fun onSerialRead(data: ByteArray?) {
                        val a = data?.decodeToString()!!.split(" ")
                        val date = a[0]
                        val timeD = a[1].split(":")
                        val time = timeD[0].toFloat()*60 + timeD[1].toFloat()
                        println(a)
//                    dbm.write(date, time)

//                    prd?.predict(dbm?.read("2024-09-04")?.toFloatArray()!!)

//                    dbm!!.read("2024-09-04")?.forEach { ele ->
//                        run {
//                            Log.i("FF", ele.toString())
//                        }
//                    }
                    }

                    override fun onSerialRead(datas: ArrayDeque<ByteArray>?) {}

                    override fun onSerialIoError(e: Exception?) {}
                })
//            while (isOk) {
////                SocialManager.get().broadcastWarningMessage(
////                DataManager.get(),
////                "정지효님씨가 위험에 처했습니다.\n" +
////                        "전화를 해보시는건 어떠신가요?"
////            )
//                Thread.sleep(1000*60)
//            }
        }

        th?.start()

        return START_STICKY
    }

    private fun getCurrentMin(): Float {
        val calendar: Calendar = Calendar.getInstance()
        return (calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE)).toFloat()
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        isOk = false
        th?.interrupt()
        super.onDestroy()
    }

    class LocalBinder : Binder() {
        fun getServerInstance(): KeeperService {
            return KeeperService()
        }
    }
}