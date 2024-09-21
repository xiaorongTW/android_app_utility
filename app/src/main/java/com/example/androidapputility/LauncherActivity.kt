package com.example.androidapputility

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.androidapputility.databinding.ActivityLauncherBinding
import com.example.androidapputility.widget.BaseDialogFragment
import com.example.androidapputility.widget.waiting.WaitingCursorFragment

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTENT_PICKER_REQEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra(BUNDLE_PARAM_INTENT_TIME)
        }
    }

    private fun initUI() {
        binding.tvToActivity.setOnClickListener {
        }

        binding.tvToFragment.setOnClickListener {
        }
    }

    private fun startMediaPickerActivity() {
        val intent = Intent(this@LauncherActivity, MediaPickerActivity::class.java)
        intent.putExtra(MediaPickerActivity.BUNDLE_PARAM_INTENT_TIME, System.currentTimeMillis())
//        startActivity(intent)
        startActivityForResult(intent, INTENT_PICKER_REQEST_CODE)
    }

    private fun showBaseDialogFragment() {
        val dialog = BaseDialogFragment().apply {
            setTitle("Hello BaseDialogFragment")
            setMessage("This is a BaseDialogFragment.\nClick OK, NO or Cancel to close it.")
            setPositiveButtonText("OK")
            setNegativeButtonText("NO")
            setNeutralButtonText("Cancel")
            setOnClickListener(object : BaseDialogFragment.OnClickListener {
                private var clickWhat: String? = null

                override fun onPositive() {
                    clickWhat = "Click OK."
                    dismiss()
                }

                override fun onNegative() {
                    clickWhat = "Click NO."
                    dismiss()
                }

                override fun onNeutral() {
                    clickWhat = "Click Cancel."
                    dismiss()
                }

                override fun onDismiss() {
                    clickWhat?.let {
                        Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        dialog.show(supportFragmentManager, dialog.TAG)
    }

    private fun showWaitingCursorFragment() {
        val fragment = WaitingCursorFragment()
        if (hadFragment(fragment.TAG)) return

        showFragment(fragment, fragment.TAG, R.id.full_screen_fragment_container)
    }

}