package mx.arquidiocesis.eamxcommonutils.common

import android.view.View
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView

interface EAMXBackHandler {
    fun addFragment(fragmentEAMX: EAMXBaseFragment, containerId: Int)
    fun addFragment(fragmentEAMX: Fragment, containerId: Int)
    fun addFragment(fragmentEAMX: Fragment, containerId: Int, tag: String)
    fun addFragmentValidateBackStack(fragmentEAMX: Fragment, containerId: Int, TAG : String? = null, transition: Int? = null)
    fun changeFragment(fragmentEAMX: Fragment, containerId: Int, TAG : String?)
    fun changeFragment(fragmentEAMX: EAMXBaseFragment, containerId: Int, TAG : String?)
    fun changeFragmentValidateBackStack(fragmentEAMX: EAMXBaseFragment, containerId: Int, TAG : String?, transition: Int? = null)
    fun changeFragmentValidateBackStack(fragmentEAMX: Fragment, containerId: Int, TAG : String?, transition: Int? = null)
    fun backNavigationFragment(fragment: Fragment)
    fun showProgressBarCustom(message: String? = null, isCancelable: Boolean = false)
    fun hideProgressBarCustom()
    fun hideKeyBoard()
    fun showKeyBoard(viewEditable: View)
    fun showLottie(lottie: LottieAnimationView,
                   containerPersonality: View,
                   containerParent: View, show: Boolean)
    fun finisActivityAndEmptyBackStack()
    fun setFragment(EAMXBaseFragment: EAMXBaseFragment)
}