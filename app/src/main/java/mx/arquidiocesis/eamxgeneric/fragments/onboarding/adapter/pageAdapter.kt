package mx.arquidiocesis.eamxgeneric.fragments.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mx.arquidiocesis.eamxgeneric.fragments.onboarding.EAMXFirstView
import mx.arquidiocesis.eamxgeneric.fragments.onboarding.EAMXSecondView

class ViewPageAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager){

    private val fragmentList = listOf(EAMXFirstView(), EAMXSecondView())

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

/*
    list:ArrayList<Fragment>,
    fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {


    private val fragmentList: ArrayList<Fragment> = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

*/
}