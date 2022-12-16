package mx.arquidiocesis.eamxbiblioteca.adapters


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import mx.arquidiocesis.eamxbiblioteca.models.LibraryModel
import mx.arquidiocesis.eamxbiblioteca.view.DescriptionFragment
import mx.arquidiocesis.eamxbiblioteca.view.ResourcesFragment


class CollectionAdapter(fragment: Fragment, private val libraryModel: LibraryModel) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return when (position) {
            0 -> {
                var descripcion=libraryModel.description
                if(descripcion==null){
                    descripcion=""
                }
                DescriptionFragment(descripcion)
                // tab.icon = drawable1
            }
            1 -> {
                ResourcesFragment(libraryModel.resources)
                // tab.icon = drawable1
            }
            else-> DescriptionFragment("")
        }
      /*  fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }*/

    }
}


