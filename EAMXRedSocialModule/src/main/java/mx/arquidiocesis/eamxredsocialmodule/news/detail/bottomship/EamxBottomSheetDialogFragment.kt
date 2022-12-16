package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.common.EAMXBackHandler
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxRactionsBinding
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.adapter.EAMXAutorAdapter
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.Autor
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.EAMXGetInPublicationRequest
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.totallike.EAMXTotalReactionRequest


class EamxBottomSheetDialogFragment(val idReaction: Int, val eamxBackHandler: EAMXBackHandler) : BottomSheetDialogFragment() {

    lateinit var mBinding: EamxRactionsBinding
    lateinit var viewModel: EAMXTotalReactionViewModel
    val adapter = EAMXAutorAdapter()
    companion object {
        val TAG = EamxBottomSheetDialogFragment::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = EamxRactionsBinding.inflate(layoutInflater, container,  false)
        setViewModel()
        initObservers()

        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnCancelListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(R.id.bottomSheetDialog)

            parentLayout?.let {
                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return mBinding.root
    }

    fun setViewModel() {
        viewModel = ViewModelProvider(this).get(EAMXTotalReactionViewModel::class.java)
    }


    fun initObservers() {
        viewModel.responseGeneric.observe(this.viewLifecycleOwner, {
            when (it.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
//                    eamxBackHandler.showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
//                    eamxBackHandler.hideProgressBarCustom()
                    val arrayRes = it.successData?.topReactions
                    var total= 0
                    if (arrayRes != null) {
                        for (index in arrayRes) {
                            total = index.total
                        }
                        mBinding.txtAllLike.text = total.toString()
                    }
                }

                EAMXStatusRequestEnum.FAILURE -> {
                    eamxBackHandler.hideProgressBarCustom()
                    it.errorData?.let { errorMessage ->
                        toast(errorMessage)
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    eamxBackHandler.hideProgressBarCustom()
                }
            }
        })

        viewModel.responseGenericTotalTop.observe(this.viewLifecycleOwner, {
            when (it.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {
//                    eamxBackHandler.showProgressBarCustom()
                }
                EAMXStatusRequestEnum.SUCCESS -> {
//                    eamxBackHandler.hideProgressBarCustom()

                    mBinding.progressBar.visibility = View.GONE

                    eamxLog("response", "${it.successData?.data?.resp}")
                    val respData = it.successData?.data?.resp
                    val arrayAutor = mutableListOf<Autor>()

                    if (respData != null) {
                        if (respData.isNotEmpty()) {
                            for (autor in respData) {
                                arrayAutor.add(Autor(autor.autor!!.FIIDEMPLEADO, autor.autor!!.imagen, autor.autor!!.nombre))
                            }
                        }else {
                            toast("No contienes ningún me gusta")

                        }
                    }
//                    if (respData != null) {
//                        for (autor in respData) {
//                            arrayAutor.add(Autor(autor.autor!!.FIIDEMPLEADO, autor.autor!!.imagen, autor.autor!!.nombre))
//                        }
//                    }else {
//                        toast("No contienes ningún me gusta")
//                    }

                    mBinding.rvAllReaction.adapter = adapter
                    var list = arrayAutor
                    adapter.submitList(list)
                    adapter.notifyDataSetChanged()
                }

                EAMXStatusRequestEnum.FAILURE -> {
                    eamxBackHandler.hideProgressBarCustom()
                    it.errorData?.let { errorMessage ->
                        toast(errorMessage)
                    }
                }
                EAMXStatusRequestEnum.NONE -> {
                    eamxBackHandler.hideProgressBarCustom()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding.apply {
            viewModel.requestTotalReaction(EAMXTotalReactionRequest(idReaction))
            viewModel.requestGetInPublication(EAMXGetInPublicationRequest(post_id = idReaction))
        }
    }
}
