package mx.arquidiocesis.eamxcommonutils.util.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentReviewBinding
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.model.ReviewRespondeModel
import mx.arquidiocesis.eamxcommonutils.util.Adapter.ReviewAdapter
import mx.arquidiocesis.eamxcommonutils.util.Repository.ReviewRepository
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.viewModel.DELETE
import mx.arquidiocesis.eamxcommonutils.util.viewModel.ReviewViewModel
import mx.arquidiocesis.eamxcommonutils.util.viewModel.UPDATE

class ReviewFragment() : FragmentBase() {
    companion object {
        fun newInstance(callBack: EAMXHome): ReviewFragment {
            val fragment = ReviewFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModel: ReviewViewModel by lazy {
        getViewModel {
            ReviewViewModel(ReviewRepository(requireContext()))
        }
    }

    private lateinit var binding: FragmentReviewBinding
    var idIglesia = 0
    var star = 0f
    var isUpdate = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.comentarios)
        idIglesia = requireArguments().getInt("idIglesia")
        star = requireArguments().getFloat("star")
        binding.tvNombreIglesia.text = requireArguments().getString("nombre")
        if (star != null) {
            binding.rbIglesia.rating = star
        }

        initObservers()

        viewModel.getComentarios(idIglesia, 1)
        showLoader()
        binding.apply {
            btnPublicar.setOnClickListener {
                if (!etComentarios.text.isNullOrEmpty()) {
                    showLoader()
                    if (isUpdate == 0) {
                        viewModel.setComentario(
                            idIglesia,
                            etComentarios.text.toString(),
                            rbComentarios.rating.toInt()
                        )
                    } else {
                        viewModel.updateComentario(
                            idIglesia, isUpdate,
                            etComentarios.text.toString(),
                            rbComentarios.rating.toInt()
                        )
                    }
                }

            }
            btnCancel.setOnClickListener {
                if (isUpdate == 0) {
                    UtilAlert.Builder()
                        .setTitle("¿Seguro que quieres eliminar tu comentario?")
                        .setTextButtonOk(getString(R.string.seguir))
                        .setTextButtonCancel(getString(R.string.eliminar))
                        .setListener { action ->
                            when (action) {
                                UtilAlert.ACTION_ACCEPT -> {

                                }
                                UtilAlert.ACTION_CANCEL -> {
                                    cvComentario.visibility = View.GONE
                                    tvAddReview.visibility = View.VISIBLE
                                }
                            }
                        }
                        .build()
                        .show(childFragmentManager, "")

                } else {
                    cvComentario.visibility = View.GONE
                }
            }
            tvAddReview.setOnClickListener {
                clearReview()
                tvAddReview.visibility = View.GONE
            }
        }
    }

    fun initObservers() {
        viewModel.apply {
            opinionResponse.observe(viewLifecycleOwner) { item ->
                hideLoader()
                binding.cvComentario.visibility = View.GONE
                setRating(item)
                viewModel.getComentarios(idIglesia, 1)

            }
            opinionListResponse.observe(viewLifecycleOwner) {
                hideLoader()
                var list: MutableList<ReviewModel> = mutableListOf()
                var isMyReview = false
                if (it.my_review != null) {
                    binding.cvComentario.visibility = View.GONE
                    list.add(it.my_review)
                    isMyReview = true
                    if (!it.other_reviews.isNullOrEmpty()) {
                        it.other_reviews.forEach {
                            list.add(it)
                        }
                    }
                } else if (!it.other_reviews.isNullOrEmpty()) {
                    list = it.other_reviews as MutableList<ReviewModel>
                }
                binding.apply {
                    tvNumReview.text = "${list.size} comentarios"
                    rvComentarios.apply {
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        adapter = ReviewAdapter(
                            requireContext(),
                            list,
                            binding.rvComentarios,
                            isMyReview
                        ) { item, etiqueta ->
                            when (etiqueta) {
                                "EDITAR" -> {
                                    cvComentario.apply {
                                        visibility = View.VISIBLE
                                        etComentarios.setText(item.review!!)
                                        rbComentarios.rating = if (item.rating != null) item.rating.toFloat() else 0.0F
                                        isUpdate = item.id!!
                                    }
                                }
                                "ELIMINAR" -> {
                                    UtilAlert.Builder()
                                        .setTitle("¿Quieres eliminar tu comentario?")
                                        .setTextButtonOk(getString(R.string.eliminar))
                                        .setTextButtonCancel(getString(R.string.text_button_cancel))
                                        .setListener { action ->
                                            when (action) {
                                                UtilAlert.ACTION_ACCEPT -> {
                                                    showLoader()
                                                    viewModel.deleteComentario(idIglesia, item.id!!)
                                                }
                                                UtilAlert.ACTION_CANCEL -> {

                                                }
                                            }
                                        }
                                        .build()
                                        .show(childFragmentManager, "")
                                }
                            }
                        }
                    }
                }
            }

            reviewDeleteResponse.observe(viewLifecycleOwner) {
                hideLoader()
                clearReview()
                setRating(it)
                viewModel.getComentarios(idIglesia, 1)
            }
            reviewUpdateResponse.observe(viewLifecycleOwner) {
                hideLoader()
                setRating(it)
                binding.cvComentario.visibility = View.GONE
                viewModel.getComentarios(idIglesia, 1)
            }
            errorResponse.observe(viewLifecycleOwner) {
                hideLoader()
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage(it)
                    .build()
                    .show(childFragmentManager, "")
            }
        }

    }

    private fun setRating(response: ReviewRespondeModel?) {
        response?.let { resp ->
            resp.rating?.let { ratingSecond ->
                binding.rbIglesia.rating =
                    if (ratingSecond.isEmpty()) 0.0f else ratingSecond.toFloat()
            }
        } ?: kotlin.run {
            binding.rbIglesia.rating = 0.0f
        }
    }

    private fun clearReview() {
        binding.apply {
            cvComentario.visibility = View.VISIBLE
            etComentarios.setText("")
            rbComentarios.rating = 0.0f
            isUpdate = 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }


}