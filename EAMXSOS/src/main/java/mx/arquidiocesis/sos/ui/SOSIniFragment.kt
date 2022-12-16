package mx.arquidiocesis.sos.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.FragmentFaithfulProfileBinding
import com.upax.eamxsos.databinding.FragmentSOSIniBinding


class SOSIniFragment : Fragment() {
    lateinit var binding: FragmentSOSIniBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSOSIniBinding.inflate(layoutInflater, container, false)
        binding.cvSOS.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameSOSProfile, FaithfulProfileFragment.newInstance())
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
        return binding.root
    }
}