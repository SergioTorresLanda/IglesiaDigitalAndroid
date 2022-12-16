package mx.arquidiocesis.misiglesias.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.userAllowAccessAsAdmin
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.model.DataForView

class ProfileChurchFragment : FragmentBase() {

    companion object {
        fun newInstance(callBack: EAMXHome): ProfileChurchFragment {
            val fragment = ProfileChurchFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_church, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.miIglesia)

        val dataView = loadDataView();

        val fragment = MisIglesiasFragment.newInstance(requireActivity() as EAMXHome)
        val bundle = Bundle().apply {
            putParcelable(EAMXEnumUser.VIEW.name, dataView)
        }
        fragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameProfile, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }

    private fun loadDataView() : DataForView {

        val userIsAdmin = userAllowAccessAsAdmin(EAMXEnumUser.USER_PERMISSION_CHURCH.name)

        val userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT) as Int

        return if(userIsAdmin){
            DataForView(
                userId,
                EAMXProfile.Priest,
                mapOf(
                    0 to getString(R.string.label_church_principal_priest),
                    1 to getString(R.string.label_church_favorite_priest),
                    2 to getString(R.string.label_church_principal_priest),
                    3 to getString(R.string.label_church_favorite_priest)),
                mapOf(0 to View.GONE)
            )
        }else{
            DataForView(
                userId,
                EAMXProfile.Devoted,
                mapOf(0 to getString(R.string.label_church_principal_faithful),
                    1 to getString(R.string.label_church_favorite_faithful),
                    2 to getString(R.string.label_church_principal_faithful),
                    3 to getString(R.string.label_church_favorite_faithful)),
                mapOf(0 to View.VISIBLE)
            )
        }
    }
}