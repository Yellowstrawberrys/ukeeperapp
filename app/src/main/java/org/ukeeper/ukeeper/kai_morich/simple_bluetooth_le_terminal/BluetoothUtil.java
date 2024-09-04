package org.ukeeper.ukeeper.kai_morich.simple_bluetooth_le_terminal;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;


public class BluetoothUtil {

    interface PermissionGrantedCallback {
        void call();
    }

    /*
     * more efficient caching of name than BluetoothDevice which always does RPC
     */
    static class Device implements Comparable<Device> {
        BluetoothDevice device;
        String name;

        @SuppressLint("MissingPermission")
        public Device(BluetoothDevice device) {
            this.device = device;
            this.name = device.getName();
        }

        public BluetoothDevice getDevice() { return device; }
        public String getName() { return name; }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Device)
                return device.equals(((Device) o).device);
            return false;
        }

        /**
         * sort by name, then address. sort named devices first
         */
        @Override
        public int compareTo(Device other) {
            boolean thisValid = this.name!=null && !this.name.isEmpty();
            boolean otherValid = other.name!=null && !other.name.isEmpty();
            if(thisValid && otherValid) {
                int ret = this.name.compareTo(other.name);
                if (ret != 0) return ret;
                return this.device.getAddress().compareTo(other.device.getAddress());
            }
            if(thisValid) return -1;
            if(otherValid) return +1;
            return this.device.getAddress().compareTo(other.device.getAddress());
        }

    }

}
