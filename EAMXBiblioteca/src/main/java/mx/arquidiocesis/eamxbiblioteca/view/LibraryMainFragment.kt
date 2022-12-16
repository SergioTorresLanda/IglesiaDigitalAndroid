package mx.arquidiocesis.eamxbiblioteca.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.adapters.CategoryAdapter
import mx.arquidiocesis.eamxbiblioteca.adapters.HighlightAdapter
import mx.arquidiocesis.eamxbiblioteca.adapters.PagerAdapter
import mx.arquidiocesis.eamxbiblioteca.customviews.FindDialogFragment
import mx.arquidiocesis.eamxbiblioteca.customviews.LoadingFragment
import mx.arquidiocesis.eamxbiblioteca.customviews.ViewPagerLibraryFragment
import mx.arquidiocesis.eamxbiblioteca.databinding.FragmentLibraryMainBinding
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryViewModel
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryViewModelFactory
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert

class LibraryMainFragment : Fragment() {

    lateinit var binding: FragmentLibraryMainBinding
    private val factory by lazy { LibraryViewModelFactory(LibraryRepository()) }

    private val libraryViewModel: LibraryViewModel by viewModels { factory }
    private val loadingFragment by lazy { LoadingFragment() }

    companion object {
        fun newInstance(): LibraryMainFragment = LibraryMainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        binding.etBuscar.setOnClickListener {
            showDialogCongregation()
        }
    }

    override fun onResume() {
        super.onResume()

        showLoader()
        libraryViewModel.getHomeContent()
    }

    fun initObservers() {
        libraryViewModel.homeResponse.observe(viewLifecycleOwner) {
            val libraryFragments: ArrayList<ViewPagerLibraryFragment> = ArrayList()

            it.news.forEach {
                libraryFragments.add(ViewPagerLibraryFragment(it))
            }

            binding.apply {

                pager.apply {
                    adapter = activity?.let { it1 -> PagerAdapter(it1, libraryFragments, it.news) }
                }

                TabLayoutMediator(tab, pager) { tab, position -> }.attach()

                rvHighlights.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                    adapter = HighlightAdapter(it.featured) {
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame, LibraryDetailFragment.newInstance(it.id))
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }

                rvCategories.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                    adapter = CategoryAdapter(it.categories) {
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame, CategoryDetailFragment.newInstance(it))
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }
            }


            hideLoader()
        }

        libraryViewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    fun showDialogCongregation() {
        activity?.supportFragmentManager?.let {
            val dialog = FindDialogFragment.newInstance {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, LibraryDetailFragment.newInstance(it.id))
                transaction.addToBackStack(null)
                transaction.commit()
            }
            dialog.show(it, "")
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