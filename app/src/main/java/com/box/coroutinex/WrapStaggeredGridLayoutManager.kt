package com.box.coroutinex

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.Exception

class WrapStaggeredGridLayoutManager(span: Int, orientaiton: Int) :
    StaggeredGridLayoutManager(span, orientaiton) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

}