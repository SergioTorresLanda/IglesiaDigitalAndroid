package mx.arquidiocesis.eamxredsocialmodule.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxredsocialmodule.EAMXContentFragmentRedSocial
import mx.arquidiocesis.eamxredsocialmodule.model.FollowModel
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXFollowListFragment
import mx.arquidiocesis.eamxredsocialmodule.ui.EAMXRedSocialFragment

class ViewPagerRedAdapter(fragment: Fragment,val postsAdapter:PostAdapter,val adapterFollower: FollowAdapter , val adapterFollow:FollowAdapter, val id_user:Int) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                EAMXRedSocialFragment(false,id_user)
            }
            1 -> {
                EAMXFollowListFragment(adapterFollower)
            }
            2 -> {
                EAMXFollowListFragment(adapterFollow)
            }
            else -> EAMXFollowListFragment(adapterFollower)

        }
    }
}