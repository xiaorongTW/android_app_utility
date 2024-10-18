package com.example.androidapputility

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.example.androidapputility.databinding.ActivityLauncherBinding
import com.example.androidapputility.testbed.DegreeRulerFragment
import com.example.androidapputility.typedef.FileType
import com.example.androidapputility.utility.PermissionUtil
import com.example.androidapputility.utility.UnitTest
import com.example.androidapputility.utility.UriUtil
import com.example.androidapputility.utility.ZipUtil
import com.example.androidapputility.widget.BaseDialogFragment
import com.example.androidapputility.widget.waiting.WaitingCursorFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry

class LauncherActivity : BaseActivity() {

    companion object {
        val BUNDLE_PARAM_INTENT_TIME = "BUNDLE_PARAM_TIME"

        private val INTENT_PICKER_REQEST_CODE = 100
    }

    private lateinit var binding: ActivityLauncherBinding

    private val ioJob = Job()
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + ioJob)

    override fun getContentActivityResult(uri: Uri?) {
        uri?.let {
//            zipFileToExternalStorageDirectory(it)
        }
    }

    override fun getMultipleContentsActivityResult(uriList: List<Uri>?) {
        uriList?.let {
//            zipFileListToExternalStorageDirectory(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        PermissionUtil.requestStoragePermissionIfNeeded(
            LauncherActivity@ this,
            PermissionUtil.REQUEST_CODE_STORAGE_PERMISSION
        )
    }

    override fun onResume() {
        super.onResume()

        if (PermissionUtil.hadStoragePermission(LauncherActivity@ this)) {
//            showToast("Storage permission access was confirmed!")
        } else {
            showToast("Storage permission access was denied!")
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ioJob.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTENT_PICKER_REQEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra(BUNDLE_PARAM_INTENT_TIME)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    showToast("Storage permission access was confirmed!")
                } else {
                    showToast("Storage permission access was denied!")
                }
            }
        }
    }

    private fun initUI() {
        binding.tvToActivity.setOnClickListener {
//            startMediaPickerActivity()
        }

        binding.tvToFragment.setOnClickListener {
            //showBaseDialogFragment()
            //showWaitingCursorFragment()
            //showDegreeRulerFragment()
        }

        binding.tvToExecuteTask.setOnClickListener {
//            pickFileToZip()
//            pickFilesToZip()
//            unzipRawFileToExternalStorageDirectory()
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

    private fun showDegreeRulerFragment() {
        val fragment = DegreeRulerFragment()
        if (hadFragment(fragment.TAG)) return

        showFragment(fragment, fragment.TAG, R.id.bottom_fragment_container)
    }

    private fun pickFileToZip() {
        getContentActivityResultLauncher.launch(FileType.IMAGE.toString())
    }

    private fun pickFilesToZip() {
        getMultipleContentsActivityResultLauncher.launch(
            arrayOf(
                FileType.IMAGE.toString(),
                FileType.VIDEO.toString()
            )
        )
    }

    private fun unzipRawFileToExternalStorageDirectory() {
        ioCoroutineScope.launch {
            val time = System.currentTimeMillis()
            UnitTest.unzipRawFileToExternalStorageDirectory(
                this@LauncherActivity,
                object : ZipUtil.OnUnzipListener {
                    override fun onPrepare(zipEntry: ZipEntry) {
                    }

                    override fun onStart() {
                    }

                    override fun onProgress(progress: Int) {
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                            }
                        }
                    }

                    override fun onComplete() {
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                                showToast("Unzip complete! Cost " + (System.currentTimeMillis() - time) + " ms.")
                            }
                        }
                    }

                    override fun onException(throwable: Throwable) {
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                            }
                        }
                    }
                })
        }
    }

    private fun zipFileToExternalStorageDirectory(uri: Uri) {
        val file = UriUtil.toFile(this@LauncherActivity, uri)
        file?.let {
            val tick = System.currentTimeMillis()
            zipFileToExternalStorageDirectory(it.absolutePath) { progress ->
                if (progress == 100) {
                    val costMS = System.currentTimeMillis() - tick
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            showToast("zipFileToExternalStorageDirectory complete! cost $costMS ms.")
                        }
                    }
                }
            }
        }
    }

    private fun zipFileToExternalStorageDirectory(path: String, listener: (Int) -> Unit) {
        ioCoroutineScope.launch {
            UnitTest.zipFileToExternalStorageDirectory(path, listener)
        }
    }

    private fun zipFileListToExternalStorageDirectory(uriList: List<Uri>) {
        val fileList = ArrayList<File>()
        uriList.forEach {
            val file = UriUtil.toFile(this@LauncherActivity, it)
            file?.let {
                fileList.add(it)
            }
        }

        if (fileList.size > 0) {
            val tick = System.currentTimeMillis()
            zipFileListToExternalStorageDirectory(fileList) { progress ->
                if (progress == 100) {
                    val costMS = System.currentTimeMillis() - tick
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            showToast("zipFileListToExternalStorageDirectory complete! cost $costMS ms.")
                        }
                    }
                }
            }
        }
    }

    private fun zipFileListToExternalStorageDirectory(
        srcFileList: List<File>,
        listener: (Int) -> Unit
    ) {
        ioCoroutineScope.launch {
            UnitTest.zipFileListToExternalStorageDirectory(srcFileList, listener)
        }
    }
}