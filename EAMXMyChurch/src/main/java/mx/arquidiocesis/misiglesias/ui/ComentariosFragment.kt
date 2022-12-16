/*package mx.arquidiocesis.misiglesias.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.misiglesias.adapters.OpinionAdapter
import mx.arquidiocesis.misiglesias.database.instance.MeetRoomDataBase
import mx.arquidiocesis.misiglesias.databinding.FragmentComentariosBinding
import mx.arquidiocesis.misiglesias.model.OpinioModel
import mx.arquidiocesis.misiglesias.repository.Repository
import mx.arquidiocesis.misiglesias.utils.PublicFunctions
import mx.arquidiocesis.misiglesias.viewmodel.DetalleIglesiaViewModel


class ComentariosFragment : FragmentBase() {
    companion object {
        fun newInstance(callBack: EAMXHome): ComentariosFragment {
            val fragment = ComentariosFragment()
            fragment.callBack = callBack
            return fragment
        }
    }

    private val viewModel: DetalleIglesiaViewModel by lazy {
        getViewModel {
            DetalleIglesiaViewModel( Repository(
                requireContext(),
                MeetRoomDataBase.getRoomInstance(requireActivity())
            ))
        }
    }

    private lateinit var binding: FragmentComentariosBinding
    var idIglesia = 0
    var star = 0f
    var userId = 0
    var comentar=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComentariosBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBack.showToolbar(true, AppMyConstants.comentarios)
        idIglesia = requireArguments().getInt("idIglesia")
        star = requireArguments().getFloat("star")
        comentar=requireArguments().getBoolean("comentar")
        binding.tvNombreIglesia.text=requireArguments().getString("nombre")
        if (star != null) {
            binding.tvNumStar.text = "$star"
            binding.rbIglesia.rating = star
        }
        if(comentar){
            binding.cvComentario.visibility=View.VISIBLE
        }
        initObservers()
        userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
        viewModel.getComentarios(userId, idIglesia, 1)
        showLoader()
        binding.btnPublicar.setOnClickListener {
            if(!binding.etComentarios.text.isNullOrEmpty()){
                showLoader()
                viewModel.setComentario(
                    userId,
                    idIglesia,
                    binding.etComentarios.text.toString(),
                    binding.rbComentarios.rating.toInt()
                )
            }

        }
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }


    }

    fun initObservers() {
        viewModel.apply {
            opinionResponse.observe(viewLifecycleOwner) {item->
                hideLoader()
                binding.cvComentario.visibility=View.GONE
                if (!item.rating.isNullOrEmpty()) {
                    binding.tvNumStar.text = "${item.rating}"
                    binding.rbIglesia.rating = PublicFunctions().redondearStar(item.rating.toFloat())
                }
                viewModel.getComentarios(userId, idIglesia, 1)

            }
            opinionListResponse.observe(viewLifecycleOwner) {
                hideLoader()
                binding.rvComentarios.apply {
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = OpinionAdapter(
                        requireContext(),
                        it as MutableList<OpinioModel>,
                        binding.rvComentarios
                    ) {

                    }
                }
            }
            errorResponse.observe(viewLifecycleOwner) {
                hideLoader()
                UtilAlert
                    .Builder()
                    .setTitle("Aviso")
                    .setMessage(it)
                    .build()
                    .show(childFragmentManager, "")
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        callBack.restoreToolbar()
    }


}*/