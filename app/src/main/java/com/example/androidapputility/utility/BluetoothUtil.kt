package com.example.androidapputility.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothUtil {

    companion object {

        fun isBluetoothSupported(): Boolean {
            return getBluetoothAdapter() != null
        }

        fun isBluetoothEnabled(): Boolean? {
            return getBluetoothAdapter()?.isEnabled ?: null
        }

        @SuppressLint("MissingPermission")
        fun getBluetoothName(context: Context): String? {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return null
            return getBluetoothAdapter()?.name
        }

        @SuppressLint("MissingPermission")
        fun enableBluetooth(context: Activity, requestCode: Int) {
            getBluetoothAdapter()?.let {
                if (!it.isEnabled) {
                    if (!PermissionUtil.hadBluetoothConnectPermission(context))
                        return
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    context.startActivityForResult(enableBtIntent, requestCode)
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun disableBluetooth(context: Context) {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return

            getBluetoothAdapter()?.let {
                if (it.isEnabled) {
                    it.disable()
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun startBluetoothDiscovery(context: Context) {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return

            getBluetoothAdapter()?.let {
                if (it.isEnabled) {
                    if (it.isDiscovering)
                        it.cancelDiscovery()
                    it.startDiscovery()

                    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                    context.registerReceiver(object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            val action = intent.action
                            if (BluetoothDevice.ACTION_FOUND == action) {
                                val device: BluetoothDevice? =
                                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                device?.let {
                                    if (!PermissionUtil.hadBluetoothConnectPermission(context))
                                        return
                                    val foundDevice = Pair<String, String>(it.name, it.address)
                                }
                            }
                        }
                    }, filter)
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun stopBluetoothDiscovery(context: Context, receiver: BroadcastReceiver) {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return

            getBluetoothAdapter()?.let {
                if (it.isDiscovering) {
                    it.cancelDiscovery()
                    context.unregisterReceiver(receiver)
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun startBluetoothLeScan(context: Context) {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return

            getBluetoothAdapter()?.let {
                it.bluetoothLeScanner?.startScan(object : ScanCallback() {
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        if (!PermissionUtil.hadBluetoothConnectPermission(context))
                            return
                        val device = result.device
                        val deviceName = device.name
                        val deviceAddress = device.address // Mac address
                    }
                })
            }
        }

        @SuppressLint("MissingPermission")
        fun stopBluetoothLeScan(context: Context, callback: ScanCallback) {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return

            getBluetoothAdapter()?.let {
                it.bluetoothLeScanner?.stopScan(callback)
            }
        }

        @SuppressLint("MissingPermission")
        fun getPairedDevices(context: Context, pairedMap: HashMap<String, String>) {
            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                return

            getBluetoothAdapter()?.let {
                val pairedDevices: Set<BluetoothDevice>? = it.bondedDevices
                if (!pairedDevices.isNullOrEmpty()) {
                    for (device in pairedDevices)
                        pairedMap[device.name] = device.address
                }
            }
        }

        fun createPairReceiver(context: Context): BroadcastReceiver? {
            return object : BroadcastReceiver() {
                @SuppressLint("MissingPermission")
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            if (!PermissionUtil.hadBluetoothConnectPermission(context))
                                return

                            when (it.bondState) {
                                BluetoothDevice.BOND_BONDED -> {
                                    //it.name
                                }

                                BluetoothDevice.BOND_BONDING -> {
                                    //it.name
                                }

                                BluetoothDevice.BOND_NONE -> {
                                    //it.name
                                }
                            }
                        }
                    }
                }
            }
        }

        fun createConnectionReceiver(): BroadcastReceiver? {
            return object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (!PermissionUtil.hadBluetoothConnectPermission(context))
                            return

                        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                            //it.name
                        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                            //it.name
                        }
                    }
                }
            }
        }

        private fun getBluetoothAdapter(): BluetoothAdapter? {
            return BluetoothAdapter.getDefaultAdapter()
        }
    }
}