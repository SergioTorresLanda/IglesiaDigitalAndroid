package mx.arquidiocesis.eamxbiblioteca.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.adapters.FoundAdapter
import mx.arquidiocesis.eamxbiblioteca.databinding.FragmentFindDialogBinding
import mx.arquidiocesis.eamxbiblioteca.models.SearchResponse
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryViewModel
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryViewModelFactory
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert

class FindDialogFragment : DialogFragment() {

    lateinit var listener: (SearchResponse) -> Unit
    lateinit var binding: FragmentFindDialogBinding
    private val factory by lazy { LibraryViewModelFactory(LibraryRepository()) }
    private val libraryViewModel: LibraryViewModel by viewModels { factory }
    private val loadingFragment by lazy { LoadingFragment() }

    companion object {
        fun newInstance(listener: (SearchResponse) -> Unit): FindDialogFragment {
            val fragment = FindDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_find_dialog,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoader()
        libraryViewModel.getLibrarySearch("")
        initObservers()

        binding.ivClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.svFind.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    showLoader()
                    libraryViewModel.getLibrarySearch(it)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let {
                    showLoader()
                    libraryViewModel.getLibrarySearch(it)
                }
                return false
            }
        })
    }

    fun initObservers() {

        libraryViewModel.searchResponse.observe(this, {
            binding.rvFind.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = FoundAdapter(it) {
                    listener(it)
                    dialog?.dismiss()
                }
            }
            hideLoader()
        })

        libraryViewModel.errorResponse.observe(this, {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun showLoader() {
        if (!loadingFragment.isAdded) {
            loadingFragment.show(childFragmentManager, "LOADER")
        }
    }

    private fun hideLoader() {
        if (loadingFragment.isAdded) {
            loadingFragment.dismissAllowingStateLoss()
        }
    }
}