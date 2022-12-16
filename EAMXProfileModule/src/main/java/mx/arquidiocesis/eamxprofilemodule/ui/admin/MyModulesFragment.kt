package mx.arquidiocesis.eamxprofilemodule.ui.admin

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_admin_detail.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnums
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.FragmentMyModulesBinding
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemModuleBinding
import mx.arquidiocesis.eamxprofilemodule.model.*
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import mx.arquidiocesis.eamxprofilemodule.viewmodel.CollaboratorViewModel
import mx.arquidiocesis.eamxprofilemodule.viewmodel.ModuleViewModel

const val LOCATION_INFORMATION = "LOCATION_INFORMATION"
const val SERVICES = "SERVICES"
const val SOCIAL_NETWORKS = "SOCIAL_NETWORKS"
const val DONATIONS = "DONATIONS"
const val SOS = "SOS"
const val APPOINT_ADMINISTRATOR = "APPOINT_ADMINISTRATOR"

lateinit var binding: FragmentMyModulesBinding
val modulesSelected = ArrayList<RequestModuleUpdate>()
var responseModules: ArrayList<ModuleModel> = ArrayList()

lateinit var responseModule: ArrayList<Module>

class MyModulesFragment : FragmentBase() {

    var collaborator: CollaboratorModel? = null
    var collaboratorDetail: CollaboratorDetailModel? = null
    private var churchCurrentUser: Int = 0

    companion object {
        fun newInstance(): MyModulesFragment =
            MyModulesFragment()

        fun newInstance(collaborator: CollaboratorModel): MyModulesFragment {
            val fragment = MyModulesFragment()
            fragment.collaborator = collaborator
            return fragment
        }

        fun newInstanceWithCollaborator(
            collaborator: CollaboratorModel,
            collaboratorDetail: CollaboratorDetailModel,
        ): MyModulesFragment {
            val fragment = MyModulesFragment()
            fragment.collaborator = collaborator
            fragment.collaboratorDetail = collaboratorDetail
            return fragment
        }
    }

    private val viewModel: ModuleViewModel by lazy {
        getViewModel {
            ModuleViewModel(
                RepositoryProfile(requireContext())
            )
        }
    }

