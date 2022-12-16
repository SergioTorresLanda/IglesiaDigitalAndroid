package mx.arquidiocesis.eamxbiblioteca.view

import android.content.ClipDescription
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.description_fragment.*
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.customviews.LoadingFragment
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryDetailViewModel
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryDetailViewModelFactory

class DescriptionFragment(var description: String) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.description_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(description.equals("")){
            laDescrip.playAnimation()
            laDescrip.visibility=View.VISIBLE
        }else{
            tvDescripcion.text = description
        }
    }
}