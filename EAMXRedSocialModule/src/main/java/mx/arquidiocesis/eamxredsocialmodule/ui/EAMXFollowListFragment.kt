package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.FollowAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemFollowListBinding
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel


class EAMXFollowListFragment(val adapterFollow:FollowAdapter) :
    FragmentBase() {
    lateinit var binding: ItemFollowListBinding


    private val viewModel: RedSocialViewModel by lazy {
        getViewModel {
            RedSocialViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ItemFollowListBinding.inflate(layoutInflater, container, false)
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        binding.apply {
            rvFollow.apply {
                layoutManager =
                    LinearLayoutManager(
                        activity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                adapter = adapterFollow
            }
        }
    }


}