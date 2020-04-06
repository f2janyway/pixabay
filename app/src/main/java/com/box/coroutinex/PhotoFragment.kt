package com.box.coroutinex

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    //    private var param2: String? = null
    lateinit var photoAdapter: PhotoAdapter

    var q: String? = null
    var pageNum = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        pageNum = 1

        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        photoAdapter =
            PhotoAdapter(arrayListOf(Hits(largeImageURL = "https://pixabay.com/get/55e0d340485aa814f6da8c7dda293277143edfe1534c704c7d297ad1924ccd51_1280.jpg")))
        photo_recycler.adapter = photoAdapter
        doSearch("car")
        Log.e("onActivityCreated", "created")
        photo_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    pageNum++
                    doSearch(q!!)
                }
            }
        })

    }

    fun doSearch(query: String) {
        if (q == null)
            q = query
        GlobalScope.launch(Dispatchers.IO) {
            val service = APIService.pixabayRetrofit.create(APIService::class.java)
            //param1  : key ,, param2 : query
            val photoResult: Call<Photo> = service.searchImages(param1!!, query, pageNum)
            photoResult.enqueue(object : Callback<Photo> {
                override fun onFailure(call: Call<Photo>, t: Throwable) {
                }

                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    Log.e("url", response.headers().toString())
                    val hits = response.body()!!.hits as ArrayList
//                    Log.e("hits", Gson().fromJson(hits.toString(), Hits::class.java).toString())
                    GlobalScope.launch(Dispatchers.Main) {
                        if (pageNum == 1)
                            photoAdapter.setHitsList(hits)
                        else
                            photoAdapter.addHitsList(hits)
                    }
                }
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance() = PhotoFragment()
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
