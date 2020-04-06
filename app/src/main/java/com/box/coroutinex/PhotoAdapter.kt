package com.box.coroutinex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoAdapter(var list: ArrayList<Hits>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context).load(list[position].largeImageURL).override(300)
                .placeholder(R.drawable.ic_launcher_foreground)
                .fitCenter()
                .into(iamge_item)
        }
    }
    fun setHitsList(hitsList : ArrayList<Hits>){
        list = hitsList
        notifyDataSetChanged()
    }

    fun addHitsList(hits: ArrayList<Hits>) {
        list.addAll(hits)
        notifyDataSetChanged()
    }

}