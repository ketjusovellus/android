package fi.ketjusovellus.ketjupilotti.main

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.ketjusovellus.ketjupilotti.R
import kotlinx.android.synthetic.main.item_exposure_day.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    parent.inflate(R.layout.item_exposure_day, false)
) {
    fun bind(exposuresForDay: ExposuresForDay) {
        val dateFormat = SimpleDateFormat("MM/dd/yy")
        itemView.number_of_exposures.text =
            itemView.context.resources.getString(R.string.exposures_list_item, exposuresForDay.exposures)
        itemView.exposure_day.text = dateFormat.format(Date(exposuresForDay.timeStamp))
    }
}

class ExposuresListAdapter : RecyclerView.Adapter<ViewHolder>() {

    var items : List<ExposuresForDay> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

}

data class ExposuresForDay(
    val timeStamp: Long,
    val exposures: Int
)
