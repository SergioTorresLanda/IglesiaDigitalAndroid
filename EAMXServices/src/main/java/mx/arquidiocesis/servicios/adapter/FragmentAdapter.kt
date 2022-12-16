package mx.arquidiocesis.servicios.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel
import mx.arquidiocesis.servicios.ui.admin.FragmentServicesAdminHistory
import mx.arquidiocesis.servicios.ui.admin.ServiceAdminServicesFragment

class FragmentAdapter(fragment : Fragment, val callBack: EAMXHome, val listener : (AdminServiceModel)-> Unit) : FragmentStateAdapter(fragment){
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return if(position == 1){
            FragmentServicesAdminHistory.newInstance(callBack, listener)
        }else{
            ServiceAdminServicesFragment.newInstance(callBack, listener)
        }
    }
}