package mx.arquidiocesis.servicios.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.util.getViewModel
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.servicios.databinding.FragmentIntentionScheduleDayBinding
import mx.arquidiocesis.servicios.repository.Repository
import mx.arquidiocesis.servicios.viewModel.ServiciosViewModel
import java.util.*

class IntentionScheduleDayFragment : FragmentBase() {

    lateinit var binding: FragmentIntentionScheduleDayBinding
    private val viewModel: ServiciosViewModel by lazy {
        getViewModel {
            ServiciosViewModel(Repository(requireContext()))
        }
    }
    var idChurch: Int = 0
    lateinit var dateSelected: String

    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    lateinit var establecimientoToGet: String
    val DAYS = arrayOf(
        "Domingo",
        "Lunes",
        "Martes",
        "Miércoles",
        "Jueves",
        "Viernes",
        "Sábado"
    )

    val MONTHS = arrayOf(
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
    )

    companion object {
        fun newInstance(idChurch: Int): IntentionScheduleDayFragment {
            val fragment = IntentionScheduleDayFragment()
            fragment.idChurch = idChurch
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntentionScheduleDayBinding.inflate(inflater, container, false)
        val establecimientoNombre2 = arguments?.getString("ESTABLECIMIENTO_NAME")
        Log.d("IntentionScheduleDay", "establecimientoNombre: $establecimientoNombre2")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val establecimientoNombre = arguments?.getString("ESTABLECIMIENTO_NAME")

        if (establecimientoNombre != null || establecimientoNombre!="") {
            Log.d("IntentionScheduleDay", "establecimientoNombre: $establecimientoNombre")
            establecimientoToGet=establecimientoNombre.toString()
        } else {
            establecimientoToGet="Ha ocurrido un error, intente nuevamente."
        }
        initObservers()

        dateSelected = getDate(Date().time)

        binding.cvSchesule.setOnDateChangeListener(object : CalendarView.OnDateChangeListener {
            override fun onSelectedDayChange(p0: CalendarView, year: Int, month: Int, day: Int) {

                this@IntentionScheduleDayFragment.day = day
                this@IntentionScheduleDayFragment.month = month
                this@IntentionScheduleDayFragment.year = year

                dateSelected = "${day} de ${MONTHS[month]} del ${year}"
            }
        })

        binding.cvSchesule.minDate = Date().time

        binding.btnNext.setOnClickListener {
            NavigationFragment.Builder()
                .setActivity(requireActivity())
                .setView(requireView().parent as ViewGroup)
                .setAllowStack(false)
                .setFragment(
                    IntentionScheduleHourFragment.newInstance(
                        idChurch,
                        dateSelected,
                        getDay(
                            this@IntentionScheduleDayFragment.day,
                            this@IntentionScheduleDayFragment.month,
                            this@IntentionScheduleDayFragment.year
                        ),
                        this@IntentionScheduleDayFragment.day,
                        this@IntentionScheduleDayFragment.month,
                        this@IntentionScheduleDayFragment.year
                    )
                )
                .build().nextWithReplace()
        }

        showLoader()

        viewModel.getChurchDetail(idChurch)
    }

    fun initObservers() {
        viewModel.churchDetailResponse.observe(viewLifecycleOwner) {
            Log.d("IntentionScheduleDay", "churchDetailResponse: $it")
            binding.apply {
                Glide.with(requireContext())
                    .load(Uri.parse(it.image_url))
                    .into(ivChurchSchedule)
                //
                tvChurchNameSchedule.text = it.name
            }
            hideLoader()
        }
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .build().show(childFragmentManager, "")
        }


        viewModel.errorResponseExit.observe(viewLifecycleOwner) {
            hideLoader()
            UtilAlert.Builder()
                .setTitle("Aviso")
                .setMessage(it)
                .setListener {
                    activity?.onBackPressed()
                }
                .build().show(childFragmentManager, "")
        }
    }

    fun getDate(time: Long): String {
        val c = Calendar.getInstance()
        c.time = Date(time)

        this@IntentionScheduleDayFragment.day = c.get(Calendar.DAY_OF_MONTH)
        this@IntentionScheduleDayFragment.month = c.get(Calendar.MONTH)
        this@IntentionScheduleDayFragment.year = c.get(Calendar.YEAR)

        return "${c.get(Calendar.DAY_OF_MONTH)} de ${MONTHS[c.get(Calendar.MONTH)]} del ${
            c.get(
                Calendar.YEAR
            )
        }"
    }

    fun getDay(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        return DAYS[dayOfWeek.minus(1)]
    }
}