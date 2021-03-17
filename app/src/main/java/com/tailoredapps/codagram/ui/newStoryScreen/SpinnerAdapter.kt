package com.tailoredapps.codagram.ui.newStoryScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tailoredapps.codagram.R
import java.util.*


class SpinnerAdapter(
    context: Context,
    val data: ArrayList<String>,
    val nameList: ArrayList<String>
) : BaseAdapter() {
    private val infalate: LayoutInflater

    init {
        infalate = LayoutInflater.from(context)
    }

    override fun getView(postition: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var view = convertView

        if (view == null) {
            view = infalate.inflate(R.layout.custom_spinner_adapter, parent, false)
            viewHolder = ViewHolder(view)

        } else {
            viewHolder = view.tag as ViewHolder

        }
        view?.tag = viewHolder
        viewHolder.idTextView.text = nameList[postition]
        return view
    }

    override fun getItem(postition: Int): Any = data[postition]


    override fun getItemId(postition: Int): Long = postition.toLong()


    override fun getCount(): Int = data.size

    class ViewHolder(view: View) {
         var tvGroupName: TextView
         var idTextView: TextView

        init {
            tvGroupName = view.findViewById(R.id.nameTextView)
            idTextView = view.findViewById(R.id.idTextView)
        }
    }
}