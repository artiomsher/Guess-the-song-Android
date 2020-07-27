package com.example.guessthesong

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Adapter(private val imageModelArrayList: MutableList<MyModel>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var imgView: ImageView
        var txtMsg: TextView

        init {
            imgView = layout.findViewById<View>(R.id.icon) as ImageView
            txtMsg = layout.findViewById<View>(R.id.firstLine) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_song_view, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModelArrayList[position]
        holder.imgView.setImageResource(info.getImage_drawables())
        holder.txtMsg.setText(info.getNames())
        holder.txtMsg.setOnClickListener { v ->
            val intentProfile = Intent(v.context, SongActivity::class.java)
            intentProfile.putExtra("lyrics", info.getLyrics())
            intentProfile.putExtra("title", info.getTitles())
            intentProfile.putExtra("artist", info.getArtists())
            v.context.startActivity(intentProfile)
        }

    }


    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }
}