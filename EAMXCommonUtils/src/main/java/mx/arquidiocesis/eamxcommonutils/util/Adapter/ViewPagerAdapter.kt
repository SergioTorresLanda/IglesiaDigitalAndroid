package mx.arquidiocesis.eamxcommonutils.util.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxcommonutils.model.ViewPagerModel
import mx.arquidiocesis.eamxcommonutils.util.ViewPager.ViewPagerFragment

class ViewPagerAdapter (fragment: Fragment, val list: List<ViewPagerModel>, val listenerClose: (Any) -> Unit, val listener: (Any) -> Unit) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)

        return ViewPagerFragment(list[position],listenerClose,listener)

        /*  fragment.arguments = Bundle().apply {
              // Our object is just an integer :-P
              putInt(ARG_OBJECT, position + 1)
          }*/

    }
}