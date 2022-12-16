package mx.arquidiocesis.sos.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.FragmentNotificationSOSFielBinding
import kotlinx.coroutines.Job
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CANCEL
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.userAllowAccessAsAdmin
import mx.arquidiocesis.sos.model.ProgressHistoryModel
import mx.arquidiocesis.sos.model.ServiceBoxModel
import mx.arquidiocesis.sos.repository.SOSRepository
import mx.arquidiocesis.sos.utils.Constants
import mx.arquidiocesis.sos.utils.Constants.Status.Companion.CANCELLED
import mx.arquidiocesis.sos.utils.Constants.Status.Companion.COMPLETED
import mx.arquidiocesis.sos.utils.Constants.Status.Companion.IN_PROGRESS
import mx.arquidiocesis.sos.utils.Constants.Status.Companion.SENT
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.ACCEPTED
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.CALL_FINISHED
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.CALL_WAITING
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.HELP_ON_THE_WAY
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.LOOKING_FOR_ASSISTANCE
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.PENDING_CONFIRMATION
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.SUCCESSFULLY
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.UNSUCCESSFULLY
import mx.arquidiocesis.sos.viewmodel.SOSServicesFaithfulViewModel

class SOSNotificationFaithfulFragment : FragmentBase() {

    companion object {
        fun newInstance(): SOSNotificationFaithfulFragment {
            val fragment = SOSNotificationFaithfulFragment()
            return fragment
        }
    }

    private val TAG = SOSNotificationFaithfulFragment.javaClass.name

    private val notificationViewModel: SOSServicesFaithfulViewModel by lazy {
        getViewModel {
            SOSServicesFaithfulViewModel(SOSRepository(requireContext()))
        }
    }
    lateinit var binding: FragmentNotificationSOSFielBinding
    private var idService = 0
    private var serviceName = ""
    private var serviceId = 0
    lateinit var job: Job
    var phone = ""
    var fromSOS = true
    var esSaserdote = false
    var nombre = ""
    var direccion = ""
    var isCalificate = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationSOSFielBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userIsAdmin = userAllowAccessAsAdmin(EAMXEnumUser.USER_PERMISSION_SOS.name)

