package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxevent.R

class EventFragment : Fragment() {

    lateinit var callBack: EAMXHome

    companion object {
        fun newInstance(callBack: EAMXHome): EventFragment {
            var fragment = EventFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.evento)
    }


}