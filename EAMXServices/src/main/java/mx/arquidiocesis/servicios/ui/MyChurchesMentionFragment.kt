package mx.arquidiocesis.servicios.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXHome
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.adapter.ChurchAdapter
import mx.arquidiocesis.servicios.customviews.DialogFindChurchFragment
import mx.arquidiocesis.servicios.databinding.FragmentMyChurchesMentionBinding
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel

class MyChurchesMentionFragment : FragmentBase() {

    lateinit var binding: FragmentMyChurchesMentionBinding
    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }
    private var esSacerdote = ""
private var instalacionNombre=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.solicitarIntencion)
        binding = FragmentMyChurchesMentionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        esSacerdote =
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_PROFILE.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
        var userId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int

        initObservers()
        showLoader()

        (requireActivity() as EAMXHome).showToolbar(true, AppMyConstants.solicitarIntencion)

       // if (userId == 0)
         //   userId = 313

        binding.apply {
            etBusarIglesia.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showDialogFindChurch()
                }
            }

            etBusarIglesia.setOnClickListener {
                showDialogFindChurch()
            }

            ibSearch.setOnClickListener {
                showDialogFindChurch()
            }
        }

        viewModel.getMyChurches(userId, esSacerdote)
    }

    fun initObservers() {
        viewModel.iglesiasResponse.observe(viewLifecycleOwner) { misIgleciasModel ->
            if (misIgleciasModel.assigned != null) {
                binding.tvIglesia.text = misIgleciasModel.assigned.name

                Glide.with(requireContext())
                    .load(Uri.parse(misIgleciasModel.assigned?.image_url))
                    .into(binding.ivIglesia)

                binding.cvAsignado.setOnClickListener {
                    instalacionNombre=""
                    instalacionNombre =binding.tvIglesia.text.toString()
                    scheduleMention(misIgleciasModel.assigned.id)
                }
            }else{
                binding.ivIglesia.visibility = View.GONE
                binding.tvIglesia.text = ""
            }

            binding.rvListaIglesias.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = ChurchAdapter(requireContext(), misIgleciasModel.locations) {
                    instalacionNombre=""
                    instalacionNombre =it.name
                    scheduleMention(it.id)
                }

            }
            hideLoader()
        }

        viewModel.errorResponse.observe(viewLifecycleOwner){
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build().show(childFragmentManager, "")
        }
    }

    private fun showDialogFindChurch() {
        activity?.supportFragmentManager?.let {
            val dialog = DialogFindChurchFragment.newInstance()
            dialog.churchSelected = {
                scheduleMention(it.id)
            }
            dialog.show(it, "Show Dialog Congregation")
        }
    }

    fun scheduleMention(idChuch: Int) {
        val bundle = Bundle()
        bundle.putString(
            "ESTABLECIMIENTO_NAME",instalacionNombre
        )
        NavigationFragment.Builder()
            .setActivity(requireActivity())
            .setView(requireView().parent as ViewGroup)
            .setAllowStack(false)
            .setBundle(bundle)
            .setFragment(IntentionScheduleDayFragment.newInstance(idChuch))
            .build().nextWithReplace()
    }

}