        esSaserdote = userIsAdmin
        fromSOS = eamxcu_preferences.getData(
            "FROMSOS",
            EAMXTypeObject.BOOLEAN_OBJECT
        ) as Boolean
        loadView()
        initObservers()

    }

    private fun loadView() {
        //
        if (arguments != null &&
            arguments?.get(ID) != null
        ) {//Se agrego arguments
            idService = requireArguments().getInt(ID)
            loadCounter()

        }
        /*  }else{
              notificationViewModel.getPriestServicesWithDevotee(notificationViewModel.getUserId())
              showLoader()
          }
  */

    }

    private fun loadCounter() {
        if (esSaserdote) {
            binding.apply {
                tvTitleSolicita.text = "Solicitante"
                tvConfirma.text = "Recibido"
                clSolicitada.visibility = View.GONE
                clButon.visibility = View.VISIBLE
                clDirrecion.visibility = View.VISIBLE
                clTelefono.visibility = View.VISIBLE
                tvDistancia.visibility = View.VISIBLE
                btnCancelarServicio.visibility = View.GONE
            }
            showLoader()
            notificationViewModel.getServices(idService)

        } /*else if (!fromSOS) {
            showLoader()
            notificationViewModel.pendiente(
                notificationViewModel.getUserId(),
                "FIEL",
                notificationViewModel.itemService!!.id
            )

        }*/ else {
            showLoader()
            job = notificationViewModel.initCounter()

        }
// showAlertProcess()
    }


    private fun showAlertProcess() {
        UtilAlert.Builder()
            .setTitle(getString(R.string.text_title_service_in_progress))
            .setMessage(getString(R.string.text_message_service_in_progress))
            .build()
            .show(childFragmentManager, TAG)
    }

    private fun showAlertCancelRequest() {
        UtilAlert.Builder()
            .setTitle("¿DESEAS cancelar tu solicitud?")
            .setTextButtonOk(getString(R.string.text_button_si))
            .setTextButtonCancel(getString(R.string.text_button_no))
            .setListener { action ->
                when (action) {
                    ACTION_ACCEPT -> {
                        parentFragmentManager.popBackStack()
                        parentFragmentManager.popBackStack()
                        parentFragmentManager.popBackStack()
                    }
                    ACTION_CANCEL -> {

                    }
                }
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun alertCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        startActivity(intent)
        UtilAlert.Builder()
            .setTitle("Realizar llamada")
            .setMessage(
                "Comunicate con el solicitante\n" +
                        "¿Se realizó la llamada con éxito?"
            )
            .setTextButtonOk(getString(R.string.text_button_si))
            .setTextButtonCancel(getString(R.string.text_button_no))
            .setListener { action ->
                when (action) {
                    ACTION_ACCEPT -> {
                        showLoader()
                        notificationViewModel.setServices(
                            idService,
                            "CALL_FINISHED"
                        )
                    }
                    ACTION_CANCEL -> {
                        showLoader()
                        notificationViewModel.setServices(
                            idService,
                            "CANCELLED"
                        )
                    }
                }
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun alertConfirmar() {
        UtilAlert.Builder()
            .setTitle("Confirmar")
            .setIsCancel(false)
            .setMessage(
                "¿Se realizará el servicio?"
            )
            .setTextButtonOk(getString(R.string.text_button_si))
            .setTextButtonCancel(getString(R.string.text_button_no))
            .setListener { action ->
                when (action) {
                    ACTION_ACCEPT -> {
                        showLoader()
                        notificationViewModel.setServices(
                            idService,
                            "LOOKING_FOR_ASSISTANCE"
                        )
                    }
                    ACTION_CANCEL -> {
                        showLoader()
                        notificationViewModel.setServices(
                            idService,
                            "CANCELLED"
                        )
                    }
                }
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun alertRealizado() {
        UtilAlert.Builder()
            .setTitle("Confirmar")
            .setIsCancel(false)
            .setMessage(
                "¿Se realizó con éxito el servicio?"
            )
            .setTextButtonOk(getString(R.string.text_button_si))
            .setTextButtonCancel(getString(R.string.text_button_no))
            .setListener { action ->
                when (action) {
                    ACTION_ACCEPT -> {
                        showLoader()
                        notificationViewModel.setServices(
                            idService,
                            "SUCCESSFULLY"
                        )
                    }
                    ACTION_CANCEL -> {
                        showLoader()
                        notificationViewModel.setServices(
                            idService,
                            "UNSUCCESSFULLY"
                        )
                    }
                }
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun alertIngresardatos(priestList: List<ServiceBoxModel>) {
        mx.arquidiocesis.sos.ui.SOSDialogFragment
            .Builder()
            .setTipo(4)
            .setIsCancel(false)
            .setOptions(priestList)
            .setTitle("Ingresar datos")
            .setSubTitleDos("Fecha y hora")
            .setListener { dialog ->
                showLoader()
                notificationViewModel.actualizaHora(idService, dialog.star, dialog.text2)
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun alertCalifica() {
        mx.arquidiocesis.sos.ui.SOSDialogFragment
            .Builder()
            .setTipo(3)
            .setIsCancel(false)
            .setTitle("Gracias por utilizar Iglesia Digital, ¿te gustaría dejar algún comentario?")//Califica el servicio
            .setSubTitleDos("Comentarios")
            .setListener { dialog ->
                showLoader()
                notificationViewModel.actualizaCalificacion(idService, dialog.star, dialog.text2)
                requireActivity().run {
                    startActivity(
                        Intent().apply {
                            setClassName(
                                requireActivity(),
                                "mx.arquidiocesis.eamxgeneric.activities.EAMXHomeActivity"
                            )
                        })
                    finish()
                }
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun AlertOtro() {
        UtilAlert.Builder()
            .setTitle("Porfavor selecciona otro párroco")
            .setTextButtonOk(getString(R.string.text_button_seleciona))
            .setListener { it ->
                when (it) {
                    ACTION_ACCEPT -> {
                        changeSelection()


                    }
                }
            }
            .build()
            .show(childFragmentManager, "")
    }

    fun AlertTimeout() {
        UtilAlert.Builder()
            .setTitle("¿Recibiste la confirmación del párroco?")
            .setTextButtonOk(getString(R.string.text_button_si))
            .setTextButtonCancel(getString(R.string.text_button_no))
            .setListener { action ->
                when (action) {
                    ACTION_ACCEPT -> {
                        notificationViewModel.getServices(idService)
                        showLoader()
                    }
                    ACTION_CANCEL -> {
                        AlertOtro()
                    }
                }
            }
            .build()
            .show(childFragmentManager, TAG)
    }

    fun changeSelection() {
        val bundle = Bundle().apply {
            putInt(SERVICE_ID, serviceId)
            putString(SERVICE_NAME, serviceName)
            putInt(ID, idService)
            putString(NOMBRE, nombre)
            putString(DIRECCION, direccion)
        }

        val fragment = SOSListPriestChurchFaithfulFragment()
        fragment.arguments = bundle
        val transaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            (requireView().parent as ViewGroup).id,
            fragment
        )
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    private fun initObservers() {
        notificationViewModel.servicioPendiente.observe(viewLifecycleOwner) {
            if (it != null) {
                var id = 0
                when (it.status) {
                    Constants.Status.COMPLETED -> {

                    }
                    Constants.Status.CANCELLED -> {
                    }
                    else -> {
                        id = it.service_id
                    }
                }
                if (id == 0) {
                    requireActivity().onBackPressed()
                } else {
                    showLoader()
                    job = notificationViewModel.initCounter()
                }
            }

            hideLoader()

        }

        notificationViewModel.counterMinutes.observe(viewLifecycleOwner) {
            if (it != null) {
                notificationViewModel.getServices(idService)
            }
        }
        notificationViewModel.counterSeconds.observe(viewLifecycleOwner) {
            when (it.toInt()) {
                50 -> {
                    notificationViewModel.getServices(idService)

                }
                40 -> {
                    notificationViewModel.getServices(idService)

                }
                30 -> {
                    notificationViewModel.getServices(idService)

                }
                20 -> {
                    notificationViewModel.getServices(idService)

                }
                10 -> {
                    notificationViewModel.getServices(idService)
                }
            }


            // binding.tvSecond.text = it
        }
        notificationViewModel.serviceDetalle.observe(viewLifecycleOwner) {
            binding.apply {
                if (esSaserdote) {
                    tvTitulo.text = it.service.name
                    tvParroco.text = it.devotee.name
                    tvDistancia.text = it.location.distance.toString()
                    tvDirrecion.text = it.address.description
                    tvTelefono.text = it.devotee.phone
                    serviceId = it.service.id
                    serviceName = it.service.name
                    if (!it.devotee.phone.isNullOrEmpty()) {
                        phone = it.devotee.phone
                        ivTelefono.setOnClickListener {
                            alertCall()
                        }
                    }
                    btnAcceptDetalle.setOnClickListener {
                        showLoader()
                        clButon.visibility = View.GONE
                        notificationViewModel.setServices(
                            idService,
                            "ACCEPTED"
                        )
                    }
                    btnCancelDetalle.setOnClickListener {
                        showLoader()
                        clButon.visibility = View.GONE
                        notificationViewModel.setServices(
                            idService,
                            "REJECTED"
                        )
                    }
                    if (it.progress_history.isNotEmpty()) {
                        val status = it.progress_history.last()
                        when (status.status) {
                            SENT -> {
                                statusSent(status, true)
                            }
                            IN_PROGRESS -> {
                                ultimoStatus(it.progress_history, false)
                                statusInProgress(status, true)
                            }
                            COMPLETED -> {
                                ultimoStatus(it.progress_history, true)

                                statusComplete(status)
                            }
                            CANCELLED -> {
                                ultimoStatus(it.progress_history, false)
                                statusCancel(status)
                            }
                        }
                    }
                } else {
                    tvTitulo.text = it.service.name
                    tvParroco.text = it.support_contact?.name
                    tvFecha.text =
                        notificationViewModel.parseDateToddMMyy(it.creation_date) + " hrs"
                    serviceId = it.service.id
                    serviceName = it.service.name
                    btnCancelarServicio.setOnClickListener {
                        showLoader()
                        btnCancelarServicio.isEnabled = false
                        notificationViewModel.setServices(
                            idService,
                            "CANCELLED"
                        )
                    }
                    if (it.progress_history.isNotEmpty()) {
                        val status = it.progress_history.last()
                        when (status.status) {
                            SENT -> {
                                statusSent(status, true)
                            }
                            IN_PROGRESS -> {
                                ultimoStatus(it.progress_history, false)
                                statusInProgress(status, true)
                            }
                            COMPLETED -> {
                                ultimoStatus(it.progress_history, true)
                                statusComplete(status)
                            }
                            CANCELLED -> {
                                ultimoStatus(it.progress_history, false)
                                nombre = it.funeral_home.toString()
                                direccion = it.address.description
                                statusCancel(status)
                            }
                        }
                    }
                }
            }

            hideLoader()

        }

        notificationViewModel.updateStatus.observe(viewLifecycleOwner) {
            when (it) {

                2 -> {
                    requireActivity().onBackPressed()
                    hideLoader()
                    job.cancel()
                }
                else -> {
                    notificationViewModel.getServices(idService)

                }
            }

        }
        notificationViewModel.launchMessageCounter.observe(viewLifecycleOwner)
        {
            AlertTimeout()
        }
        notificationViewModel.priestList.observe(viewLifecycleOwner) {
            hideLoader()
            alertIngresardatos(it)
        }
        notificationViewModel.errorResponse.observe(viewLifecycleOwner)
        {
            hideLoader()
            UtilAlert
                .Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build()
                .show(childFragmentManager, "")

            if (!esSaserdote) {
                job.cancel()
            }
        }
    }

    fun ultimoStatus(statusList: List<ProgressHistoryModel>, isProgress: Boolean) {
        var ultimoSent: ProgressHistoryModel? = null
        var ultimoProgress: ProgressHistoryModel? = null
        statusList.forEach {
            when (it.status) {
                SENT -> {
                    ultimoSent = it
                }
                IN_PROGRESS -> {
                    ultimoProgress = it
                }
            }
        }
        statusSent(ultimoSent!!, false)
        if (isProgress) {
            statusInProgress(ultimoProgress!!, false)
        }

    }

    fun statusSent(status: ProgressHistoryModel, actual: Boolean) {
        binding.apply {
            tvConfirma.setTextColor(resources.getColor(R.color.dorado))
            ivConfirmada.setImageResource(R.drawable.ic_none)
            tvConfirmaHora.text = notificationViewModel.parseDateToddMMyy(status.creation_date)
            when (status.sub_status) {
                PENDING_CONFIRMATION -> {
                    tvConfirma.setTextColor(resources.getColor(R.color.black))
                    ivConfirmada.setImageResource(R.drawable.ic_progreso)
                    tvConfirmaStatus.text = "Por aceptar"
                }
                ACCEPTED -> {

                    tvConfirmaStatus.text = "Aceptada"
                    if (esSaserdote && actual) {
                        alertCall()
                    }
                }
                CALL_WAITING -> {
                    tvConfirmaStatus.text = "Aceptada  \n" +
                            "Llamada en espera"
                }
                CALL_FINISHED -> {
                    tvConfirmaStatus.text = "Aceptada  \n" +
                            "Llamada realizada"
                    ivTelefono.visibility = View.GONE
                    if (esSaserdote && actual) {
                        alertConfirmar()
                    }
                }
            }
        }

    }

    fun statusInProgress(status: ProgressHistoryModel, actual: Boolean) {
        binding.apply {
            ivConfirmada.setImageResource(R.drawable.ic_paloma)
            ivProgreso.setImageResource(R.drawable.ic_progreso)
            tvProgreso.setTextColor(resources.getColor(R.color.dorado))
            tvProgresoHora.text = notificationViewModel.parseDateToddMMyy(status.creation_date)
            clButon.visibility = View.GONE
            if (!esSaserdote) {
                // job.cancel()
                btnCancelarServicio.isEnabled = true
                btnCancelarServicio.background =
                    requireActivity().getDrawable(R.drawable.background_button_cancel)
                btnCancelarServicio.setTextColor(Color.BLUE)
            }

            when (status.sub_status) {
                LOOKING_FOR_ASSISTANCE -> {
                    tvProgresoStatus.text = "Si se realizará el servicio"
                    if (esSaserdote && actual) {
                        notificationViewModel.getPrientsList()
                    }
                }
                HELP_ON_THE_WAY -> {
                    tvProgresoStatus.text = "Ayuda en camino"
                    if (esSaserdote && actual) {
                        alertRealizado()
                    }
                }

            }
        }


    }

    fun statusComplete(status: ProgressHistoryModel) {
        binding.apply {
            ivProgreso.setImageResource(R.drawable.ic_paloma)
            ivCompletada.setImageResource(R.drawable.ic_progreso)
            tvCompletaHora.text = notificationViewModel.parseDateToddMMyy(status.creation_date)
            if (!esSaserdote && isCalificate) {
                isCalificate = false
                alertCalifica()

            }
            when (status.sub_status) {
                SUCCESSFULLY -> {
                    tvCompletaStatus.text = "Con éxito"
                }
                UNSUCCESSFULLY -> {
                    tvCompletaStatus.text = "Sin éxito"

                }

            }
        }
    }

    fun statusCancel(status: ProgressHistoryModel) {
        binding.apply {
            ivProgreso.visibility = View.GONE
            tvProgreso.visibility = View.GONE
            tvProgresoStatus.visibility = View.GONE
            tvProgresoHora.visibility = View.GONE
            ivLine2.visibility = View.GONE

            if (!esSaserdote) {
                job.cancel()
                AlertOtro()

            } else {
                ivTelefono.visibility = View.GONE
            }
            ivCompletada.setImageResource(R.drawable.ic_progreso)
            tvCompletaHora.text = status.creation_date
            tvCompleta.text = "Cancelado"
        }
    }
}