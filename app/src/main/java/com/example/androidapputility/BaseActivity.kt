package com.example.androidapputility

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {

    companion object {
    }

    private val TAG: String = BaseActivity@ this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleMsg("onCreate")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        lifecycleMsg("onPostCreate")
    }

    override fun onStart() {
        super.onStart()
        lifecycleMsg("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        lifecycleMsg("onRestart")
    }

    override fun onResume() {
        super.onResume()
        lifecycleMsg("onResume")
    }

    override fun onPostResume() {
        super.onPostResume()
        lifecycleMsg("onPostResume")
    }

    override fun onPause() {
        super.onPause()
        lifecycleMsg("onPause")
    }

    override fun onStop() {
        super.onStop()
        lifecycleMsg("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleMsg("onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifecycleMsg("onSaveInstanceState")
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    open fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    open fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun verboseMsg(msg: String?) {
        msg?.let {
            Log.v(TAG, it)
        }
    }

    open fun debugMsg(msg: String?) {
        msg?.let {
            Log.d(TAG, it)
        }
    }

    open fun errorMsg(msg: String?) {
        msg?.let {
            Log.e(TAG, it)
        }
    }

    open fun infoMsg(msg: String?) {
        msg?.let {
            Log.i(TAG, it)
        }
    }

    open fun warningMsg(msg: String?) {
        msg?.let {
            Log.w(TAG, it)
        }
    }

    private fun lifecycleMsg(msg: String) {
        debugMsg("Lifecycle: $msg")
    }
}