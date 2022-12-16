package mx.arquidiocesis.eamxredsocialmodule.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.EAMXFormatDate
import mx.arquidiocesis.eamxcommonutils.util.buildTextSuccess
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxCommentFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.adapter.CommentAdapter
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemMediaPickerBinding
import mx.arquidiocesis.eamxredsocialmodule.model.CommentModel
import mx.arquidiocesis.eamxredsocialmodule.model.MultimediaModel
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultMultiProfileModel
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel

class EAMXComentFragment(
    var postModel: PostModel,
    var list: List<ResultMultiProfileModel>? = null
) : FragmentBase() {

    lateinit var binding: EamxCommentFragmentBinding
    lateinit var commentAdapter: CommentAdapter
    lateinit var mBottomSheetFragment: EAMXBottomSheetComment
    var model = MutableLiveData<CommentModel?>()
    var idPost = 0
    var listComent = mutableListOf<CommentModel>()
    val viewmodel: RedSocialViewModel by lazy {
        getViewModel {
            RedSocialViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initObservers()
        binding = EamxCommentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    fun initView() {
        binding.apply {
            ilPublication.apply {
                idPost = postModel.id
                postModel.scope.typeId?.let { typeId ->
                    if (typeId > 1) {
                        txtName.text = postModel.scope.name
                        if (!postModel.scope.image.isNullOrEmpty()) {
                            Glide.with(root.context)
                                .load(postModel.scope.image)
                                .centerCrop()
                                .into(imgPriest)
                        }
                    } else {
                        txtName.text = postModel.author.name
                        if (!postModel.author.image.isNullOrEmpty()) {
                            Glide.with(root.context)
                                .load(postModel.author.image)
                                .centerCrop()
                                .into(imgPriest)
                        }
                    }
                }
                val dateResponse = postModel.createdAt.toLong()
                val objectFormat = EAMXFormatDate(root.context)
                val miFecha = objectFormat.diferencia(dateResponse)
                txtDate.text = miFecha

                txtMessage.buildTextSuccess(postModel.content)
                tvLike.text = postModel.totalReactions.toString()
                tvComent.text = postModel.totalComments.toString()
                ivOption.visibility = View.GONE
                viewTop.visibility = View.GONE

                tvLike.setOnClickListener {
                    //  onItemClickListener(postModel, LIKE)
                }
                tvComent.setOnClickListener {
                    model.value = null
                    showBottomSheet()
                }
                if (postModel.multimedia.isEmpty()) {
                    iMediaGallery.cGallery.visibility = View.GONE
                } else {
                    iMediaGallery.cGallery.visibility = View.VISIBLE
                }
                iMediaGallery.cGallery.setOnClickListener {
                    // onItemClickListener(postModel, "")
                }

                resetContainer(iMediaGallery.iThumbnail1)
                resetContainer(iMediaGallery.iThumbnail2)
                resetContainer(iMediaGallery.iThumbnail3)
                resetContainer(iMediaGallery.iThumbnail4)


                for ((index, media) in postModel.multimedia.withIndex()) {
                    when (index) {
                        0 -> {
                            displayContent(iMediaGallery.iThumbnail1, media)
                        }
                        1 -> {
                            displayContent(iMediaGallery.iThumbnail2, media)
                        }
                        2 -> {
                            displayContent(iMediaGallery.iThumbnail4, media)
                        }
                        3 -> {
                            displayContent(iMediaGallery.iThumbnail3, media)
                            if (postModel.multimedia.size > 4) {
                                iMediaGallery.iThumbnail4.vTransparency.visibility = View.VISIBLE
                                iMediaGallery.iThumbnail4.tvMoreMedia.visibility = View.VISIBLE
                                iMediaGallery.iThumbnail4.tvMoreMedia.text =
                                    "+ ${postModel.multimedia.size - 3}"
                            }
                        }
                    }
                }
            }
            showSkeleton(true)
            viewmodel.getComment(idPost)
            showBottomSheet()
        }
    }

    fun initObservers() {
        viewmodel.responseComment.observe(viewLifecycleOwner) {
            binding.apply {
                it.result?.let { resultModel ->
                    if (!resultModel.Comments.isNullOrEmpty()) {
                        resultModel.Comments.forEach {
                            listComent.add(it)
                        }
                    }
                    if (resultModel.Pagination != null) {
                        if (resultModel.Pagination.hasMore) {
                            viewmodel.getComment(idPost, resultModel.Pagination.next)
                        } else {
                            ilPublication.tvComent.text = listComent.size.toString()
                            commentAdapter = CommentAdapter(
                                requireContext(),
                                listComent,
                                rvReview,
                                list,
                                viewmodel.getSuper()
                            ) { Etiqueta, item ->
                                //  model.value = it
                                when (Etiqueta) {
                                    EDITAR -> {
                                        model.value = item
                                        showBottomSheet()
                                    }
                                    ELIMINAR -> {
                                        UtilAlert.Builder()
                                            .setTitle("¿Seguro que quieres eliminar esta publicación?")
                                            .setTextButtonOk("Aceptar")
                                            .setTextButtonCancel("Cancelar")
                                            .setListener { action ->
                                                when (action) {
                                                    UtilAlert.ACTION_ACCEPT -> {
                                                        showSkeleton(true)
                                                        viewmodel.deleteComment(item.id)
                                                    }
                                                    UtilAlert.ACTION_CANCEL -> {

                                                    }
                                                }
                                            }
                                            .build()
                                            .show(childFragmentManager, tag)
                                    }
                                    COMENTARIO -> {
                                        model.value = null
                                        showBottomSheet(item.id)
                                    }

                                }
                            }
                            showSkeleton(false)
                            rvReview.apply {
                                layoutManager =
                                    LinearLayoutManager(
                                        activity,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                adapter = commentAdapter

                            }
                        }
                    }
                }
            }
        }
        viewmodel.responseDelete.observe(viewLifecycleOwner) {
            showSkeleton(false)
            listComent = mutableListOf()
            viewmodel.getComment(idPost)
        }

        viewmodel.error.observe(viewLifecycleOwner) {
            showSkeleton(false)
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")
        }
    }

    fun showBottomSheet(respuesta: Int? = null) {
        mBottomSheetFragment = EAMXBottomSheetComment(idPost, model, list, respuesta) {
            showSkeleton(it)
            listComent = mutableListOf()
            viewmodel.getComment(idPost)
        }
        mBottomSheetFragment.show(
            requireActivity().supportFragmentManager,
            "EAMXBottomSheetComment"
        )
    }

    private fun resetContainer(item: ItemMediaPickerBinding) {
        item.itemRoot.visibility = View.GONE
        item.vTransparency.visibility = View.GONE
        item.ivPlay.visibility = View.GONE
        item.vTransparency.visibility = View.GONE
        item.tvMoreMedia.visibility = View.GONE
    }

    private fun displayContent(item: ItemMediaPickerBinding, media: MultimediaModel) {
        val placeHolderDrawable = ContextCompat.getDrawable(
            item.itemRoot.context,
            R.drawable.ic_placeholder
        )?.apply {
            setTintList(
                ContextCompat.getColorStateList(
                    item.itemRoot.context,
                    R.color.grayLevelThree
                )
            )
        }

        Glide.with(item.itemRoot.context)
            .load(media.url)
            .centerCrop()
            .thumbnail(0.1f)
            .placeholder(placeHolderDrawable)
            .into(item.ivThumbnail)

        item.itemRoot.visibility = View.VISIBLE
        if (!media.format.contains("image")) {
            item.vTransparency.visibility = View.VISIBLE
            item.ivPlay.visibility = View.VISIBLE
        }
    }

    private fun showSkeleton(show: Boolean) {
        binding.apply {
            llSkeleton.apply {
                llSkeleton.visibility = if (show) View.VISIBLE else View.GONE
                if (show) {
                    shimmerFaithful.startShimmer()
                } else {
                    shimmerFaithful.stopShimmer()
                }
            }
        }
    }
}