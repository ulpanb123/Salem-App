package com.example.salemapplication

import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.backendless.BackendlessUser
import com.bumptech.glide.Glide
import java.util.zip.Inflater

class FriendsAdapter(private val friends : ArrayList<BackendlessUser>, private val context : Context) : BaseAdapter() {

    private val TAG : String = "FriendsAdapter"

    private lateinit var inflater : LayoutInflater

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var _convertView = convertView
        var viewHolder : ViewHolder? = null

        if(viewHolder == null) {
            _convertView = inflater.inflate(R.layout.row_friend_item, null)
            viewHolder = ViewHolder(_convertView)
            _convertView.tag = viewHolder
        } else {
            viewHolder = _convertView?.tag as ViewHolder
        }

        //set friend's name to viewHolder

        viewHolder.friendTextView.text = (friends[position].getProperty("name"))?.toString()

        //set image via Glide
        Glide.with(context).load(friends[position].getProperty("profileImageURL"))
            .centerCrop()
            .into(viewHolder.friendImageView)

        return _convertView
    }

    override fun getItem(position: Int): Any {
        return friends[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return friends.size
    }

    private class ViewHolder(view : View) {
        val friendImageView : ImageView = view.findViewById(R.id.friendImageView)
        val friendTextView : TextView = view.findViewById(R.id.friendsNameTextView)
    }
}