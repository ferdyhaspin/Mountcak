package com.wysiwyg.mountcak.ui.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wysiwyg.mountcak.R
import com.wysiwyg.mountcak.data.model.Mount
import com.wysiwyg.temanolga.utilities.gone
import com.wysiwyg.temanolga.utilities.visible
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment(), HomeView {

    private lateinit var presenter: HomePresenter
    private lateinit var adapter: MountAdapter
    private val mount: MutableList<Mount?> = mutableListOf()

    override fun showLoading() {
        progressHome.visible()
        rvHome.gone()
    }

    override fun hideLoading() {
        progressHome.gone()
        rvHome.visible()
    }

    override fun showMountList(mount: MutableList<Mount?>) {
        adapter = MountAdapter(mount)
        rvHome.setHasFixedSize(true)
        rvHome.layoutManager = LinearLayoutManager(context)
        rvHome.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter = HomePresenter(this)
        presenter.getData(mount)
    }
}