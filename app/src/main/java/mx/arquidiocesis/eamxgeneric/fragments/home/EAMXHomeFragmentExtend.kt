package mx.arquidiocesis.eamxgeneric.fragments.home

import android.view.View
import androidx.fragment.app.Fragment
import com.wallia.eamxcomunidades.ui.EAMXCommunitiesPrincipalFragment
import mx.arquidiocesis.eamxcadenaoracionesmodule.ui.EAMXCadenaOracionesFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXSignOut
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxdonaciones.ui.ProfileDonacionesFragment
import mx.arquidiocesis.eamxgeneric.R
import mx.arquidiocesis.eamxgeneric.fragments.WebViewHomeFragment
import mx.arquidiocesis.eamxlivestreammodule.ProfileLiveFragment
import mx.arquidiocesis.eamxredsocialmodule.EAMXContentFragmentRedSocial
import mx.arquidiocesis.misiglesias.ui.ProfileChurchFragment
import mx.arquidiocesis.oraciones.ui.ProfileOrationFragment
import mx.arquidiocesis.servicios.ui.ServicesMainFragment
import mx.upax.formacion.ui.FormationFragment

fun EAMXHomeFragment.initOnClickListener(signOut: EAMXSignOut) {
    mBinding.apply {
        cardLiveEvent.setOnClickListener {
            if (cardLiveEvent.cardBackgroundColor.defaultColor == requireContext().getColor(R.color.color_card_eventos_exist))
                eamxBackHandler.changeFragment(
                    ProfileLiveFragment.newInstance(callBack!!),
                    R.id.contentFragment,
                    ProfileLiveFragment::class.java.simpleName
                )
        }

        clRedSocial.setOnClickListener {
            eamxBackHandler.changeFragment(
                EAMXContentFragmentRedSocial.newInstance(callBack!!),
                R.id.contentFragment,
                EAMXContentFragmentRedSocial::class.java.simpleName
            )
        }

        clOraciones.setOnClickListener {
            eamxBackHandler.changeFragment(
                ProfileOrationFragment.newInstance(callBack!!),
                R.id.contentFragment,
                ProfileOrationFragment::class.java.simpleName
            )
        }

        clFormacion.setOnClickListener {
            eamxBackHandler.changeFragment(
                FormationFragment.newInstance(callBack!!),
                R.id.contentFragment,
                FormationFragment::class.java.simpleName
            )
        }

        clMiIglesia.setOnClickListener {
            eamxBackHandler.changeFragment(
                ProfileChurchFragment.newInstance(callBack!!),
                R.id.contentFragment,
                ProfileChurchFragment::class.java.simpleName
            )
        }

        clCadenas.setOnClickListener {
            eamxBackHandler.changeFragment(
                EAMXCadenaOracionesFragment.newInstance(callBack!!),
                R.id.contentFragment,
                EAMXCadenaOracionesFragment::class.java.simpleName
            )
        }

        clServicios.setOnClickListener {
            eamxBackHandler.changeFragment(
                ServicesMainFragment.newInstance(callBack!!),
                R.id.contentFragment,
                ServicesMainFragment::class.java.simpleName
            )
        }

        clComunidades.setOnClickListener {
            eamxBackHandler.changeFragment(
                EAMXCommunitiesPrincipalFragment.newInstance(callBack!!, signOut, false),
                R.id.contentFragment,
                EAMXCommunitiesPrincipalFragment::class.java.simpleName
            )
        }
        mBinding.btnApoyar.setOnClickListener {
            eamxBackHandler.changeFragment(
                ProfileDonacionesFragment.newInstance(),
                R.id.contentFragment,
                ProfileDonacionesFragment::class.java.simpleName
            )
//            EAMXPaymentFragment(callBack!!, R.id.framePrincipalLocal) {
//                //mBinding.clViewGenmeral.visibility = if (it) View.VISIBLE else View.GONE
//                //mBinding.framePrincipalLocal.visibility = if (it) View.GONE else View.VISIBLE
//            }.show(
//                childFragmentManager,
//                ""
//            )
        }
    }
}

fun EAMXHomeFragment.communityRegistro(fromMSG: Boolean) {
    eamxBackHandler.changeFragment(
        EAMXCommunitiesPrincipalFragment.newInstance(callBack!!, signOut!!, fromMSG!!),
        R.id.contentFragment,
        ProfileLiveFragment::class.java.simpleName
    )
}

fun EAMXHomeFragment.oracionesChange() {
    eamxBackHandler.changeFragment(
        ProfileOrationFragment.newInstance(callBack!!),
        R.id.contentFragment,
        ProfileLiveFragment::class.java.simpleName
    )
}

fun EAMXHomeFragment.IrFragment(f: Fragment,name:String) {
    eamxBackHandler.changeFragment(
        f, //Fragmente
        R.id.contentFragment,
        name //ProfileLiveFragment::class.java.simpleName
    )
}

fun EAMXHomeFragment.webView(link: String) {
    eamxBackHandler.changeFragment(
        WebViewHomeFragment(link),
        R.id.contentFragment,
        ProfileLiveFragment::class.java.simpleName
    )
}

fun EAMXHomeFragment.visibleModulesByProfile() {
    val profile = eamxcu_preferences.getData(
        EAMXEnumUser.USER_PROFILE.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String

    mBinding.apply {
        when (profile) {
            EAMXProfile.Devoted.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                // btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.DevotedAdmin.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //               btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.Priest.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.PriestAdmin.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.DeanPriest.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.CommunityResponsible.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.CommunityAdmin.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //btnApoyar.visibility = View.VISIBLE
            }
            EAMXProfile.CommunityMember.rol -> {
                cardLiveEvent.visibility = View.VISIBLE
                clRedSocial.visibility = View.VISIBLE
                clFormacion.visibility = View.VISIBLE //BIBLIOTECA EN LAS PBI
                clMiIglesia.visibility = View.VISIBLE
                clComunidades.visibility = View.VISIBLE
                clOraciones.visibility = View.VISIBLE
                clCadenas.visibility = View.VISIBLE
                clServicios.visibility = View.VISIBLE
                //btnApoyar.visibility = View.VISIBLE
            }
        }
    }
}

fun EAMXHomeFragment.setFullUserName() {
    val name = eamxcu_preferences
        .getData(EAMXEnumUser.USER_NAME.name, EAMXTypeObject.STRING_OBJECT)
        .toString()
    val lastName = eamxcu_preferences.getData(
        EAMXEnumUser.USER_LAST_NAME.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String

    callBack?.showToolbar(true, "$name $lastName ")
}



