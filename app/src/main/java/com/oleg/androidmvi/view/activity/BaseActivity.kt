package com.oleg.androidmvi.view.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getToolbarInstance(): Toolbar?

    override fun onResume() {
        super.onResume()
        this.getToolbarInstance()?.let { setSupportActionBar(it) }
    }
}