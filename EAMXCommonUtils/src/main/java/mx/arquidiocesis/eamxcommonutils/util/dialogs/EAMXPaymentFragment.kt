package mx.arquidiocesis.eamxcommonutils.util.dialogs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentEamxPaymentBinding
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.otherbanks.EAMXOtherBanksFragment
import mx.arquidiocesis.eamxcommonutils.util.validation.EAMXFilterAmountMoney
import mx.arquidiocesis.eamxcommonutils.util.socialsupport.crypto.EAMXEncripAES
import mx.arquidiocesis.eamxcommonutils.util.socialsupport.qr.EAMXQRActivity

class EAMXPaymentFragment(
        val callBack: EAMXHome,
        val containerId: Int,
        val listener : (actionOverView : Boolean) -> Unit) :
    BottomSheetDialogFragment() {

    lateinit var binding: FragmentEamxPaymentBinding

    private var clickBancoAzteca = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEamxPaymentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            edtAmountPayAzteca.apply {
                filters = arrayOf(EAMXFilterAmountMoney(5,2))
            }

            btnContinueBottomFragment.setOnClickListener {

                val amount = if(edtAmountPayAzteca.text == null ||
                        edtAmountPayAzteca.text.toString().isNullOrEmpty())
                    0.0f else edtAmountPayAzteca.text.toString().toFloat()

                if(amount == 0.0f) {
                    Toast.makeText(activity, "El monto debe ser mayor a cero", Toast.LENGTH_SHORT ).show()
                }else{
                    val messageEncrypt = EAMXEncripAES.encrypt(
                            getString(
                                    R.string.message,
                                    eamxcu_preferences.getData(
                                            EAMXEnumUser.USER_NAME.name,
                                            EAMXTypeObject.STRING_OBJECT
                                    ).toString(),
                                    eamxcu_preferences.getData(
                                            EAMXEnumUser.USER_LAST_NAME.name,
                                            EAMXTypeObject.STRING_OBJECT
                                    ) as String,
                                    "45678987654567897",
                                    "AndroidManifest.xml",
                                    "200.00"
                            )
                    )

                    try {
                        val url = "bancoaztecadl://?sendToAcc?$messageEncrypt&tkn=34813673"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(url)
                        }
                        startActivity(intent)
                    }catch (ex : ActivityNotFoundException){
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=mx.com.bancoazteca.bazdigitalmovil")));
                    }
                }
            }

            layoutBancoAzteca.setOnClickListener {
                clickBancoAzteca = !clickBancoAzteca
                if (clickBancoAzteca) {
                    contentShowBancoAzteca.visibility = View.VISIBLE
                    imgShow.setImageDrawable(context?.applicationContext?.let { it1 ->
                        ContextCompat.getDrawable(
                            it1, R.drawable.ic_hide
                        )
                    })
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
                listener(true)
                activity?.supportFragmentManager?.commit {
                    replace(containerId, EAMXOtherBanksFragment.newInstance(callBack, listener))
                    addToBackStack(EAMXOtherBanksFragment::class.java.simpleName)
                }
            }
        }
    }

}