package com.box.coroutinex

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.drm.DrmStore
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_screen_slide_screen.*
import kotlinx.android.synthetic.main.fragment_screen_slide_screen.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ScreenSlideScreenFragment : Fragment() {
    val WRITE_PERMISSION = 100

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            ScreenSlideScreenFragment().apply {
                arguments = Bundle().apply {
                    putString("url", url)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    lateinit var url: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString("url")!!
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_slide_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.photoView_progress.visibility = View.VISIBLE

        Glide.with(activity!!).load(url)
            .listener(object  : RequestListener<Drawable>{
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
                    view.photoView_progress.visibility = View.GONE
                    return false
                }

            })
            .into(photoView)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.apply {
            download_button.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        saveImage()
                    } else {
                        Log.e("권한", "permission denied")
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            WRITE_PERMISSION
                        )
                    }
                } else {
                    saveImage()
                }
            }
            share_button.setOnClickListener {
                if (photoView.drawable != null) {
                    (activity as MainActivity).applyNotification()
                    shareBitmap()
                }
            }
        }

    }


    private fun shareBitmap() {
        val mBitmap = (photoView.drawable as BitmapDrawable).bitmap

        val stream = ByteArrayOutputStream()
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        val shareIntent = Intent()
        val compressedBitmap = BitmapFactory.decodeByteArray(
            stream.toByteArray(),
            0,
            stream.toByteArray().size
        )
        shareIntent.apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_STREAM,
                getImageUriFromBitmap(activity!!, mBitmap)
                /*Bitmap.createScaledBitmap(compressedBitmap, compressedBitmap.width /6, compressedBitmap.height/6, false) 쓸데 없구만 uri 로 보내야지*/
            )
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    fun saveImage() {
        val mBitmap = (photoView.drawable as BitmapDrawable).bitmap

        val uri = getImageUriFromBitmap(activity!!, mBitmap!!)
        val path = /*ContextWrapper(context).getDir("images", Context.MODE_PRIVATE)*/
            getRealPathFromUri(uri!!)
//                val filepath = path + File.separator.toString() + "${UUID.randomUUID()}.jpg"
        val file = File(path!!)
        Log.e("file", file.absolutePath)
        try {
            if (!file.exists()) {
                file.createNewFile()
                Log.e("file not exists", file.absolutePath)
            }
            val fos = FileOutputStream(file)
            GlobalScope.launch(Dispatchers.IO) {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                this.launch(Dispatchers.Main) {
                    Toast.makeText(activity!!, "다운로드 됨", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var path: String? = null
        try {
            path =
                MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.permission), Toast.LENGTH_SHORT).show()
        }
        return Uri.parse(path) ?: null
    }

    private fun getRealPathFromUri(contentURI: Uri): String? {
        val file: String?
        if ("content" == contentURI.scheme) {
            val cursor: Cursor? = activity!!.contentResolver.query(
                contentURI,
                arrayOf(MediaStore.Images.ImageColumns.DATA),
                null,
                null,
                null
            )
            cursor?.moveToFirst();
            file = cursor?.getString(0)
            cursor?.close()
        } else {
            file = contentURI.path
        }
        Log.e("Chosen path ", "Chosen path = $file");
        return file
    }
}
