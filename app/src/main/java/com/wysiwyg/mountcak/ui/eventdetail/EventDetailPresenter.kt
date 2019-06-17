package com.wysiwyg.mountcak.ui.eventdetail

import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wysiwyg.mountcak.R
import com.wysiwyg.mountcak.data.model.*
import com.wysiwyg.mountcak.util.DateUtil
import org.jetbrains.anko.textResource
import java.text.NumberFormat
import java.util.*

class EventDetailPresenter(private val view: EventDetailView, private val eid: String) {

    private val db = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser!!.uid

    private fun checkSender(event: Event) {
        if (event.userId == user) view.showOwnPost(event)
        else view.isNotOwnPost(event.userId)
    }

    private val eventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            try {
                val event = p0.getValue(Event::class.java)
                view.hideLoading()
                view.showEventDetail(event)

                mountData(event?.mountId!!)
                userData(event.userId!!)
                checkSender(event)
                checkIsJoin()
            } catch (ex: Exception) {
                ex.printStackTrace()
                view.eventNotFound()
            }
        }

    }

    private fun mountData(id: String) {
        db.child("mount").child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val mount = p0.getValue(Mount::class.java)
                    view.showMountData(mount)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun userData(id: String) {
        db.child("user").child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val user = p0.getValue(User::class.java)
                    view.showUserData(user)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun checkIsJoin() {
        db.child("join").child(eid).orderByChild("userReqId").equalTo(user)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        if (p0.exists()) {
                            var data: Join? = null
                            p0.children.forEach {
                                data = it.getValue(Join::class.java)
                            }

                            when (data?.status) {
                                0 -> view.showDefault()
                                1 -> view.showIsJoined(data?.id)
                                2 -> view.showIsRequested(data?.id)
                            }
                        } else view.showDefault()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    fun getEventDetail() {
        view.showLoading()
        db.child("event").child(eid).addValueEventListener(eventListener)
    }

    fun checkDate(dateStart: String?, dateEnd: String?): String {
        return if (dateStart.equals(dateEnd))
            DateUtil.dateFormat(dateEnd, "EEEE, dd MMMM yyyy")
        else
            DateUtil.dateFormat(dateStart, "EEEE, dd MMMM") + " - " + DateUtil.dateFormat(dateEnd, "EEEE, dd MMMM yyyy")
    }

    fun checkCost(cost: Int?, tv: TextView) {
        return if (cost == 0) {
            tv.textResource = R.string.free
        } else {
            val local = Locale("in", "ID")
            val currency = NumberFormat.getCurrencyInstance(local)
            tv.text = currency.format(cost).toString()
        }
    }

    fun requestJoin(uid: String?, mountName: String?) {
        try {
            val ts = System.currentTimeMillis()
            val msgContent =
                "Hello, I want to join your trip to $mountName. Please respond my request immediately. Thank you."
            val joinId = db.child("join").push().key!!

            db.child("join").child(eid).child(joinId).setValue(Join(joinId, eid, uid, user, 2, ts))
                .addOnSuccessListener {
                    val chatId = db.child("chat").child(user).child(uid!!).push().key!!
                    val chat = Chat(chatId, user, uid, msgContent, ts, false, true, joinId, eid)

                    db.child("chat").child(user).child(uid).child(chatId).setValue(chat)
                    db.child("chat").child(uid).child(user).child(chatId).setValue(chat)
                        .addOnSuccessListener {
                            view.requestSuccess(uid)
                        }

                }
                .addOnFailureListener {
                    view.requestFailed()
                }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun cancelRequest(joinId: String?) {
        db.child("join").child(eid).child(joinId!!).removeValue()
    }

    fun deletePost() {
        db.child("event").child(eid).removeValue().addOnSuccessListener {
            view.eventNotFound()
            db.child("join").child(eid).removeValue()
        }
    }

    fun onClose() {
        db.child("event").child(eid).removeEventListener(eventListener)
    }
}