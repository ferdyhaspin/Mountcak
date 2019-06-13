package com.wysiwyg.mountcak.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.wysiwyg.mountcak.R
import com.wysiwyg.mountcak.data.model.Mount
import com.wysiwyg.mountcak.ui.explore.MountAdapter
import com.wysiwyg.temanolga.utilities.gone
import com.wysiwyg.temanolga.utilities.visible
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), SearchView, android.support.v7.widget.SearchView.OnQueryTextListener {

    private lateinit var presenter: SearchPresenter
    private lateinit var adapter: MountAdapter
    private val mount: MutableList<Mount?> = mutableListOf()

    override fun showLoading() {
        progressSearch.visible()
        rvSearch.gone()
    }

    override fun hideLoading() {
        progressSearch.gone()
        rvSearch.visible()
    }

    override fun showData(data: List<Mount?>) {
        mount.clear()
        mount.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        presenter.getData(p0!!)
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        presenter.getData(p0!!)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbarSearch)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = SearchPresenter(this)

        adapter = MountAdapter(mount)
        rvSearch.setHasFixedSize(true)
        rvSearch.layoutManager = LinearLayoutManager(this)
        rvSearch.adapter = adapter

        search.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onClose()
    }
}