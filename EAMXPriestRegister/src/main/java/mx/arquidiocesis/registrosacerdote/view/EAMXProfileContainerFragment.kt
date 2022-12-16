package mx.arquidiocesis.registrosacerdote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import mx.arquidiocesis.registrosacerdote.databinding.EamxProfileContentFragmentBinding

import mx.arquidiocesis.registrosacerdote.R

class EAMXProfileContainerFragment(val context: FragmentActivity) : Fragment(R.layout.eamx_profile_content_fragment) {
    companion object {
        fun newInstance(context: FragmentActivity) = EAMXProfileContainerFragment(context)
    }

    lateinit var adapter: MyViewPagerAdapter

    lateinit var mBinding: EamxProfileContentFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = EamxProfileContentFragmentBinding.bind(view)
        super.onViewCreated(mBinding.root, savedInstanceState)
        adapter =
            MyViewPagerAdapter(
                childFragmentManager
                , mBinding.viewPager
            )
        adapter.addFragment(EAMXProfileInfoFragment() {
            changeView()
        }, "Información")
        adapter.addFragment(EAMXPriestRegisterFragment(), "Donaciones")


        mBinding.viewPager.adapter = adapter
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)
    }

    fun changeView() {
        mBinding.tabLayout.removeAllTabs()
        var fragmentList: MutableList<Fragment> = mutableListOf()

        fragmentList.add(EAMXPriestRegisterFragment())
        fragmentList.add(EAMXProfileDonacionesFragment())
        adapter.updateReceiptsList(fragmentList)


    }

    class MyViewPagerAdapter(manager: FragmentManager, val viewPager: ViewPager) :
        FragmentPagerAdapter(manager) {

        private var fragmentList: MutableList<Fragment> = ArrayList()
        private val titleList: MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

        fun updateReceiptsList(fragmentListNueva: MutableList<Fragment>) {
            fragmentList = fragmentListNueva
            viewPager.post(Runnable { notifyDataSetChanged() })
        }
    }
}