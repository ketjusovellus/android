package fi.ketjusovellus.ketjupilotti.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.ketjusovellus.ketjupilotti.R
import kotlinx.android.synthetic.main.fragment_exposures_list.*
import org.dpppt.android.sdk.DP3T

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

class ExposuresListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_exposures_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
        close.setOnClickListener {
            view.findNavController().navigateUp()
        }
    }

    private fun populateData() {
        val status = DP3T.getStatus(requireContext())
        val groupedByDay = status.exposureDays.groupBy {
            it.exposedDate.startOfDayTimestamp
        }.map {
            ExposuresForDay(it.key, it.value.count())
        }
        val exposuresListAdapter = ExposuresListAdapter()
        exposures_list_view.adapter = exposuresListAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        exposures_list_view.layoutManager = layoutManager
        exposuresListAdapter.items = groupedByDay
        exposuresListAdapter.notifyDataSetChanged()
    }

}
