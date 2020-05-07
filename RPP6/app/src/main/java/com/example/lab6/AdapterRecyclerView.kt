package com.example.lab6

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


import java.util.ArrayList

class AdapterRecyclerView(
    private val context: Context,
    private val list: ArrayList<Item>,
    private val mOnNoteListener: OnNoteListener
) : RecyclerView.Adapter<AdapterRecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_front, parent, false)
        return ViewHolder(v, mOnNoteListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        val name = currentItem.name
        val price = java.lang.Float.toString(currentItem.price)
        holder.textViewName.text = name
        holder.textViewPrice.text = price
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View, internal var onNoteListener: OnNoteListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textViewName: TextView
        var textViewPrice: TextView

        init {
            textViewName = itemView.findViewById(R.id.text_view_name)
            textViewPrice = itemView.findViewById(R.id.text_view_price)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            onNoteListener.onNoteClick(adapterPosition)
            notifyDataSetChanged()
        }
    }

    interface OnNoteListener {
        fun onNoteClick(position: Int)
    }
}
