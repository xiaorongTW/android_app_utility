package com.example.androidapputility

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.androidapputility.databinding.ActivityLauncherBinding

class LauncherActivity : BaseActivity() {

    companion object {
        val BUNDLE_PARAM_INTENT_TIME = "BUNDLE_PARAM_TIME"

        private val INTENT_PICKER_REQEST_CODE = 100
    }

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    override fun onResume() {
        super.onResume()
        showToast("Welcome to LauncherActivity!")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTENT_PICKER_REQEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra(BUNDLE_PARAM_INTENT_TIME)
        }
    }

    private fun initUI() {
        binding.tvToActivity.setOnClickListener {
            startMediaPickerActivity()
        }

        binding.tvToFragment.setOnClickListener {
            showLauncherFragment()
        }
    }

    private fun startMediaPickerActivity() {
        val intent = Intent(this@LauncherActivity, MediaPickerActivity::class.java)
        intent.putExtra(MediaPickerActivity.BUNDLE_PARAM_INTENT_TIME, System.currentTimeMillis())
//        startActivity(intent)
        startActivityForResult(intent, INTENT_PICKER_REQEST_CODE)
    }

    private fun showLauncherFragment() {
        val launcherFragment = LauncherFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, launcherFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun dismissLauncherFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.remove(fragment)
            fragmentTransaction.commit()
        }
    }

    private fun hideLauncherFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.hide(fragment)
            fragmentTransaction.commit()
        }
    }
}