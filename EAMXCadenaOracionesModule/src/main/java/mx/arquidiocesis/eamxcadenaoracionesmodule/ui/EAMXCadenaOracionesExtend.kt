package mx.arquidiocesis.eamxcadenaoracionesmodule.ui

import androidx.fragment.app.FragmentActivity
import mx.arquidiocesis.eamxcadenaoracionesmodule.R
import mx.arquidiocesis.eamxcadenaoracionesmodule.databinding.EamxCadenaOracionesFragmentBinding
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.EAMXPraySend
import mx.arquidiocesis.eamxcadenaoracionesmodule.viewmodel.EAMXCadenaOracionesViewModel
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.EAMXEditText.validaMin
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import mx.arquidiocesis.eamxcommonutils.util.eamxLog
import mx.arquidiocesis.eamxcommonutils.util.imagen.ImagenProfile

fun initElements(
    binding: EamxCadenaOracionesFragmentBinding,
    activity: FragmentActivity?,
    viewModel: EAMXCadenaOracionesViewModel,
    callBack: EAMXHome,
    msgGuest:() -> Boolean,
    showLoader: () -> (Unit),
) {

    lateinit var player: String
    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int

    val urlImageProfile = eamxcu_preferences.getData(
        EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name,
        EAMXTypeObject.STRING_OBJECT
    ).toString()

    viewModel.getPrayers(userId)
    showLoader()

    binding.apply {
        callBack.showToolbar(true, AppMyConstants.cadenaOracion)

        "Set image profile urlImageProfile -> $urlImageProfile".log()
        swiperefresh.setOnRefreshListener {
            viewModel.getPrayers(userId)
        }

        ivAdd.setOnClickListener {
            if(!msgGuest()) {
                val dialog = BottomSheetDialog(binding.root.context)
                val view = activity!!.layoutInflater.inflate(R.layout.bottom_sheet_oracion, null)
                val imgUserSendPray = view.findViewById<CircleImageView>(R.id.imgUserSendPray)
                val txtName = view.findViewById<TextView>(R.id.txtName)
                val edtOracion = view.findViewById<EditText>(R.id.edtOracion)
                val btnCancel = view.findViewById<AppCompatButton>(R.id.btnCancel)
                val btnPost = view.findViewById<AppCompatButton>(R.id.btnPost)


                ImagenProfile().loadImageProfile(imgUserSendPray, view.context)

                val name = eamxcu_preferences.getData(
                    EAMXEnumUser.USER_NAME.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String

                val middleName = eamxcu_preferences.getData(
                    EAMXEnumUser.USER_MIDDLE_NAME.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String

                val lastName = eamxcu_preferences.getData(
                    EAMXEnumUser.USER_LAST_NAME.name,
                    EAMXTypeObject.STRING_OBJECT
                ) as String


                "NAME USER middleName-> $name  $middleName $lastName".log()
                "NAME USER lastName-> $name  $lastName $middleName".log()

                txtName.text = "$name $lastName $middleName"

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                btnPost.setOnClickListener {
                    if (validaMin(edtOracion, 0)) {
                        edtOracion.error = null

                        player = edtOracion.text.toString()

                        player.length
                        eamxLog("message $player")

                        val username = "$name $middleName $lastName"

                        val userId = eamxcu_preferences.getData(
                            EAMXEnumUser.USER_ID.name,
                            EAMXTypeObject.INT_OBJECT
                        ) as Int

                        if (!player.equals("")) {
                            viewModel.sendPrayer(EAMXPraySend(player, userId, username, 5))

                            showLoader()
                            edtOracion.error = null;
                            dialog.dismiss()
                        } else {
                            edtOracion.error = "El dato es requerido."
                        }
                    } else {
                        edtOracion.error = "No se puede enviar la oración vacía."
                    }
                }

                dialog.setContentView(view)
                dialog.show()
            }
        }
    }
}
