package mx.arquidiocesis.eamxredsocialmodule.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxredsocialmodule.model.FollowModel
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXFollowListFragment

class ViewPagerRedAdapter(fragment: Fragment, val adapterFollow:FollowAdapter,val adapterFollower: FollowAdapter) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                EAMXFollowListFragment(adapterFollower)
            }
            1 -> {
                EAMXFollowListFragment(adapterFollow)
            }
            else -> EAMXFollowListFragment(adapterFollower)

        }
    }
}