package mx.arquidiocesis.eamxevent.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.databinding.FragmentDespensasBinding
import mx.arquidiocesis.eamxevent.databinding.FragmentEventBinding
import mx.arquidiocesis.eamxevent.databinding.FragmentEventDetailBinding
import mx.arquidiocesis.eamxevent.model.ViewModelEvent
import mx.arquidiocesis.eamxevent.repository.RepositoryEvent

class DespensasFragment : FragmentBase() {

    lateinit var binding: FragmentDespensasBinding
    lateinit var viewmodel: ViewModelEvent

    companion object {
        fun newInstance(callBack: EAMXHome): DespensasFragment {
            var fragment = DespensasFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewmodel = ViewModelEvent(RepositoryEvent(requireContext()))
        binding = FragmentDespensasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}