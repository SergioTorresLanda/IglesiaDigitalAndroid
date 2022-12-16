package mx.arquidiocesis.eamxbiblioteca.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxbiblioteca.customviews.ViewPagerLibraryFragment
import mx.arquidiocesis.eamxbiblioteca.models.New


class PagerAdapter(
    fragmentActivity: FragmentActivity,
    val libraryFragments: ArrayList<ViewPagerLibraryFragment>,
    val news: List<New>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = libraryFragments.size

    override fun createFragment(position: Int): Fragment = ViewPagerLibraryFragment(news[position])
}