package com.wysiwyg.mountcak.ui.notification

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.wysiwyg.mountcak.R
import com.wysiwyg.mountcak.data.model.Join
import kotlinx.android.synthetic.main.fragment_notification.*
import org.jetbrains.anko.support.v4.onRefresh

class NotificationFragment : Fragment(), NotificationView {

    private lateinit var presenter: NotificationPresnter
    private lateinit var adapter: NotificationAdapter
    private val join: MutableList<Join?> = mutableListOf()

    override fun showLoading() {
        srlNotif.isRefreshing = true
    }

    override fun hideLoading() {
        srlNotif.isRefreshing = false
    }

    override fun showNotification(join: List<Join?>) {
        this.join.clear()
        this.join.addAll(join)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = NotificationAdapter(join)

        rvNotif.setHasFixedSize(true)
        rvNotif.layoutManager = LinearLayoutManager(activity)
        rvNotif.adapter = adapter

        presenter = NotificationPresnter(this)
        presenter.getNotification()

        srlNotif.onRefresh { presenter.getNotification() }
    }

}
