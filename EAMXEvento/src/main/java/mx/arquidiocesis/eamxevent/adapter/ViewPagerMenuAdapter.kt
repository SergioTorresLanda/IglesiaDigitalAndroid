package mx.arquidiocesis.eamxevent.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxevent.ui.DespensasFragment
import mx.arquidiocesis.eamxevent.ui.EventFragment
import mx.arquidiocesis.eamxredsocialmodule.adapter.FollowAdapter
import mx.arquidiocesis.eamxredsocialmodule.adapter.PostAdapter
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXFollowListFragment
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXRedSocialFragment

class ViewPagerMenuAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int  = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                EventFragment()
            }
            1 -> {
                DespensasFragment()
            }
            2 -> {
                DespensasFragment()
            }
            else -> EventFragment()

        }
    }

}
