package mx.arquidiocesis.eamxbiblioteca.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.library_detail_fragment.*
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.adapters.CollectionAdapter
import mx.arquidiocesis.eamxbiblioteca.customviews.LoadingFragment
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryDetailViewModel
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryDetailViewModelFactory

class LibraryDetailFragment : Fragment() {

    companion object {
        fun newInstance(idDetalle: Int): LibraryDetailFragment {
            val fragment = LibraryDetailFragment()
            fragment.idDetalle = idDetalle
            return fragment
        }
    }

    private val factory by lazy { LibraryDetailViewModelFactory(LibraryRepository()) }
    private val viewModel: LibraryDetailViewModel by viewModels { factory }
    private val loadingFragment by lazy { LoadingFragment() }

    private var idDetalle: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.library_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.obtenerDetalle(idDetalle)
        vpDetalle.isUserInputEnabled = false;

        showLoader()
        initObservers()
    }

    fun initObservers() {
        viewModel.response.observe(viewLifecycleOwner) {
            if (it.image.trim().isEmpty()) {
                ivDetalle.setImageResource(R.drawable.ic_empy)
            } else {
                Glide.with(ivDetalle)
                    .load(Uri.parse(it.image))
                    .into(ivDetalle)
            }
            tvDetalle.text = it.title
            var adapter = CollectionAdapter(this, it)
            vpDetalle.adapter = adapter

            TabLayoutMediator(tlDetalle, vpDetalle) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Descripción"
                        // tab.icon = drawable1
                    }
                    1 -> {
                        tab.text = "Recursos"
                        // tab.icon = drawable1
                    }
                }

            }.attach()
            hideLoader()
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
        }
    }

    private fun showLoader() {
        if (!loadingFragment.isAdded) {
            loadingFragment.show(childFragmentManager, "lOADER")
        }
    }

    private fun hideLoader() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismissAllowingStateLoss()
        }
    }
}