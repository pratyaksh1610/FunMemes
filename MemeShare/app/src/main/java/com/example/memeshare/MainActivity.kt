package com.example.memeshare

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var currentImageUrl:String? = null

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
        pbLoad.visibility = View.VISIBLE
        //when meme is loading progress bar will load
        val queue = Volley.newRequestQueue(this)
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
                        pbLoad.visibility = View.GONE
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbLoad.visibility = View.GONE
                        return false
                    }
                }).into(imgView)


            },
            {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
            })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
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
}