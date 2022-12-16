package mx.arquidiocesis.eamxgeneric.fragments.socialsupport

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.eamx_home_fragment.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.socialsupport.crypto.EAMXEncripAES
import mx.arquidiocesis.eamxcommonutils.util.socialsupport.qr.EAMXQRActivity
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.databinding.EamxSocialSupportBinding


class CustomBottomSheetDialogFragment(val callBack: EAMXHome) : BottomSheetDialogFragment() {

    lateinit var mBinding: EamxSocialSupportBinding
    private var clickBancoAzteca = false
    var lastClickTime: Long = 0
    val name = eamxcu_preferences.getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT).toString()
    val lastName = eamxcu_preferences.getData(EAMXEnumUser.USER_LAST_NAME.name, EAMXTypeObject.STRING_OBJECT) as String
    val account = "45678987654567897"
    val schema = "AndroidManifest.xml"

    companion object {
        val TAG = CustomBottomSheetDialogFragment::class.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = EamxSocialSupportBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mBinding.apply {

            layoutBancoAzteca.setOnClickListener {
                clickBancoAzteca = !clickBancoAzteca

                if (clickBancoAzteca) {
                    contentShowBancoAzteca.visibility = View.VISIBLE
                    imgShow.setImageDrawable(context?.applicationContext?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1, R.drawable.ic_hide
                        )
                    })

                    btnContinueBottomFragment.setOnClickListener {
                        if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
                            return@setOnClickListener
                        }
                        lastClickTime = SystemClock.elapsedRealtime()

                        val msg = EAMXEncripAES.encrypt(getString(
                                R.string.message,
                                name,
                                lastName,
                                account,
                                schema,
                                "200.00"
                            ))
                        Toast.makeText(context, "Encriptado: $msg", Toast.LENGTH_SHORT).show()

                        val msgDes = EAMXEncripAES.decrypt(msg.toString())
                        Toast.makeText(context, "Desencriptado: $msgDes", Toast.LENGTH_SHORT).show()

//                        val data = EAMXEncripAES.decrypt("DX7XCx7dH-0njqI5ThKS7mLf67nCg0rA_gSbsXbiDZl2BK51xGEwrtzrb_Hk6E4yOKidjJqfZED2Nsa5MdaHJGxqqSdhQo59_mR5s5mudIBdfWTSbTTfUyDDGVNXyARMhQnJjBR5dqpxCHt9I5SIZ_fs17g_tCkObCxDDsUbZOqhUTj1baeEtLVKa-R60bJK5DKYsrHED_pKqgiwtWinIzaHyhLEDYoX3zwazLXMHbulg_1MT23YqWCd7FYFJQMu")

                        val url = "bancoaztecadl://?sendToAcc?$msg&tkn=34813673"
                        eamxLog("uri", url)

                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    }
                } else {
                    contentShowBancoAzteca.visibility = View.GONE
                    imgShow.setImageDrawable(context?.applicationContext?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1, R.drawable.ic_show
                        )
                    })
                }
            }

            layoutQR.setOnClickListener {
                dismiss()
                startActivity(Intent(context, EAMXQRActivity::class.java))
            }

            layoutOthers.setOnClickListener {
                dismiss()
                if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
                    return@setOnClickListener
                }
                lastClickTime = SystemClock.elapsedRealtime()

                /*
                activity?.supportFragmentManager?.commit {
                    replace(R.id.contentFragment , EAMXOtherBanksFragmentGeneric.newInstance(callBack))
                    addToBackStack(EAMXOtherBanksFragmentGeneric::class.java.simpleName)
                }*/
            }
        }
    }
}
