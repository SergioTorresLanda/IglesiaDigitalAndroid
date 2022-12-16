package mx.arquidiocesis.eamxlivestreammodule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome

class ProfileLiveFragment: Fragment() {

    companion object {
        fun newInstance(callBack: EAMXHome): ProfileLiveFragment {
            val fragment = ProfileLiveFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    lateinit var callBack: EAMXHome

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callBack.showToolbar(true, AppMyConstants.eventos_vivo)

        val fragment = EAMXLiveStreamFragment.newInstance()

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLive, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}