package com.pratyakshkhurana.memes_recycler_view_demo

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var mMemeAdapter: MemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //layout manager
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        fetch()
        mMemeAdapter = MemeAdapter(this)
        recyclerView.adapter = mMemeAdapter

    }

    private fun fetch() {
        val url = "https://meme-api.herokuapp.com/gimme/memes/50"

        //creating json object request
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                //extract memes json array
                val memesJsonArray = it.getJSONArray("memes")
                val memesArrayToAdapter = ArrayList<MemeData>()
                //traverse json array and extract each object and pass to memesArrayToAdapter
                //and finally to adapter to load in recycler view by binding.....
                for (i in 0 until memesJsonArray.length()) {
                    //extract eachObj of array at position 'i'
                    val eachObj = memesJsonArray.getJSONObject(i)
                    val obj = MemeData(
                        eachObj.getString("title"),
                        eachObj.getString("author"),
                        eachObj.getString("url")
                    )
                    memesArrayToAdapter.add(obj)
                }
                mMemeAdapter.update(memesArrayToAdapter)
            },
            {
                //if error occurs due to glitch , will display this toast
                //short message for the user
                Toast.makeText(this, "Error !", Toast.LENGTH_LONG).show()
            }
        )
        // Access the RequestQueue through your singleton class.
        //made singleton so that there is only one instance of volley for entire app
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    fun shareImage(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this cool meme I got from Reddit  $url ")

        //chooser where to send and it will display all apps capable of sharing in android device
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }

    fun saveImg(view: View){
        // on click of this btnSve button it will capture
        // screenshot of imgView and save it to gallery
            // extract the bitmap of the imgView using
            // getBitmapOfImgView
            val bitmap = getBitmapOfImgView(view)
            // if bitmap is not null then
            // save it to gallery
            if (bitmap != null) {
                saveImgViewToStorage(bitmap)
            }
    }

    private fun getBitmapOfImgView(v: View): Bitmap? {
        // create a bitmap object
        var screenshotOfImgView: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshotOfImgView = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshotOfImgView)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("Error", "Failed to capture ! " + e.message)
        }
        // return the bitmap
        return screenshotOfImgView
    }


    // this method saves the image to gallery
    private fun saveImgViewToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the content values
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an output stream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Saved to Gallery ! \uD83C\uDF89" , Toast.LENGTH_SHORT).show()
        }
    }



}