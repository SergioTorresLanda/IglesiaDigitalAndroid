package mx.arquidiocesis.eamxregistromodule.ui.confirm

import android.widget.EditText
import mx.arquidiocesis.eamxcommonutils.util.deleteFocusEditText
import mx.arquidiocesis.eamxcommonutils.util.nextFocusEditText
import mx.arquidiocesis.eamxcommonutils.util.toast
import mx.arquidiocesis.eamxregistromodule.R
import mx.arquidiocesis.eamxregistromodule.databinding.EamxrConfirmCodeActivityBinding
import mx.arquidiocesis.eamxregistromodule.ui.confirm.model.EAMXConfirmCodeRequest

fun setFocus(mBinding: ArrayList<EditText>) {
    for (i in 0 until mBinding.size) {
        mBinding[i].deleteFocusEditText()
        when (i) {
            0 -> {
                mBinding[i].nextFocusEditText(null, mBinding[i + 1])
            }
            mBinding.size - 1 -> {
                mBinding[i].nextFocusEditText(mBinding[i - 1], null)
            }
            else -> {
                mBinding[i].nextFocusEditText(mBinding[i - 1], mBinding[i + 1])
            }
        }

    }
}

fun requestConfirmCode(userName: String, viewModel: EAMXConfirmCodeViewModel, edtElements: ArrayList<EditText>, context: EAMXConfirmCodeActivity) {
    var verifyCode = ""
    edtElements.forEach {
        verifyCode += it.text.toString()
    }
    if (verifyCode.length < 6) {
        context.toast(context.getString(R.string.enter_the_complete_code))
    }else {
        viewModel.requestCodeConfirm(EAMXConfirmCodeRequest(userName, verifyCode))
    }
}