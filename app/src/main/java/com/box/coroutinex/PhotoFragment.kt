package com.box.coroutinex

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.box.coroutinex.data.APIService
import com.box.coroutinex.data.Hits
import com.box.coroutinex.data.Photo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PhotoFragment : Fragment() {
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
        photoAdapter = PhotoAdapter(ArrayList<Hits>())
        progressBar.visibility = View.VISIBLE
        photoAdapter.apply {
            setHasStableIds(true)
            loadingCheck = object : PhotoAdapter.LoadingCheck {
                override fun isLoadingEnd(boolean: Boolean) {
                    if (boolean) {
                        progressBar.visibility = View.GONE
                    }
                }
            }
            itemClick = object : PhotoAdapter.ItemClick {
                override fun itemClickListener(url: String, position: Int) {
                    activity!!.pager_container.visibility = View.VISIBLE

                    activity!!.viewPager2.apply {
                        adapter = ScreenSlidePagerAdapter(activity!!)
                        currentItem = position
                    }
                    activity!!.viewpager_cancel_button.apply {
                        this.setOnClickListener {
                            activity!!.pager_container.visibility = View.GONE
                        }
                    }
                }
            }
        }
        photo_recycler.adapter = photoAdapter
        photo_recycler.setHasFixedSize(true)

        doSearch("car")
        photo_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (pageNum != 0) {
                        pageNum++
//                        Log.e("pageNUm", pageNum.toString())
                        doSearch(q!!)
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun doSearch(query: String) {
        if (q != query) {
            pageNum = 1
            q = query
        }
        GlobalScope.launch(Dispatchers.IO) {
            val service = APIService.pixabayRetrofit.create(APIService::class.java)
            //param1  : key ,, param2 : query
            val photoResult: Call<Photo> = service.searchImages(param1!!, q!!, pageNum,true)
            photoResult.enqueue(object : Callback<Photo> {
                override fun onFailure(call: Call<Photo>, t: Throwable) {
                    Toast.makeText(activity, getString(R.string.no_wifi), Toast.LENGTH_SHORT).show()
                    Log.e("fair", "fail network")
                }

                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    Log.e("url", response.toString())

                    if (response.body() == null || response.body()!!.hits.isEmpty()) {
                        Toast.makeText(
                            activity,
                            getString(R.string.no_search_info),
                            Toast.LENGTH_SHORT
                        ).show()
                        activity!!.progressBar.visibility = View.GONE
                        return
                    }
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

    inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = photoAdapter.list.size

        override fun createFragment(position: Int): Fragment =
            ScreenSlideScreenFragment.newInstance(photoAdapter.list[position].largeImageURL!!)
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}
