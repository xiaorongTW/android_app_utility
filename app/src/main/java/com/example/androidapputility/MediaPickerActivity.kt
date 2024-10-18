package com.example.androidapputility

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.androidapputility.databinding.ActivityMediaPickerBinding


class MediaPickerActivity : BaseActivity() {

    companion object {
        val BUNDLE_PARAM_INTENT_TIME = "BUNDLE_PARAM_TIME"
    }

    private lateinit var binding: ActivityMediaPickerBinding

    override fun getContentActivityResult(uri: Uri?) {
    }

    override fun getMultipleContentsActivityResult(uriList: List<Uri>?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentTime = intent.getLongExtra(BUNDLE_PARAM_INTENT_TIME, -1)

        binding = ActivityMediaPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.tvBack.setOnClickListener {
            leave()
        }
    }

    private fun leave() {
        finish()
    }

    private fun leave2() {
        onBackPressed()
    }

    private fun leave3() {
        val intent = Intent()
        intent.putExtra(LauncherActivity.BUNDLE_PARAM_INTENT_TIME, System.currentTimeMillis())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun leave4() {
        val intent = Intent(this, LauncherActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}