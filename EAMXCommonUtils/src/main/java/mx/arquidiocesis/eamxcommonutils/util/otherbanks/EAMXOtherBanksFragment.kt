package mx.arquidiocesis.eamxcommonutils.util.otherbanks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.ScanCardIntent
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.databinding.EamxOtherBanksFragmentUtilBinding

class EAMXOtherBanksFragment : EAMXBaseFragment() {
    val TAG = EAMXOtherBanksFragment::class.java.simpleName

    val REQUEST_SCAN = 100
    val REQUEST_AUTOTEST = 200

    lateinit var mBinding: EamxOtherBanksFragmentUtilBinding
    lateinit var callBack: EAMXHome
    lateinit var listener: (actionOverView : Boolean) -> (Unit)

    override fun getLayout() = R.layout.eamx_other_banks_fragment_util

    override fun initBinding(view: View) {
        mBinding = EamxOtherBanksFragmentUtilBinding.bind(view)
    }

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {
        initListener()

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
        listener(false)
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
        fun newInstance(callBack: EAMXHome, listener: (actionOverView : Boolean) -> (Unit)) : EAMXOtherBanksFragment {
            var othersBanks = EAMXOtherBanksFragment()
            othersBanks.callBack = callBack
            othersBanks.listener = listener
            return othersBanks
        }
    }
}