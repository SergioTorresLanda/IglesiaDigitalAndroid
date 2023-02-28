package mx.arquidiocesis.eamxredsocialmodule

import android.os.Bundle
import android.view.View
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.*
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXRedSocialFragment

class EAMXContentFragmentRedSocial : EAMXBaseFragment() {

    lateinit var callBack: EAMXHome

    override fun getLayout() = R.layout.eamx_content_fragment_dews

    override fun setViewModel() {}

    override fun initBinding(view: View) {}

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {
        callBack.showToolbar(true, AppMyConstants.red_social)
        val idUser = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID_REDSOCIAL.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        val fragment = EAMXRedSocialFragment(true, idUser)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFragmentRedSocial, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    companion object {
        fun newInstance(callBack: EAMXHome): EAMXContentFragmentRedSocial {
            val contentFragment = EAMXContentFragmentRedSocial()
            contentFragment.callBack = callBack
            return contentFragment
        }
    }
}