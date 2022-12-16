package mx.arquidiocesis.eamxbiblioteca.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome

class BibliotecaFragment : Fragment() {
    companion object {
        fun newInstance(callBack: EAMXHome): BibliotecaFragment {
            val fragment = BibliotecaFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    lateinit var callBack: EAMXHome

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_biblioteca, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.formacion)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, LibraryMainFragment.newInstance())
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }
}