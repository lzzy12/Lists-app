package com.shivam.notes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class myViewAdapter (private val mDataset: ArrayList<String>): RecyclerView.Adapter<myViewAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val itemTextView : TextView = view.findViewById(R.id.recyclerTextView)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): myViewAdapter.MyViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_text_view, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        p0.itemTextView.text = mDataset[p1]
    }
}