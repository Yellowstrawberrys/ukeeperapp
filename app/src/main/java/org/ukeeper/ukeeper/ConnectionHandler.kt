package org.ukeeper.ukeeper

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import no.nordicsemi.android.ble.BleManager


class ConnectionHandler(activity: MainActivity, context:Context) : BleManager(context) {

    private val activity:Activity = activity;
    private val bleAdapter:BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter;
    private val bleScanner:BluetoothLeScanner? = bleAdapter?.bluetoothLeScanner;
    private val handler: Handler = Handler(Looper.getMainLooper());
    private val scanned: Set<BluetoothDevice> = HashSet();

    @RequiresApi(Build.VERSION_CODES.S)
    public fun requestPermission():Boolean {
        val permissions = arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        );
        if(permissions.any {
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
    public fun scanDevices() {
        if(requestPermission()) {

            val filter = ScanFilter.Builder().build()
            val filters = listOf(filter)

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setReportDelay(0)
                .build()

            val leScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    Log.v("BLE", "CALLBACK")
                    Log.v("BLE", callbackType.toString())
                    Log.v("BLE", result?.device?.name.toString())
                    if(result != null && result.device != null && result.device.name != null && result.device.name.startsWith("UKeeper")) {
                        scanned.plus(result.device!!)
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
            }, 5000)
            bleScanner?.startScan(filters, settings, leScanCallback)
            Log.v("BLE", "SCAN STARTED")
        }
    }
}