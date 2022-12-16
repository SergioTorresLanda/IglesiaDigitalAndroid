package mx.arquidiocesis.sos.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.sos.model.ServicesPriestModel
import mx.arquidiocesis.sos.ui.NotificationListFragment


class ViewPagerAdapter(fragment: Fragment,val listener: (ServicesPriestModel) -> Unit) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when (position) {
            0 -> {
                NotificationListFragment("ACTIVE",listener)
                // tab.icon = drawable1
            }
            1 -> {
                NotificationListFragment("FINISHED",listener)
                // tab.icon = drawable1
            }
            else -> NotificationListFragment("ACTIVE",listener)

        }
        /*  fragment.arguments = Bundle().apply {
              // Our object is just an integer :-P
              putInt(ARG_OBJECT, position + 1)
          }*/

    }
}


