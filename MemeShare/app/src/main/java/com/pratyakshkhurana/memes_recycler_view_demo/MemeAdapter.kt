package com.pratyakshkhurana.memes_recycler_view_demo

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratyakshkhurana.memes_recycler_view_demo.MemeAdapter.MemeViewHolder
import com.pratyakshkhurana.memes_recycler_view_demo.R
import kotlinx.android.synthetic.main.item_meme.view.*

class MemeAdapter(private val listener: MainActivity) : RecyclerView.Adapter<MemeViewHolder>() {

    private val items: ArrayList<MemeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        //inflating xml to view
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_meme, parent, false)
        return MemeViewHolder(item)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        //bind data in view holder
        //the array we got from memesJsonArray, i.e items having data members of data class item_meme
        //extract current item at position and passing to view holder which will show up in recycler view
        //assign values to views created in recycler view based on position
        val current = items[position]
        holder.titleMeme.text = current.title
        //localised the string resources
        holder.postedBy.text = "Posted by : " + current.postedBy
        holder.likes.text = (11000..80000).random().toString()
        holder.share.setOnClickListener {
            listener.shareImage(current.url)
        }
        holder.saveMeme.setOnClickListener{
            listener.saveImg(holder.image)
        }
        Glide.with(holder.itemView.context).load(current.url).into(holder.image)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(updatedItems: ArrayList<MemeData>) {
        items.clear()
        items.addAll(updatedItems)
        notifyDataSetChanged()
    }

    //view holder,
    class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleMeme: TextView = itemView.title
        val postedBy: TextView = itemView.postedBy
        val image: ImageView = itemView.memeImg
        val likes: TextView = itemView.likes
        val share: ImageView = itemView.share
        val saveMeme : ImageView = itemView.saveMeme
    }
}

