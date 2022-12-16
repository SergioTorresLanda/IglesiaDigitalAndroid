package mx.arquidiocesis.eamxdonaciones.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxdonaciones.ui.BillingFragment
import mx.arquidiocesis.eamxdonaciones.ui.MyDonationFragment


class OfrendaViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when (position) {
            0 -> {
                MyDonationFragment()
            }
            1 -> {
                BillingFragment()
                // tab.icon = drawable1
            }
            else -> MyDonationFragment()


        }
        /*  fragment.arguments = Bundle().apply {
              // Our object is just an integer :-P
              putInt(ARG_OBJECT, position + 1)
          }*/

    }
}
