package mx.arquidiocesis.eamxevent.ui

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun EventFragment.setupRecyclerView() {

    binding.apply {
        rvEvents.adapter = adapter
        rvEvents.layoutManager = LinearLayoutManager(context)
        rvEvents.itemAnimator = DefaultItemAnimator()
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }
}