package com.wallia.eamxcomunidades.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.databinding.FragmentEamxMyCommunityBinding

class EAMXMyCommunityFragment : Fragment() {


    lateinit var binding: FragmentEamxMyCommunityBinding

    companion object {
        @JvmStatic
        fun newInstance(): EAMXMyCommunityFragment {
            return EAMXMyCommunityFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eamx_my_community,
            container,
            false
        )
        return binding.root
    }
}