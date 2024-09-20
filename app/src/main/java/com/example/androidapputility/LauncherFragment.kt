package com.example.androidapputility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class LauncherFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    private fun initUI(view: View) {
        val tvBack: View = view.findViewById(R.id.tvBack)
        tvBack.setOnClickListener {
            dismissFragment()
        }
    }

    private fun dismissFragment() {
        parentFragmentManager
            .beginTransaction()
            .remove(this@LauncherFragment).commit()
    }

    private fun dismissFragment2() {
        parentFragmentManager.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        showToast("Welcome to LauncherFragment!")
    }
}