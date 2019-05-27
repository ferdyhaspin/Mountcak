package com.wysiwyg.mountcak.ui.main

import android.support.v4.app.Fragment

interface MainView {
    fun toLogin()
    fun addView(fragment: Array<Fragment>)
    fun changeView(fragment: Fragment)
}