package mx.arquidiocesis.eamxbiblioteca.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.adapters.SubcategoryAdapter
import mx.arquidiocesis.eamxbiblioteca.customviews.DialogMessage
import mx.arquidiocesis.eamxbiblioteca.customviews.LoadingFragment
import mx.arquidiocesis.eamxbiblioteca.databinding.FragmentCategoryDetailBinding
import mx.arquidiocesis.eamxbiblioteca.models.Category
import mx.arquidiocesis.eamxbiblioteca.repository.LibraryRepository
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryViewModel
import mx.arquidiocesis.eamxbiblioteca.viewmodel.LibraryViewModelFactory
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

class CategoryDetailFragment : Fragment() {

    companion object {
        fun newInstance(category: Category): CategoryDetailFragment {
            val fragment = CategoryDetailFragment()
            fragment.category = category
            return fragment
        }
    }

    lateinit var binding: FragmentCategoryDetailBinding
    lateinit var category: Category
    private val factory by lazy { LibraryViewModelFactory(LibraryRepository()) }

    private val libraryViewModel: LibraryViewModel by viewModels { factory }
    private val loadingFragment by lazy { LoadingFragment() }

    var tagText = ""
    var order = ""
    var age = ""
    var topic = false

    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_category_detail,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        Glide.with(requireContext())
            .load(Uri.parse(category.image))
            .into(binding.ivCategoryImage)

        binding.tvCategoryName.text = category.name

        binding.tvOrderFilter.setOnClickListener {
            DialogMessage.newInstance(order, age, topic)
                .show((activity as FragmentActivity).supportFragmentManager, tag)
        }

        binding.ibBuscar.setOnClickListener {
            if (binding.etBuscar.text.toString() != "") {
                getContentCategories()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getContentCategories()
    }

    private fun initObservers() {
        libraryViewModel.responseContentCategory.observe(viewLifecycleOwner) {
            binding.rvSubCategories.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = SubcategoryAdapter(it.contents) {
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame, LibraryDetailFragment.newInstance(it.id))
                    transaction.addToBackStack(null)
                    transaction.commit()
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

        DialogMessage.limpiarFiltro.observe(viewLifecycleOwner) {

            tagText = ""
            order = ""
            topic = false
            age = ""

            getContentCategories()
        }

        DialogMessage.recienteAntiguio.observe(viewLifecycleOwner) {
            if (it)
                order = "ASC"
            else
                order = ""

            getContentCategories()
        }

        DialogMessage.antiguioReciente.observe(viewLifecycleOwner) {
            if (it)
                order = "DESC"
            else
                order = ""
            getContentCategories()
        }

        DialogMessage.mayorMenor.observe(viewLifecycleOwner) {
            if (it)
                order = "TOP"
            else
                order = ""
            getContentCategories()
        }

        DialogMessage.menorMayor.observe(viewLifecycleOwner) {
            order = "LOW"
            getContentCategories()
        }

        DialogMessage.rangoUno.observe(viewLifecycleOwner) {
            if (it)
                age = "CHILDREN"
            else
                age = ""
            getContentCategories()
        }

        DialogMessage.rangoDos.observe(viewLifecycleOwner) {
            if (it)
                age = "YOUTH"
            else
                age = ""

            getContentCategories()
        }

        DialogMessage.rangoTres.observe(viewLifecycleOwner) {
            if (it)
                age = "ADULTS"
            else
                age = ""
            getContentCategories()
        }

        DialogMessage.themeFilter.observe(viewLifecycleOwner) {
            topic = it
            getContentCategories()
        }

    }

    fun getContentCategories() {
        showLoader()
        tagText = binding.etBuscar.text.toString()

        libraryViewModel.getContentCategory(userId.toString(), category.code, tagText, order, topic, age)
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