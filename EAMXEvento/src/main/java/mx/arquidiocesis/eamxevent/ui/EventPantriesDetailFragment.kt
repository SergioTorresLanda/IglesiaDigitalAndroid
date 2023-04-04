package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxevent.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventPantriesDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventPantriesDetailFragment : FragmentBase() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_pantries_detail, container, false)
    }

    companion object {

        fun newInstance(callBack: EAMXHome): EventPantriesDetailFragment {
            val fragment = EventPantriesDetailFragment()
            fragment.callBack = callBack
            return fragment
        }
    }
}