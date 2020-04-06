package com.box.coroutinex

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job


class MainActivity : AppCompatActivity() {

    lateinit var job1: Job
    lateinit var retoritJob: Job
    lateinit var photoFragment: PhotoFragment
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        //search_menu.xml 등록
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list, menu)
        val searchView = menu.findItem(R.id.action_search)
        Log.e("actionView", searchView.actionView.toString())
        //menuItem을 이용해서 SearchView 변수 생성
        val sv = searchView.actionView as androidx.appcompat.widget.SearchView
        //확인버튼 활성화
        sv.isSubmitButtonEnabled = true

        //SearchView의 검색 이벤트
        sv.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색버튼을 눌렀을 경우
            override fun onQueryTextSubmit(query: String): Boolean {
                photoFragment.doSearch(query)
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view: View? = currentFocus
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = View(this@MainActivity)
                }
                imm.hideSoftInputFromWindow(view.windowToken,0)
                return true
            }
            //텍스트가 바뀔때마다 호출
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab?.apply {
            setDisplayHomeAsUpEnabled(true)
        }


        photoFragment = PhotoFragment.newInstance(getString(R.string.pixa_key), "car")

        supportFragmentManager.beginTransaction()
            .add(R.id.container, photoFragment).commit()
    }
}
