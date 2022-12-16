package mx.arquidiocesis.servicios.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.adapter.CommunityAdapter
import mx.arquidiocesis.servicios.customviews.DialogFindCommunityFragment
import mx.arquidiocesis.servicios.databinding.FragmentMyCommunitiesMentionBinding
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel

class MyCommunitiesMentionFragment : FragmentBase() {

    lateinit var binding: FragmentMyCommunitiesMentionBinding

    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommunitiesMentionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        showLoader()
        viewModel.getMainCommunity()

        binding.apply {
            etBusarComunidad.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showDialogFindCommunity()
                }
            }

            etBusarComunidad.setOnClickListener {
                showDialogFindCommunity()
            }

            ibSearch.setOnClickListener {
                showDialogFindCommunity()
            }
        }
    }

    fun initObservers() {
        viewModel.mainCommunityResponse.observe(viewLifecycleOwner) {
            binding.apply {
                if (it.assigned != null) {
                    val item = it.assigned

                    tvComunidad.text = item.name

                    if (item.image_url.isEmpty()) {
                        Glide.with(requireContext())
                            .load(R.drawable.default_image)
                            .into(ivComunidad)
                    } else {
                        Glide.with(requireContext())
                            .load(Uri.parse(item.image_url))
                            .into(ivComunidad)
                    }
                }

                if (!it.locations.isNullOrEmpty()) {
                    rvListaComunidades.apply {
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = CommunityAdapter(requireContext(), it.locations) {
                            print("")
                        }
                    }
                } else {
                    tvFavoritas.visibility = View.GONE
                    rvListaComunidades.visibility = View.GONE
                }
            }
            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build().show(childFragmentManager, "")
        }
    }

    /*
    * private fun showDialogFindChurch() {
        activity?.supportFragmentManager?.let {
            val dialog = DialogFindChurchFragment.newInstance()
            dialog.churchSelected = {
                scheduleMention(it.id)
            }
            dialog.show(it, "Show Dialog Congregation")
        }
    }*/

    fun showDialogFindCommunity(){
        activity?.supportFragmentManager?.let {
            val dialog = DialogFindCommunityFragment.newInstance()
            dialog.communitySelected = {
                print("")
                //scheduleMention(it.id)
            }
            dialog.show(it, "Show Dialog Congregation")
        }
    }
}