package com.example.laba5

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso

class LikesCatsAdapter(val context: Context, val catsList: ArrayList<CatDataClass>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.cats_like_list_item, parent, false)

        val catImageView  = view.findViewById(R.id.likedCatImage) as ImageView

        Picasso.get().load(catsList[position].catPictUrl).into(catImageView)

        return view
    }

    override fun getItem(position: Int): Any {
        return catsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return catsList.size
    }

}