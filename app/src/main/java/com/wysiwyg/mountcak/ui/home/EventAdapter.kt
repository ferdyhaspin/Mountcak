package com.wysiwyg.mountcak.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wysiwyg.mountcak.R
import com.wysiwyg.mountcak.data.model.Event
import com.wysiwyg.mountcak.ui.eventdetail.EventDetailActivity
import com.wysiwyg.mountcak.util.FirebaseUtil.getMountData
import com.wysiwyg.mountcak.util.FirebaseUtil.getUserData
import com.wysiwyg.temanolga.utilities.gone
import kotlinx.android.synthetic.main.item_event.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColorResource
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val events: MutableList<Event?>
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.item_event,
                p0,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItem(events[p1])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(event: Event?) {
            try {
                getUserData(itemView.context, event?.userId, itemView.tvUsername, itemView.imgUser)
                getMountData(
                    itemView.context,
                    event?.mountId,
                    itemView.imgMount,
                    itemView.tvMountName,
                    itemView.tvCity,
                    itemView.tvRegion
                )

                itemView.tvEventTitle.text = event?.title
                itemView.tvDayStart.text = dateFormat(event?.dateStart, "dd")
                itemView.tvDayEnd.text = dateFormat(event?.dateEnd, "dd")
                itemView.tvMontStart.text = dateFormat(event?.dateStart, "MMM")
                itemView.tvMonthEnd.text = dateFormat(event?.dateEnd, "MMM")

                itemView.tvParticipant.text = "${event?.maxParticipant} Participant"

                checkCost(event?.cost)
                checkDate(event?.dateStart, event?.dateEnd)

                itemView.btnDetail.onClick {
                    itemView.context.startActivity<EventDetailActivity>(
                        "event" to event
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        private fun checkCost(cost: Int?) {
            return if (cost == 0) {
                itemView.tvCost.text = "FREE"
                itemView.tvCost.textColorResource = R.color.colorPrimary
            }
            else {
                val local = Locale("in", "ID")
                val currency = NumberFormat.getCurrencyInstance(local)
                itemView.tvCost.text = currency.format(cost).toString()
                itemView.tvCost.textColorResource = android.R.color.black
            }
        }

        private fun checkDate(dateStart: String?, dateEnd: String?) {
            if (dateStart == dateEnd) {
                itemView.tvDayStart.gone()
                itemView.tvMontStart.gone()
                itemView.tvDiv.gone()
            }
        }

        private fun dateFormat(date: String?, pattern: String): String {
            val locale = Locale("in", "ID")
            val format = SimpleDateFormat("dd/MM/yy", locale)
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())

            return sdf.format(format.parse(date))
        }
    }
}