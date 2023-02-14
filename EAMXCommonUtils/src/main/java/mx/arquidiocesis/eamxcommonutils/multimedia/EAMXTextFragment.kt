package mx.arquidiocesis.eamxcommonutils.multimedia

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentPdfBinding
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentTextBinding

class EAMXTextFragment : FragmentBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var binding: FragmentTextBinding

        binding = FragmentTextBinding.inflate(inflater, container, false)
        val text = arguments?.getString("text")
        val image = arguments?.getString("image")

        if (text != null){
            binding.text.setText(text.toString())
        }else{
            binding.text.setText("Ha ocurrido un error, intente nuevamente.")
        }
        if (image!=null){
            //binding.imageT.setImageURI()

        }else{
           // binding.imageT.
        }

        return binding.root
        /*
        return(FragmentTextBinding.inflate(inflater, container, false)
            .text.apply {
                if (text!=null)
                setText(Uri.parse(arguments?.getString("urlWeb")).toString())
            }
            .imageT.apply {

            }

                ).rootView

        */

    }
}