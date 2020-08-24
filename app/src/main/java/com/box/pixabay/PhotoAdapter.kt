package com.box.pixabay

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.box.pixabay.data.Hits
import com.box.pixabay.databinding.PhotoItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoAdapter(var list: ArrayList<Hits>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    //딱히 효용이 없다. glide의 이미지가 완전히 로드가 끝나는 시점을 못 찾네.
    interface LoadingCheck {
        fun isLoadingEnd(boolean: Boolean)
    }

    var loadingCheck: LoadingCheck? = null

    //
    interface ItemClick {
        fun itemClickListener(url: String, position: Int)
    }

    var itemClick: ItemClick? = null

    inner class ViewHolder(val binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holder: ViewHolder, position: Int) {
            binding.imageItem.apply {
//                this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                //이미지 뷰에 글리아드 맞춤
                Glide.with(context).load(list[position].webformatURL)
                    .apply(requestOptions)
                    .listener(requestListener).into(this)

                if (itemClick != null) {
                    image_item.setOnClickListener {
                        itemClick!!.itemClickListener(list[position].largeImageURL!!, position)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {

        holder.bind(holder, position)
        holder.itemView.apply {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        val binding = PhotoItemBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    private val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).transform(RoundedCorners(25))
        .placeholder(android.R.color.white).override(400,400)


    private val requestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            loadingCheck?.isLoadingEnd(true)
            return false
        }
    }


    fun setHitsList(hitsList: ArrayList<Hits>) {
        list.clear()
        list = hitsList
        notifyDataSetChanged()
    }

    fun addHitsList(hits: ArrayList<Hits>) {
        list.addAll(hits)
        Log.e("list size", list.size.toString())
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

}