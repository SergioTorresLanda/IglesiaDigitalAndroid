package mx.arquidiocesis.eamxgeneric.fragments.otherbanks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.databinding.EamxOtherBanksFragmentBinding

class EAMXOtherBanksFragment : EAMXBaseFragment() {
    val TAG = EAMXOtherBanksFragment::class.java.simpleName

    val REQUEST_SCAN = 100
    val REQUEST_AUTOTEST = 200

    lateinit var mBinding: EamxOtherBanksFragmentBinding
    lateinit var callBack: EAMXHome

    override fun getLayout() = R.layout.eamx_other_banks_fragment

    override fun initBinding(view: View) {
        mBinding = EamxOtherBanksFragmentBinding.bind(view)
    }

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {
        initListener()
//        requireActivity().toolbar.visibility = View.GONE

        mBinding.apply {
            ivCameraIcon.setOnClickListener{
                val intent = ScanCardIntent.Builder(requireContext()).build();
                startActivityForResult(intent, 1);
            }
        }
    }

    override fun setViewModel() {}

    override fun onDestroyView() {
        super.onDestroyView()
//        requireActivity().toolbar.visibility = View.VISIBLE
        callBack.restoreToolbar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val card: Card? = data?.getParcelableExtra(ScanCardIntent.RESULT_PAYCARDS_CARD)
                mBinding.apply {
                    edtCardNumber.setText(card?.cardNumber)
                    etExpireDate.setText(card?.expirationDate)
                }
            }
        }
    }

    companion object {
        fun newInstance(callBack: EAMXHome) : EAMXOtherBanksFragment {
            var othersBanks = EAMXOtherBanksFragment()
            othersBanks.callBack = callBack
            return othersBanks
        }
    }
}