    private val viewModelCollaborator: CollaboratorViewModel by lazy {
        getViewModel {
            CollaboratorViewModel(
                RepositoryProfile(requireContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyModulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val church =
            (eamxcu_preferences.getData(
                EAMXEnumUser.CHURCH.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String)

        if (church.isEmpty() || church == "null") {
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(getString(R.string.church_communiti_empty))
                .setListener {
                    activity?.onBackPressed()
                }
                .build().show(childFragmentManager, "")
        } else {
            churchCurrentUser = church.toInt()

            binding.includeToolbar.toolbarTitle.text = "Administrar módulos"
            binding.includeToolbar.ivBack.setOnClickListener {
                activity?.onBackPressed()
            }

            initObservers()

            if (collaborator != null) {

                binding.cardView.visibility = View.VISIBLE

                collaborator!!.id?.let {
                    showLoader()
                    val church = (eamxcu_preferences.getData(
                        EAMXEnumUser.CHURCH.name,
                        EAMXTypeObject.STRING_OBJECT
                    ) as String).toInt()
                    viewModelCollaborator.getCollaboratorDetail(church, it)
                }

                binding.tvName.visibility = View.VISIBLE
                if (collaborator!!.isSuperAdmin == true) {
                    binding.tvAdmin.visibility = View.VISIBLE
                    binding.ivStar.visibility = View.VISIBLE
                }

                if (collaborator!!.isAdmin == true) {
                    binding.tvAdmin.visibility = View.VISIBLE
                }

                if (collaborator!!.isSuperAdmin == false && collaborator!!.isAdmin == false) {
                    binding.tvDoAdmin.visibility = View.VISIBLE
                }

                binding.tvName.text = "${collaborator!!.name} ${collaborator!!.firstSurname} ${collaborator!!.secondSurname}"

                //loadModules()
            } else {
                binding.tvModulesAdmin.visibility = View.VISIBLE
                binding.tvNameAdmin.visibility = View.INVISIBLE

                viewModel.getModules(churchCurrentUser)
                showLoader()
            }

            binding.tvDoAdmin.setOnClickListener {
                for (i in 0 until binding.llNameAdmin.childCount) {
                    selectOptionsModule(((binding.llNameAdmin.getChildAt(i) as ViewGroup).get(0) as TextView))
                }
            }

            binding.btnCancel.setOnClickListener {
                activity?.onBackPressed()
            }

            binding.btnSave.setOnClickListener {
                if (modulesSelected.isEmpty()) {
                    launchRequest()
                } else {
                    val modulesSelectedStr: ArrayList<String> = ArrayList()
                    responseModule.forEach { moduleIndex ->
                        modulesSelected.forEach { moduleSelectedIndex ->
                            if (moduleIndex.id == moduleSelectedIndex.moduleId) {
                                moduleSelectedIndex.let {
                                    moduleIndex.name?.let { name ->
                                        if (!modulesSelectedStr.contains(name)) {
                                            modulesSelectedStr.add(name)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    UtilAlert.Builder()
                        .setTitle(getString(R.string.title_dialog_warning))
                        .setMessage(
                            "Los siguientes módulos se activaran: ${
                                modulesSelectedStr.joinToString("\n")
                            }"
                        )
                        .setTextButtonOk(getString(R.string.btn_save))
                        .setTextButtonCancel(getString(R.string.btn_cancel))
                        .setListener { button ->
                            when (button) {
                                ACTION_ACCEPT -> {
                                    launchRequest()
                                }
                            }
                        }.build().show(childFragmentManager, "")
                }
            }

            binding.tvDisableModules.setOnClickListener {
                for (i in 0 until binding.llChurches.childCount) {
                    unselectAllOptions(((binding.llChurches.getChildAt(i) as ViewGroup).get(0) as TextView))
                }

                for (i in 0 until binding.llServices.childCount) {
                    unselectAllOptions(((binding.llServices.getChildAt(i) as ViewGroup).get(0) as TextView))
                }

                for (i in 0 until binding.llSocialNetwork.childCount) {
                    unselectAllOptions(((binding.llSocialNetwork.getChildAt(i) as ViewGroup).get(0) as TextView))
                }

                for (i in 0 until binding.llDonations.childCount) {
                    unselectAllOptions(((binding.llDonations.getChildAt(i) as ViewGroup).get(0) as TextView))
                }

                for (i in 0 until binding.llSOS.childCount) {
                    unselectAllOptions(((binding.llSOS.getChildAt(i) as ViewGroup).get(0) as TextView))
                }

                for (i in 0 until binding.llNameAdmin.childCount) {
                    unselectAllOptions(((binding.llNameAdmin.getChildAt(i) as ViewGroup).get(0) as TextView))
                }

                modulesSelected.clear()
            }

            //Se valida si el rol es un tipo de sacerdote para ver que se le permite visualizar
            when (eamxcu_preferences.getData(
                EAMXEnumUser.USER_PROFILE.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String) {
                EAMXProfile.DeanPriest.rol -> binding.clSOS.visibility = View.VISIBLE
                else -> binding.clSOS.visibility = View.GONE
            }
        }

        binding.apply {
            tvSelectAllChurches.setOnClickListener {
                changeTextEnabledOrDisabled(tvSelectAllChurches)
                activateOrInactivatePermissions(binding.llChurches, isActivateOrInactivatePermissions(tvSelectAllChurches))
            }
            tvSelectAllServices.setOnClickListener {
                changeTextEnabledOrDisabled(tvSelectAllServices)
                activateOrInactivatePermissions(binding.llServices, isActivateOrInactivatePermissions(tvSelectAllServices))
            }
            tvSelectAllSocialNetwork.setOnClickListener {
                changeTextEnabledOrDisabled(tvSelectAllSocialNetwork)
                activateOrInactivatePermissions(binding.llSocialNetwork, isActivateOrInactivatePermissions(tvSelectAllSocialNetwork))
            }
            tvSelectAllDonations.setOnClickListener {
                changeTextEnabledOrDisabled(tvSelectAllDonations)
                activateOrInactivatePermissions(binding.llDonations, isActivateOrInactivatePermissions(tvSelectAllDonations))
            }
            tvSelectAllSOS.setOnClickListener {
                changeTextEnabledOrDisabled(tvSelectAllSOS)
                activateOrInactivatePermissions(binding.llSOS, isActivateOrInactivatePermissions(tvSelectAllSOS))
            }
            tvSelectAllNameAdmin.setOnClickListener {
                changeTextEnabledOrDisabled(tvSelectAllNameAdmin)
                activateOrInactivatePermissions(binding.llNameAdmin, isActivateOrInactivatePermissions(tvSelectAllNameAdmin))
            }
        }

        binding.ivNameAdmin.setOnClickListener { showHelpMsg("Las personas asignadas deberán ser de tu entera confianza porque podrán ayudarte a nombrar a otros administradores y visualizar información y servicios de la iglesia o comunidad.") }
        binding.ivEditChurch.setOnClickListener { showHelpMsg("La persona asignada podrá editar la información de la iglesia para mantenerla actualizada.") }
        binding.ivServices.setOnClickListener { showHelpMsg("Los administradores podrán recibir solicitudes de servicios o intenciones de la iglesia y ver los servicios pendientes para asegurarse de atenderlos.") }
        binding.ivSocialNetwork.setOnClickListener { showHelpMsg("Activar esta función hará que los colaboradores puedan publicar, editar, compartir y comentar publicaciones a nombre de la iglesia o comunidad.") }
        binding.ivDonations.setOnClickListener { showHelpMsg("El administrador de este módulo podrá visualizar las donaciones recibidas en el historial de donaciones y recibir las notificaciones correspondientes.") }
        binding.ivSOS.setOnClickListener { showHelpMsg("Si activas esta función, la persona se convertirá en un contacto de apoyo del decanato que recibirá solicitudes de servicios y deberá contactar a los fieles y a los sacerdotes o diáconos hasta asegurar la atención de los servicios.") }
    }

    private fun changeTextEnabledOrDisabled(textView : TextView){
        when(textView.text){
            getString(R.string.enabled_all) -> textView.text = getString(R.string.disabled_all)
            getString(R.string.disabled_all) -> textView.text = getString(R.string.enabled_all)
        }
    }

    private fun isActivateOrInactivatePermissions(textView : TextView) : Boolean{
        return !(textView.text == getString(R.string.enabled_all))
    }

    private fun activateOrInactivatePermissions(linearLayout : LinearLayout, check : Boolean){
        for (i in 0 until linearLayout.childCount) {
            if(check) {
                selectOptionsModule(((linearLayout.getChildAt(i) as ViewGroup).get(0) as TextView))
            }else{
                unselectAllOptions(((linearLayout.getChildAt(i) as ViewGroup).get(0) as TextView))
            }
        }
    }

    private fun launchRequest() {
        showLoader()
        val modulesSend = modulesSelected.distinctBy {
            it.moduleId
        }

        if (collaborator != null) {
            viewModelCollaborator.updateModulesOfCollaborator(
                churchCurrentUser,
                collaborator?.id!!,
                modulesSend
            )
        } else {
            viewModel.updateModules(churchCurrentUser, modulesSend)
        }
        UtilAlert.Builder()
            .setTitle(getString(R.string.title_dialog_warning))
            .setMessage(
                "Los cambios se efectuaron\n" +
                        "con éxito."
            )
            .setListener {
                activity?.onBackPressed()
            }
            .setIsCancel(false)
            .build().show(childFragmentManager, "")
        hideLoader()
    }

    fun initObservers() {
        viewModel.getModulesResponse.observe(viewLifecycleOwner) { response ->
            hideLoader()


            responseModules = response as ArrayList<ModuleModel>
            responseModule = ArrayList()
            responseModules.forEach {
                modulesSelected.add(RequestModuleUpdate(it.id))
                responseModule.add(
                    Module(
                        it.category,
                        it.enable,
                        it.id,
                        it.name,
                    )
                )
            }

            response.forEach {
                when (it.category) {
                    LOCATION_INFORMATION -> {
                        val bindingImageHome =
                            ItemModuleBinding.inflate(LayoutInflater.from(context))

                        it.id?.let {
                            bindingImageHome.tvModule.id = it
                        }

                        bindingImageHome.tvModule.text = it.name

                        var isSelected = false

                        it.enable?.let {
                            if (it) {
                                isSelected = true
                            }
                        }

                        fillModulesSelected(bindingImageHome.tvModule, isSelected)

                        binding.llChurches.addView(bindingImageHome.root)
                    }
                    SERVICES -> {
                        val bindingImageHome =
                            ItemModuleBinding.inflate(LayoutInflater.from(context))
                        it.id?.let {
                            bindingImageHome.tvModule.id = it
                        }

                        bindingImageHome.tvModule.text = it.name
                        var isSelected = false

                        it.enable?.let {
                            if (it) {
                                isSelected = true
                            }
                        }

                        fillModulesSelected(bindingImageHome.tvModule, isSelected)

                        binding.llServices.addView(bindingImageHome.root)
                    }
                    SOCIAL_NETWORKS -> {
                        val bindingImageHome =
                            ItemModuleBinding.inflate(LayoutInflater.from(context))

                        it.id?.let {
                            bindingImageHome.tvModule.id = it
                        }

                        bindingImageHome.tvModule.text = it.name

                        var isSelected = false

                        it.enable?.let {
                            if (it) {
                                /*/*bindingImageHome.tvModule.setBackgroundColor(Color.parseColor("#1c75bc"))
                                bindingImageHome.tvModule.setTextColor(Color.WHITE)*/*/
                                isSelected = true
                            }
                        }

                        fillModulesSelected(bindingImageHome.tvModule, isSelected)

                        binding.llSocialNetwork.addView(bindingImageHome.root)
                    }
                    DONATIONS -> {
                        val bindingImageHome =
                            ItemModuleBinding.inflate(LayoutInflater.from(context))

                        it.id?.let {
                            bindingImageHome.tvModule.id = it
                        }

                        bindingImageHome.tvModule.text = it.name

                        var isSelected = false

                        it.enable?.let {
                            if (it) {
                                isSelected = true
                            }
                        }

                        fillModulesSelected(bindingImageHome.tvModule, isSelected)

                        binding.llDonations.addView(bindingImageHome.root)
                    }
                    SOS -> {
                        val bindingImageHome =
                            ItemModuleBinding.inflate(LayoutInflater.from(context))

                        it.id?.let {
                            bindingImageHome.tvModule.id = it
                        }

                        bindingImageHome.tvModule.text = it.name

                        var isSelected = false

                        it.enable?.let {
                            if (it) {
                                isSelected = true
                            }
                        }

                        fillModulesSelected(bindingImageHome.tvModule, isSelected)

                        binding.llSOS.addView(bindingImageHome.root)
                    }
                    APPOINT_ADMINISTRATOR -> {
                        val bindingImageHome =
                            ItemModuleBinding.inflate(LayoutInflater.from(context))

                        it.id?.let {
                            bindingImageHome.tvModule.id = it
                        }

                        bindingImageHome.tvModule.text = it.name
                        var isSelected = false

                        it.enable?.let {
                            if (it) {
                                isSelected = true
                            }
                        }

                        fillModulesSelected(bindingImageHome.tvModule, isSelected)

                        /*bindingImageHome.tvModule.setOnClickListener { item ->
                            val textView = (item as TextView)
                            fillModulesSelected(
                                textView,
                                textView.currentTextColor == Color.GRAY
                            )

                            if (textView.currentTextColor == Color.WHITE) {
                                //Se seleccionan todas las opciones en automático
                                for (i in 0 until binding.llNameAdmin.childCount) {
                                    val itemCheck =
                                        ((binding.llNameAdmin.getChildAt(i) as ViewGroup)[0] as TextView)
                                    fillModulesSelected(
                                        itemCheck,
                                        itemCheck.currentTextColor == Color.WHITE
                                    )
                                }
                            }
                        }*/
                        binding.llNameAdmin.addView(bindingImageHome.root)
                    }
                }
            }
        }

        viewModel.updateModulesResponse.observe(viewLifecycleOwner) { response ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(
                    "Los cambios se efectuaron\n" +
                            "con éxito."
                )
                .setListener {
                    if (collaborator != null) {
                        collaborator = null
                        collaboratorDetail = null
                        modulesSelected.clear()
                        responseModules.clear()
                        responseModule.clear()
                        parentFragmentManager.popBackStack(
                            EAMXEnums.EMAIL.name,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    } else {
                        collaborator = null
                        collaboratorDetail = null
                        modulesSelected.clear()
                        responseModules.clear()
                        responseModule.clear()
                        parentFragmentManager.popBackStack()
                    }
                }
                .setIsCancel(false)
                .build().show(childFragmentManager, "")
        }

        viewModelCollaborator.updateModulesOfCollaboratorResponse.observe(viewLifecycleOwner) { response ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(
                    "Los cambios se efectuaron\n" +
                            "con éxito."
                )
                .setListener {
                    if (collaborator != null) {
                        collaborator = null
                        collaboratorDetail = null
                        modulesSelected.clear()
                        responseModules.clear()
                        responseModule.clear()
                        parentFragmentManager.popBackStack(
                            EAMXEnums.EMAIL.name,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    } else {
                        collaborator = null
                        collaboratorDetail = null
                        modulesSelected.clear()
                        responseModules.clear()
                        responseModule.clear()
                        parentFragmentManager.popBackStack()
                    }
                }
                .setIsCancel(false)
                .build().show(childFragmentManager, "")
        }

        viewModelCollaborator.errorResponse.observe(viewLifecycleOwner) { message ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(message)
                .build().show(childFragmentManager, "")
        }

        viewModelCollaborator.collaboratorDetailResponse.observe(viewLifecycleOwner) { response ->
            collaboratorDetail = response

            binding.apply {
                tetStyleLife.setText(response.lifeStatus)
                tetChurch.setText(response.location?.name)
                tetEmail.setText(response.email)
                tetJob.setText(response.serviceModels?.get(0)?.name)
            }

            loadModules(response)
            hideLoader()
        }

        viewModelCollaborator.errorResponseExit.observe(viewLifecycleOwner) { response ->
            hideLoader()
            UtilAlert.Builder()
                .setTitle(getString(R.string.title_dialog_warning))
                .setMessage(response)
                .setListener {
                    activity?.onBackPressed()
                }
                .build().show(childFragmentManager, "")

            hideLoader()
        }
    }

    private fun fillModulesSelected(textView: TextView, status: Boolean) {
        if (collaborator == null) {
            responseModules.forEach {
                if (it.id == textView.id) {
                    if (status) {
                        textView.apply {
                            setTextColor(Color.WHITE)
                            setBackgroundColor(Color.parseColor("#1c75bc"))
                        }

                        if (!modulesSelected.contains(RequestModuleUpdate(it.id))) {
                            modulesSelected.add(RequestModuleUpdate(it.id))
                        }
                        return@forEach
                    } else {

                        textView.apply {
                            setTextColor(Color.GRAY)
                            setBackgroundColor(Color.WHITE)
                        }

                        modulesSelected.remove(RequestModuleUpdate(it.id))
                        return@forEach
                    }
                }
            }
        } else {
            responseModule.forEach {
                if (it.id == textView.id) {
                    if (status) {
                        textView.apply {
                            setTextColor(Color.WHITE)
                            setBackgroundColor(Color.parseColor("#1c75bc"))
                        }

                        if (!modulesSelected.contains(RequestModuleUpdate(it.id))) {
                            modulesSelected.add(RequestModuleUpdate(it.id))
                        }
                        return@forEach
                    } else {

                        textView.apply {
                            setTextColor(Color.GRAY)
                            setBackgroundColor(Color.WHITE)
                        }

                        modulesSelected.remove(RequestModuleUpdate(it.id))
                        return@forEach
                    }
                }
            }
        }
    }

    fun loadModules(response : CollaboratorDetailModel) {
        responseModule = response.modules  as ArrayList<Module>

        responseModule.forEach {
            modulesSelected.add(RequestModuleUpdate(it.id))
        }

        responseModule.forEach {
            when (it.category) {
                LOCATION_INFORMATION -> {
                    val bindingImageHome =
                        ItemModuleBinding.inflate(LayoutInflater.from(context))

                    it.id?.let {
                        bindingImageHome.tvModule.id = it
                    }

                    bindingImageHome.tvModule.text = it.name

                    var isSelected = false

                    it.enabled?.let {
                        if (it) {
                            isSelected = true
                        }
                    }

                    fillModulesSelected(bindingImageHome.tvModule, isSelected)

                    /*bindingImageHome.tvModule.setOnClickListener {
                        val textView = (it as TextView)
                        fillModulesSelected(textView, textView.currentTextColor == Color.GRAY)
                    }*/

                    if(isSelected && binding.tvSelectAllChurches.text.toString() == getString(R.string.enabled_all)){
                        changeTextEnabledOrDisabled(binding.tvSelectAllChurches)
                    }

                    binding.llChurches.addView(bindingImageHome.root)
                }
                SERVICES -> {
                    val bindingImageHome =
                        ItemModuleBinding.inflate(LayoutInflater.from(context))
                    it.id?.let {
                        bindingImageHome.tvModule.id = it
                    }

                    bindingImageHome.tvModule.text = it.name
                    var isSelected = false

                    it.enabled?.let {
                        if (it) {
                            isSelected = true
                        }
                    }

                    fillModulesSelected(bindingImageHome.tvModule, isSelected)

                    /*bindingImageHome.tvModule.setOnClickListener {
                        val textView = (it as TextView)
                        fillModulesSelected(textView, textView.currentTextColor == Color.GRAY)
                    }*/

                    if(isSelected && binding.tvSelectAllServices.text.toString() == getString(R.string.enabled_all)){
                        changeTextEnabledOrDisabled(binding.tvSelectAllServices)
                    }

                    binding.llServices.addView(bindingImageHome.root)
                }
                SOCIAL_NETWORKS -> {
                    val bindingImageHome =
                        ItemModuleBinding.inflate(LayoutInflater.from(context))

                    it.id?.let {
                        bindingImageHome.tvModule.id = it
                    }

                    bindingImageHome.tvModule.text = it.name
                    var isSelected = false

                    it.enabled?.let {
                        if (it) {
                            isSelected = true
                        }
                    }

                    fillModulesSelected(bindingImageHome.tvModule, isSelected)

                    /*bindingImageHome.tvModule.setOnClickListener {
                        val textView = (it as TextView)
                        fillModulesSelected(textView, textView.currentTextColor == Color.GRAY)
                    }*/

                    if(isSelected && binding.tvSelectAllSocialNetwork.text.toString() == getString(R.string.enabled_all)){
                        changeTextEnabledOrDisabled(binding.tvSelectAllSocialNetwork)
                    }

                    binding.llSocialNetwork.addView(bindingImageHome.root)
                }
                DONATIONS -> {
                    val bindingImageHome =
                        ItemModuleBinding.inflate(LayoutInflater.from(context))

                    it.id?.let {
                        bindingImageHome.tvModule.id = it
                    }

                    bindingImageHome.tvModule.text = it.name
                    var isSelected = false

                    it.enabled?.let {
                        if (it) {
                            isSelected = true
                        }
                    }



                    fillModulesSelected(bindingImageHome.tvModule, isSelected)

                    /*bindingImageHome.tvModule.setOnClickListener {
                        val textView = (it as TextView)
                        fillModulesSelected(textView, textView.currentTextColor == Color.GRAY)
                    }*/

                    if(isSelected && binding.tvSelectAllDonations.text.toString() == getString(R.string.enabled_all)){
                        changeTextEnabledOrDisabled(binding.tvSelectAllDonations)
                    }

                    binding.llDonations.addView(bindingImageHome.root)
                }
                SOS -> {
                    val bindingImageHome =
                        ItemModuleBinding.inflate(LayoutInflater.from(context))

                    it.id?.let {
                        bindingImageHome.tvModule.id = it
                    }

                    bindingImageHome.tvModule.text = it.name
                    var isSelected = false

                    it.enabled?.let {
                        if (it) {
                            isSelected = true
                        }
                    }

                    fillModulesSelected(bindingImageHome.tvModule, isSelected)

                    /*bindingImageHome.tvModule.setOnClickListener {
                        val textView = (it as TextView)
                        fillModulesSelected(textView, textView.currentTextColor == Color.GRAY)
                    }*/

                    if(isSelected && binding.tvSelectAllSOS.text.toString() == getString(R.string.enabled_all)){
                        changeTextEnabledOrDisabled(binding.tvSelectAllSOS)
                    }

                    binding.llSOS.addView(bindingImageHome.root)
                }
                APPOINT_ADMINISTRATOR -> {
                    val bindingImageHome =
                        ItemModuleBinding.inflate(LayoutInflater.from(context))

                    it.id?.let {
                        bindingImageHome.tvModule.id = it
                    }

                    bindingImageHome.tvModule.text = it.name
                    var isSelected = false

                    it.enabled?.let {
                        if (it) {
                            isSelected = true
                        }
                    }

                    fillModulesSelected(bindingImageHome.tvModule, isSelected)

                    /*bindingImageHome.tvModule.setOnClickListener { item ->
                        val textView = (item as TextView)
                        fillModulesSelected(textView, textView.currentTextColor == Color.GRAY)

                        if (textView.currentTextColor == Color.WHITE) {
                            //Se seleccionan todas las opciones en automático
                            for (i in 0 until binding.llNameAdmin.childCount) {
                                val itemCheck =
                                    ((binding.llNameAdmin.getChildAt(i) as ViewGroup)[0] as TextView)

                                fillModulesSelected(
                                    itemCheck,
                                    textView.currentTextColor == Color.WHITE
                                )
                            }
                        }
                    }*/

                    if(isSelected && binding.tvSelectAllNameAdmin.text.toString() == getString(R.string.enabled_all)){
                        changeTextEnabledOrDisabled(binding.tvSelectAllNameAdmin)
                    }

                    binding.llNameAdmin.addView(bindingImageHome.root)
                }
            }
        }
    }

    fun unselectAllOptions(textView: TextView) {
        modulesSelected.remove(RequestModuleUpdate(textView.id))
        textView.apply {
            setTextColor(Color.GRAY)
            setBackgroundColor(Color.WHITE)
        }
    }

    fun selectOptionsModule(textView: TextView) {

        modulesSelected.add(RequestModuleUpdate(textView.id))

        textView.apply {
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1c75bc"))
        }
    }

    fun showHelpMsg(msg: String) {
        UtilAlert
            .Builder()
            .setTitle("Aviso")
            .setMessage(msg)
            .build()
            .show(childFragmentManager, "")
    }
}