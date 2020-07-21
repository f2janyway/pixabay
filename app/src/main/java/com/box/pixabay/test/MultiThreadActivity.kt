package com.box.pixabay.test

import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.box.pixabay.R
import com.box.pixabay.data.APIService
import com.box.pixabay.data.APIService.Companion.pixabayRetrofit
import com.box.pixabay.data.Hits
import com.box.pixabay.data.Photo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_mutil_thread.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MultiThreadActivity : AppCompatActivity() {

    fun addImageText(it: Hits) {
        val imageView = ImageView(this@MultiThreadActivity)
        val textView = TextView(this@MultiThreadActivity)
        textView.text = it.user_id.toString()
        container_layout.apply {
            addView(imageView)
            addView(textView)
        }
        Glide.with(application).load(it.largeImageURL).into(imageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mutil_thread)

        val service = pixabayRetrofit.create(APIService::class.java)
        val cars = service.searchImages(getString(R.string.pixa_key), "car", 1, true)
        val tree = service.searchImages(getString(R.string.pixa_key), "tree", 1, true)
        val building = service.searchImages(getString(R.string.pixa_key), "building", 1, true)
        GlobalScope.launch(Dispatchers.IO) {
            cars.enqueue(object : Callback<Photo> {
                override fun onFailure(call: Call<Photo>, t: Throwable) {
                }

                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    val rs = response.body()
                    val hits = rs?.hits
                    hits?.forEach {
                         GlobalScope.launch(Dispatchers.Main) {
                            textview1.text = textview1.text.toString() + it.user_id
                            addImageText(it)
                        }
                    }
                }
            })
            tree.enqueue(object : Callback<Photo> {
                override fun onFailure(call: Call<Photo>, t: Throwable) {
                }

                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    val rs = response.body()
                    val hits = rs?.hits
                    hits?.forEach {
                        GlobalScope.launch(Dispatchers.Main) {
                            textview2.text = textview1.text.toString() + it.user_id
                            addImageText(it)
                            Log.e("thread","tree")
                        }
                    }
                }
            })
            building.enqueue(object : Callback<Photo> {
                override fun onFailure(call: Call<Photo>, t: Throwable) {
                }

                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    val rs = response.body()
                    val hits = rs?.hits
                    hits?.forEach {
                        GlobalScope.launch(Dispatchers.Main) {
                            textview3.text = textview1.text.toString() + it.user_id
                            addImageText(it)
                            Log.e("thread","building")
                        }
                    }
                }
            })
        }

    }
}
