package mx.arquidiocesis.eamxcommonutils.multimedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPlayerBinding

class EAMXPlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       var binding: FragmentPlayerBinding
        binding = FragmentPlayerBinding.inflate(inflater, container, false)



        return view
    }
}