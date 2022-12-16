package com.wallia.eamxcomunidades.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wallia.eamxcomunidades.R

class  EAMXAgregarComunidadSacerdoteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_eamx_agregar_comunidad_sacerdote,
            container,
            false
        )
    }
}