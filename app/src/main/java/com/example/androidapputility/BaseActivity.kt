package com.example.androidapputility

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.androidapputility.utility.DeviceUtil
import java.security.InvalidParameterException

abstract class BaseActivity : AppCompatActivity() {

    companion object {
    }

    abstract fun getContentActivityResult(uri: Uri?)
    abstract fun getMultipleContentsActivityResult(uriList: List<Uri>?)

    private val TAG = BaseActivity@ this.javaClass.simpleName

    private var powerWakeLock: WakeLock? = null

    open val getContentActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            getContentActivityResult(uri)
        }
    open val getMultipleContentsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uriList: List<Uri>? ->
            getMultipleContentsActivityResult(uriList)
        }

    //  ==== Lifecycle =============================================================================
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
        getContentActivityResultLauncher.unregister()
        lockPowerWake(false)
        lifecycleMsg("onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifecycleMsg("onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lifecycleMsg("onRestoreInstanceState")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleMsg("onActivityResult")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        lifecycleMsg("onBackPressed")
    }

    override fun finish() {
        super.finish()
        lifecycleMsg("finish")
    }

    //  ==== Fragment ==============================================================================
    open fun findFragment(id: Int): Fragment? {
        return supportFragmentManager.findFragmentById(id)
    }

    open fun hadFragment(id: Int): Boolean {
        return findFragment(id) != null
    }

    open fun findFragment(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    open fun hadFragment(tag: String): Boolean {
        return findFragment(tag) != null
    }

    open fun showFragment(fragment: Fragment, tag: String, fragmentContainerId: Int) {
        if (tag.isNullOrEmpty())
            throw InvalidParameterException("Got invalid tag.")

        supportFragmentManager.findFragmentByTag(tag)?.let {
            if (it.isAdded) {
                val status = if (it.isVisible) "visible" else "hidden"
                throw RuntimeException("Fragment: $tag was added, status: $status.")
            } else
                throw RuntimeException("Fragment: $tag was lost in fragment manager.")
        }

        supportFragmentManager.beginTransaction().let {
            it.add(fragmentContainerId, fragment, tag)
            it.addToBackStack(null)
            it.commit()
        }
    }

    open fun dismissFragment(tag: String?) {
        if (tag.isNullOrEmpty()) return

        supportFragmentManager.findFragmentByTag(tag)?.let {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.remove(it)
            fragmentTransaction.commit()
        }
    }

    open fun hideFragment(tag: String?) {
        if (tag.isNullOrEmpty()) return

        supportFragmentManager.findFragmentByTag(tag)?.let {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.hide(it)
            fragmentTransaction.commit()
        }
    }

    //  ==== Permission ============================================================================
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

    //  ==== Keyboard ==============================================================================
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyLongPress(keyCode, event)
    }

    // ==== Touch ==================================================================================
    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        return super.onGenericMotionEvent(event)
    }

    //  ==== Power Management ======================================================================
    open fun lockPowerWake(lock: Boolean) {
        if (lock) {
            if (powerWakeLock == null) {
                @SuppressWarnings("deprecation")
                val lockLevel = (PowerManager.SCREEN_DIM_WAKE_LOCK
                        or PowerManager.ON_AFTER_RELEASE
                        or PowerManager.ACQUIRE_CAUSES_WAKEUP);
                powerWakeLock =
                    (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(lockLevel, TAG)
            }
            setPowerWakeLock(true)
        } else {
            powerWakeLock?.let {
                try {
                    setPowerWakeLock(false)
                    it.release()
                } catch (e: Exception) {
                }
            }
            powerWakeLock = null
        }
    }

    open fun setPowerWakeLock(acquire: Boolean) {
        powerWakeLock?.let {
            if (acquire) {
                if (!it.isHeld) {
                    try {
                        it.acquire()
                        warningMsg("Power wake was locked.")
                    } catch (e: SecurityException) {
                        errorMsg("Lock power wake failed, message: " + e.message)
                    }
                }
            } else {
                if (it.isHeld) {
                    try {
                        it.release()
                        warningMsg("Power wake was unlocked.")
                    } catch (e: SecurityException) {
                        errorMsg("Unlock power wake failed, message: " + e.message)
                    }
                }
            }
        }
    }

    //  ==== Device Orientation ====================================================================
    open fun lockScreenOrientation(lock: Boolean) {
        if (DeviceUtil.isChromebookDevice(this@BaseActivity)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            return
        }

        var orientationSetting = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        if (lock) {
            val orientation = resources.configuration.orientation
            orientationSetting =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        requestedOrientation = orientationSetting

        warningMsg("Lock screen orientation: $lock")
    }

    //  ==== Toast/Dump Message ====================================================================
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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

    private fun getId(): Int {
        return System.identityHashCode(this@BaseActivity)
    }

    private fun lifecycleMsg(msg: String) {
        debugMsg("Activity id: 0x" + Integer.toHexString(getId()) + ", lifecycle status: " + "$msg")
    }
}