package org.ukeeper.ukeeper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import no.nordicsemi.android.ble.BleManager
import org.ukeeper.ukeeper.db.DataManager
import org.ukeeper.ukeeper.kai_morich.simple_bluetooth_le_terminal.SerialListener
import org.ukeeper.ukeeper.kai_morich.simple_bluetooth_le_terminal.SerialSocket
import java.lang.Exception
import java.util.ArrayDeque
import java.util.UUID


class ConnectionHandler(private val activity: MainActivity, private val context: Context) : BleManager(context) {

    private val bleAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter;
    private val bleScanner: BluetoothLeScanner? = bleAdapter?.bluetoothLeScanner;
    private val handler: Handler = Handler(Looper.getMainLooper());
    private val service: UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    private val uuid: UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");

    private var scanning: Boolean = false


    @RequiresApi(Build.VERSION_CODES.S)
    public fun requestPermission(): Boolean {
        val permissions = arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        );
        if (permissions.any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(activity, permissions, 1)

            return permissions.any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    public fun scanDevices(scanned: MutableList<BluetoothDevice>) {
        if (!scanning && requestPermission()) {

            val filter = ScanFilter.Builder().build()
            val filters = listOf(filter)

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setReportDelay(0)
                .build()

            val leScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    if (result != null && result.device != null && result.device.name != null && result.device.name.startsWith(
                            "U-Keeper"
                        )
                    ) {
                        if (!scanned.contains(result.device!!)) scanned.add(result.device!!)
                    }
                    super.onScanResult(callbackType, result)
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.v("BLE", "ERR")
                    Log.v("BLE", errorCode.toString())
                    super.onScanFailed(errorCode)
                }
            }
            handler.postDelayed({
                Log.v("BLE", "SCAN STOPPED")
                bleScanner?.stopScan(leScanCallback)
                scanning = false
            }, 2000)
            bleScanner?.startScan(filters, settings, leScanCallback)
            scanning = true
            Log.v("BLE", "SCAN STARTED")
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    public fun findDevice(id: String, callback: ScanCallback) {
        if (requestPermission()) {
            val filter = ScanFilter.Builder().setDeviceAddress(id).build()
            val filters = listOf(filter)

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setReportDelay(0)
                .build()

            handler.postDelayed({
                Log.v("BLE", "SCAN STOPPED")
                bleScanner?.stopScan(callback)
            }, 2000)
            bleScanner?.startScan(filters, settings, callback);
            Log.v("BLE", "SCAN STARTED")
        }
    }


    @SuppressLint("MissingPermission")
    public fun read(scm: SocialManager, dbm: DataManager, bd: BluetoothDevice, navHostController: NavHostController) {
        SerialSocket(context, bd)
            .connect(object : SerialListener {
                override fun onSerialConnect() {
                    navHostController.navigate("/home")
//                    TODO("Not yet implemented")
                }

                override fun onSerialConnectError(e: Exception?) {
//                    TODO("Not yet implemented")
                }

                override fun onSerialRead(data: ByteArray?) {
                    val a = data?.decodeToString()!!.split(" ")
                    val date = a[0]
                    val timeD = a[1].split(":")
                    val time = timeD[0].toFloat()*60 + timeD[1].toFloat()
                    dbm.write(date, time)

//                    prd?.predict(dbm?.read("2024-09-04")?.toFloatArray()!!)

//                    dbm!!.read("2024-09-04")?.forEach { ele ->
//                        run {
//                            Log.i("FF", ele.toString())
//                        }
//                    }
                }

                override fun onSerialRead(datas: ArrayDeque<ByteArray>?) {
//                    TODO("Not yet implemented")
                }

                override fun onSerialIoError(e: Exception?) {
//                    TODO("Not yet implemented")
                }

            })
    }
}