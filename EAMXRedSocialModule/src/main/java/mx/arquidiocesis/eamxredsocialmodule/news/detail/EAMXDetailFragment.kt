package mx.arquidiocesis.eamxredsocialmodule.news.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import mx.arquidiocesis.eamxcommonutils.api.core.status.EAMXStatusRequestEnum
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.EamxDetailFragmentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.model.Publication

class EAMXDetailFragment : EAMXBaseFragment() {
    lateinit var mBinding: EamxDetailFragmentBinding
    lateinit var viewModel: EAMXDetailFragmentViewModel
    lateinit var modelPublication: PostModel
    var lastClickTime: Long = 0
    var urlPrueba = ""

    override fun setViewModel() {
        viewModel = ViewModelProvider(this).get(EAMXDetailFragmentViewModel::class.java)
    }

    override fun getLayout() = R.layout.eamx_detail_fragment

    override fun initBinding(view: View) {
        mBinding = EamxDetailFragmentBinding.bind(view)
    }

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {
        viewModel.responseGenericLike.observe(this.viewLifecycleOwner) {
            when (it.statusRequest) {
                EAMXStatusRequestEnum.LOADING -> {}
                EAMXStatusRequestEnum.SUCCESS -> {
                    eamxLog("response", "${it.successData?.data?.countReact}")
//                    var like = mBinding.txtCount.text.toString().toInt()
                    var miLike = it.successData?.data?.countReact.toString().toInt()
//                    var totalLikes = miLike + like
                    mBinding.txtCount.text = "$miLike"
                    if (miLike == 1) mBinding.icHands.visibility =
                        View.VISIBLE else mBinding.icHands.visibility = View.INVISIBLE
                }
                EAMXStatusRequestEnum.FAILURE -> {
                    hideProgressBarCustom()
                }
                EAMXStatusRequestEnum.NONE -> {
                    hideProgressBarCustom()
                }
            }
        }
    }

    override fun initView(view: View) {
        requireArguments()?.let {
            modelPublication = it.getParcelable(EAMXEnumUser.PUBLICATIONS.name)!!
        }
        if (modelPublication.multimedia.isEmpty()) {
            mBinding.constrainView.visibility = View.GONE
        }else {
            mBinding.constrainView.visibility = View.VISIBLE
        }
        initElements()
    }


}