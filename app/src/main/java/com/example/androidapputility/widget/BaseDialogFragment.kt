package com.example.androidapputility.widget

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment : DialogFragment() {

    companion object {
        val TAG = BaseDialogFragment::class.java.simpleName
    }

    private lateinit var title: String
    private lateinit var message: String
    private lateinit var textPositive: String
    private lateinit var textNegative: String
    private lateinit var textNeutral: String

    private var listener: OnClickListener? = null

    interface OnClickListener {
        fun onPositive()
        fun onNegative()
        fun onNeutral()
        fun onDismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)

        if (::textPositive.isInitialized) {
            builder.setPositiveButton(textPositive) { dialog, which ->
                listener?.onPositive()
            }
        }
        if (::textNegative.isInitialized) {
            builder.setNegativeButton(textNegative) { dialog, which ->
                listener?.onNegative()
            }
        }
        if (::textNeutral.isInitialized) {
            builder.setNeutralButton(textNeutral) { dialog, which ->
                listener?.onNeutral()
            }
        }
        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    fun setTitle(title: String) {
        BaseDialogFragment@ this.title = title
    }

    fun setMessage(message: String) {
        BaseDialogFragment@ this.message = message
    }

    fun setPositiveButtonText(text: String) {
        BaseDialogFragment@ this.textPositive = text
    }

    fun setNegativeButtonText(text: String) {
        BaseDialogFragment@ this.textNegative = text
    }

    fun setNeutralButtonText(text: String) {
        BaseDialogFragment@ this.textNeutral = text
    }

    fun setOnClickListener(listener: OnClickListener?) {
        BaseDialogFragment@ this.listener = listener
    }
}