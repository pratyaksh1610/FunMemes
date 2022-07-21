package com.pratyakshkhurana.funmemes

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private var currentImageUrl:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        loadMeme()
    }

    // manifest file is single source of truth , includes name of app,user permission
    // launcher activity and title , label, icon, theme etc
    private fun loadMeme(){
        // Instantiate the RequestQueue.
        progress_bar_loading.visibility = View.VISIBLE
        //when meme is loading progress bar will load
        val url = "https://meme-api.herokuapp.com/gimme"
        // api call is done to fetch image of meme

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                currentImageUrl = response.getString("url")
                //glide library for image processing
                Glide.with(this)
                    .load(currentImageUrl)
                    .fitCenter()
                    .listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress_bar_loading.visibility = View.GONE
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress_bar_loading.visibility = View.GONE
                        return false
                    }
                }).into(imgView)


            },
            {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun nextMeme(view: View) {
        //for getting next meme on the screen
        loadMeme()
    }

    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,"Hey, checkout this cool meme I got from Reddit  $currentImageUrl ")

        //chooser where to send
        val chooser = Intent.createChooser(intent,"Share this meme using...")
        startActivity(chooser)
    }

    fun saveImg(view: View){
        // on click of this btnSve button it will capture
        // screenshot of imgView and save it to gallery
        btnSave.setOnClickListener {
            // extract the bitmap of the imgView using
            // getBitmapOfImgView
            val bitmap = getBitmapOfImgView(imgView)
            // if bitmap is not null then
            // save it to gallery
            if (bitmap != null) {
                saveImgViewToStorage(bitmap)
            }
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

                // Opening an outputstream with the Uri that we got
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