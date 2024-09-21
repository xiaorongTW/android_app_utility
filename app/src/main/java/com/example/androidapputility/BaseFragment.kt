package com.example.androidapputility

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    companion object {

    }

    private val TAG: String = BaseFragment@ this.javaClass.simpleName
    private lateinit var rootView: View

    private val canPassThrough = false

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        lifecycleMsg("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleMsg("onCreate")
    }

    override fun onStart() {
        super.onStart()
        lifecycleMsg("onStart")
    }

    override fun onResume() {
        super.onResume()
        lifecycleMsg("onResume")
    }

    override fun onPause() {
        super.onPause()
        lifecycleMsg("onPause")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifecycleMsg("onSaveInstanceStatePause")
    }

    override fun onStop() {
        super.onStop()
        lifecycleMsg("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleMsg("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleMsg("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        lifecycleMsg("onDetach")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view

        if (!canPassThrough) {
            rootView.setOnClickListener(View.OnClickListener {
                return@OnClickListener // Block pass through behavior
            })
        }

        lifecycleMsg("onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleMsg("onActivityCreated")
    }

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    open fun close() {
        // Method 1: Close by transaction
        parentFragmentManager
            .beginTransaction()
            .remove(this).commit()

        // Method 2: Close by pop back stack
//        parentFragmentManager.popBackStack()
    }

    open fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    open fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
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