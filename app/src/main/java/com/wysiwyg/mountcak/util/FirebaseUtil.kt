package com.wysiwyg.mountcak.util

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wysiwyg.mountcak.data.model.Mount

object FirebaseUtil {

    private val db = FirebaseDatabase.getInstance().reference

    fun getUserData(context: Context, uid: String?, tvName: TextView?, imgAva: ImageView) {
        db.child("user").child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val name = p0.child("name").getValue(String::class.java)
                    val photo = p0.child("photo").getValue(String::class.java)

                    tvName?.text = name
                    Glide.with(context).load(photo).into(imgAva)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun getMountData(ctx: Context, id: String?, img: ImageView, tvName: TextView, tvCity: TextView, tvReg: TextView) {
        db.child("mount").child(id!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val mount = p0.getValue(Mount::class.java)

                    Glide.with(ctx).load(mount?.cover).into(img)
                    tvName.text = mount?.mountName
                    tvCity.text = "${mount?.city}, "
                    tvReg.text = mount?.region
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}