//package org.ukeeper.ukeeper
//
//import android.app.Service
//import android.bluetooth.BluetoothGattCharacteristic
//import android.content.Intent
//import android.os.IBinder
//
//class KeeperService : Service() {
//    override fun onBind(p0: Intent?): IBinder? {
////        TODO("Not yet implemented")
//
//        return null
//    }
//
//    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
//        bluetoothGatt?.let { gatt ->
//            gatt.readCharacteristic(characteristic)
//        } ?: run {
//            Log.w(TAG, "BluetoothGatt not initialized")
//            Return
//        }
//    }
//
//}