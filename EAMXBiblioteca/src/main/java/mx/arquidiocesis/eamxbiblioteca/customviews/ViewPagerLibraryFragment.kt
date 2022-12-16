package mx.arquidiocesis.eamxbiblioteca.customviews

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.databinding.FragmentViewPagerBinding
import mx.arquidiocesis.eamxbiblioteca.models.New

class ViewPagerLibraryFragment(val new: New) : Fragment() {

    lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_pager,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.apply {
            Glide.with(requireContext()).load(Uri.parse(new.image)).into(ivNew)
            tvTitleNew.text = new.title
            tvDescriptionNew.text = new.subtitle

            btnNew.setOnClickListener {

            }
        }
    }
}