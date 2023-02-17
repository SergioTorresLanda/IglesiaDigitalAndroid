package mx.arquidiocesis.eamxredsocialmodule

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXRedSocialFragment

class EAMXContentFragmentRedSocial : EAMXBaseFragment() {

    lateinit var callBack: EAMXHome
   // lateinit var callBackBottom: EAMXActionBottom

    override fun getLayout() = R.layout.eamx_content_fragment_dews

    override fun setViewModel() {}

    override fun initBinding(view: View) {}

    override fun initDependency(savedInstanceState: Bundle?) {}

    override fun initObservers() {}

    override fun initView(view: View) {
        callBack.showToolbar(true, AppMyConstants.red_social)
        val fragment = EAMXRedSocialFragment(true,0)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFragmentRedSocial, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
//        requireActivity().supportFragmentManager.commit {
//            add(R.id.contentFragmentRedSocial, EAMXRedSocialFragment.newInstance(callBack, callBackBottom))
//            addToBackStack(EAMXRedSocialFragment::class.java.simpleName)
//        }
    }

    companion object {
        fun newInstance(callBack: EAMXHome): EAMXContentFragmentRedSocial {
            val contentFragment = EAMXContentFragmentRedSocial()
            contentFragment.callBack =  callBack
           // contentFragment.callBackBottom = callBackBottom
            return contentFragment
        }
    }
}