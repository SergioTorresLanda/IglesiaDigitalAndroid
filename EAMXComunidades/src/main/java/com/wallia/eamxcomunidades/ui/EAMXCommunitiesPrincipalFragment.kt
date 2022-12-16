package com.wallia.eamxcomunidades.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.config.Constants
import com.wallia.eamxcomunidades.database.instance.MeetRoomDataBaseCommunity
import com.wallia.eamxcomunidades.repository.Repository
import com.wallia.eamxcomunidades.utils.PublicFunctions
import com.wallia.eamxcomunidades.viewmodel.ComunidadesViewModel
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment

class EAMXCommunitiesPrincipalFragment : FragmentBase() {

    lateinit var signOut: EAMXSignOut
    var fromMSG: Boolean? = null
    private val viewModel: ComunidadesViewModel by lazy {
        getViewModel {
            ComunidadesViewModel(
                Repository(
                    context = requireContext(),
                    database = MeetRoomDataBaseCommunity.getRoomInstance(requireContext())
                )
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            callBack: EAMXHome,
            signOut: EAMXSignOut,
            fromMSG: Boolean,
        ): EAMXCommunitiesPrincipalFragment {
            val contentFragmentPrincipal = EAMXCommunitiesPrincipalFragment()
            contentFragmentPrincipal.callBack = callBack
            contentFragmentPrincipal.signOut = signOut
            contentFragmentPrincipal.fromMSG = fromMSG
            return contentFragmentPrincipal
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(
            R.layout.eamx_contenedor_principal_communities_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callBack.showToolbar(true, AppMyConstants.comunidad)

        initObservers()

        //val publicFunctions = PublicFunctions()
        //viewModel.getUserDetail(publicFunctions.getUserId())
        //showLoader()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }

    private fun initObservers() {
        loadFragmentByProfileUser(eamxcu_preferences.getData(EAMXEnumUser.USER_COMMUNITY_STATUS.name,
            EAMXTypeObject.STRING_OBJECT) as String)
    }

    private fun loadFragmentByProfileUser(status: String) {
        val isResponsable = eamxcu_preferences.getData(
            EAMXEnumUser.USER_PROFILE.name,
            EAMXTypeObject.STRING_OBJECT
        ) as String

        val communityId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_COMMUNITY_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int

        //fromMSG = true

        val fragment =
            if (isResponsable == EAMXProfile.CommunityResponsible.rol && communityId == 0 && status != Constants.PENDING_VICARAGE_APPROVAL) {
                "FLOW EAMXComunidadesSacerdoteFragment".log()
                EAMXComunidadesSacerdoteFragment.newInstance(callBack, signOut)
            } else {
                if (!fromMSG!!) {
                    "FLOW EAMXComunidadesFielFragment".log()
                    EAMXComunidadesFielFragment.newInstance(callBack, signOut)//AQUI ENTRA MY APP
                } else {
                    "FLOW EAMXRegisterCommunityFragment".log()
                    EAMXRegisterCommunityFragment.newInstance(
                        Constants.PENDING_COMPLETION)////AQUI ENTRA MY GENERIC
                }
            }

        /*val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameCommunities, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()*/

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameCommunities, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}