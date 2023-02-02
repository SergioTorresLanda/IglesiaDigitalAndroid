package mx.arquidiocesis.oraciones.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.arquidiocesis.oraciones.databinding.FragmentDetalleOracionBinding
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_detalle_oracion.*
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.oraciones.adapter.OracionDetalleAdapter
import mx.arquidiocesis.oraciones.repository.Repository
import mx.arquidiocesis.oraciones.viewmodel.OracionDetallesViewModel

class DetalleOracionFragment : FragmentBase() {

    companion object {
        fun newInstance(): DetalleOracionFragment {
            var detalleFragment = DetalleOracionFragment()
            return detalleFragment
        }
    }

    lateinit var binding: FragmentDetalleOracionBinding
    private val oracionDetalleViewModel: OracionDetallesViewModel by lazy {
        getViewModel {
            OracionDetallesViewModel(Repository(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetalleOracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener { activity?.onBackPressed() }
        val idOracion = requireArguments().getInt("idOracion")
        oracionDetalleViewModel.obtenerDetalle(idOracion)
        showLoader()
        initObservers()
    }

    fun initObservers() {
        oracionDetalleViewModel.response.observe(viewLifecycleOwner) { oracionModel ->
            binding.tvDetalleOracionText.text = oracionModel.description
            callBack = (activity as EAMXHome)
            callBack.showToolbar(true, oracionModel.name)
            binding.tvTitulos.text = oracionModel.name
            Glide.with(requireContext()).load(Uri.parse(oracionModel.image_url))
                .apply(RequestOptions()
                    //.override(700, 373)
                    //.centerCrop()
                )
                .into(binding.ivDetalleOracion)
            /* val oracionAdapter = OracionDetalleAdapter(oracionModel.similars, requireContext()) {
                limpiarPantalla()
                showLoader()
                oracionDetalleViewModel.obtenerDetalle(it.id)
            }
             binding.rvDetalleOracion.apply {
                layoutManager = LirLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = oracionAdapter
                binding.rvDetalleOracion.visibility = View.VISIBLE
            }*/
            hideLoader()
        }
        oracionDetalleViewModel.errorResponse.observe(viewLifecycleOwner) {
            println("ErrorResponse")
            hideLoader()
        }
    }

    fun limpiarPantalla() {
        binding.tvDetalleOracionText.text = ""
        binding.ivDetalleOracion.setImageDrawable(null)
        binding.rvDetalleOracion.visibility = View.INVISIBLE
    }
}