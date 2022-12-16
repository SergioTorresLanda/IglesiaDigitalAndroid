package mx.arquidiocesis.eamxcommonutils.util.ViewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.model.ViewPagerModel
import mx.arquidiocesis.eamxcommonutils.databinding.FragmentOnboardingBinding
import mx.arquidiocesis.eamxcommonutils.util.Adapter.RecyclerViewAdapter


class ViewPagerFragment(
    val item: ViewPagerModel,
    val listenerClose: (Any) -> Unit,
    val listener: (Any) -> Unit
) : Fragment() {
    lateinit var binding: FragmentOnboardingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)
        binding.apply {
            tvDescription.text = item.contenido
            if (item.titulo) {
                tvTitle.visibility = View.VISIBLE
            }
            when (item.tipo) {
                1 -> {
                    clTipo1.visibility = View.VISIBLE
                    tvOmmit.setOnClickListener {
                        listenerClose(true)
                    }
                    ivNext.setOnClickListener {
                        listener(true)
                    }
                }
                2 -> {
                    clTipo2.visibility = View.VISIBLE
                    tvContinuar.setOnClickListener {
                        listener(true)
                    }
                }
                else->{

                }
            }
            if(!item.list.isNullOrEmpty()){
                rvObciones.apply {
                    visibility=View.VISIBLE
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter=RecyclerViewAdapter(item.list){

                    }
                }
            }


        }

        return binding.root
    }




}