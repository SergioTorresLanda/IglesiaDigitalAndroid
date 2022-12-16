package mx.arquidiocesis.eamxprofilemodule.info.utils

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxprofilemodule.ui.profile.model.EAMXViewPagerConstructor

class EAMXProfilePagerAdapter<T>(viewPagerConstructor: EAMXViewPagerConstructor) :
    FragmentStateAdapter(viewPagerConstructor.fragmentManager, viewPagerConstructor.lifecycle) {

    private var fragmentList: ArrayList<T> = ArrayList()
    private var fragmentTitleList: ArrayList<String> = ArrayList()

    fun setFragmentList(fragments: ArrayList<T>) {
        fragmentList = fragments
    }

    fun setTitleList(titleList: ArrayList<String>){
        fragmentTitleList = titleList
    }

    fun getTitleForPosition(position: Int) = fragmentTitleList[position]

    fun getItemForPosition(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position] as Fragment

}