package mx.arquidiocesis.sos.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.ItemPriestServiceBinding
import mx.arquidiocesis.sos.model.ServicesPriestModel
import mx.arquidiocesis.sos.utils.Constants
import mx.arquidiocesis.sos.utils.Constants.SubStatus.Companion.ACCEPTED

class PriestServicesAdapter(
    val services: List<ServicesPriestModel>,
    val listener: (Any) -> Unit
) :
    RecyclerView.Adapter<PriestServicesAdapter.PriestServicesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PriestServicesAdapter.PriestServicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPriestServiceBinding.inflate(inflater, parent, false)
        return PriestServicesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PriestServicesAdapter.PriestServicesViewHolder,
        position: Int
    ) {
        holder.bind(listener, services[position])
    }

    override fun getItemCount(): Int = services.size

    inner class PriestServicesViewHolder(val binding: ItemPriestServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: (ServicesPriestModel) -> Unit,
            service: ServicesPriestModel
        ) {
            binding.apply {
                tvService.text = service.service.name
                tvName.text = service.priest.name

                when (service.status) {
                    Constants.Status.IN_PROGRESS -> {//Listo

                        ivIcon.visibility = View.VISIBLE//ICONO IGLESIA
                        clCall.visibility = View.VISIBLE//ICONO LLAMADA
                        tvDistance.visibility = View.VISIBLE//DISTANCIA

                        tvStatus.visibility = View.INVISIBLE//STATUS
2
                        viewCircle.visibility = View.INVISIBLE//STATUS

                        clTrash.visibility = View.VISIBLE//BOTON CANCELAR
                        clInprogress.visibility = View.GONE
                        clCall.visibility = View.VISIBLE//BOTON ACEPTAR
                        ivFinished.visibility = View.GONE
                        ivBorder.visibility = View.VISIBLE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ivBorder.setBackgroundColor(binding.root.context.getColor(R.color.red))
                        }

                        tvDistance.text = "${service.location.distance} k.m."

                        clTrash.setOnClickListener {
                            listener(Pair("CANCEL", service))
                        }

                        clCall.setOnClickListener {
                            listener(Pair("ACCEPT", service))
                        }

                    }

                    ACCEPTED -> {//Listo
                        ivIcon.visibility = View.INVISIBLE//ICONO IGLESIA
                        clCall.visibility = View.GONE//ICONO LLAMADA
                        tvDistance.visibility = View.INVISIBLE//DISTANCIA

                        tvStatus.visibility = View.VISIBLE//STATUS
                        viewCircle.visibility = View.VISIBLE//STATUS

                        ivBorder.visibility = View.VISIBLE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ivBorder.setBackgroundColor(binding.root.context.getColor(R.color.blue))
                        }

                        clTrash.visibility = View.GONE
                        clInprogress.visibility = View.VISIBLE
                        ivFinished.visibility = View.GONE


                        tvStatus.text = "En progreso"
                        viewCircle.background = binding.root.context.getDrawable(R.drawable.circle_progress)

                        clDelete.setOnClickListener {
                            listener(Pair("CANCEL", service))
                        }

                        clTerminate.setOnClickListener {
                            listener(Pair("ACCEPT", service))
                        }
                    }

                    Constants.Status.REJECTED -> {
                        ivIcon.visibility = View.INVISIBLE//ICONO IGLESIA
                        clCall.visibility = View.GONE//ICONO LLAMADA
                        tvDistance.visibility = View.INVISIBLE//DISTANCIA

                        tvStatus.visibility = View.VISIBLE//STATUS
                        viewCircle.visibility = View.VISIBLE//STATUS

                        ivBorder.visibility = View.GONE

                        clTrash.visibility = View.GONE
                        clInprogress.visibility = View.GONE
                        ivFinished.visibility = View.GONE

                        //clCancel.visibility = View.GONE//BOTON CANCELAR
                        //clAccept.visibility = View.GONE//BOTON ACEPTAR

                        viewCircle.background = binding.root.context.getDrawable(R.drawable.circle_canceled)

                        tvStatus.text = "Rechazado"
                    }

                    Constants.Status.COMPLETED -> {//Ocultar botones
                        ivIcon.visibility = View.INVISIBLE//ICONO IGLESIA
                        clCall.visibility = View.GONE//ICONO LLAMADA
                        tvDistance.visibility = View.INVISIBLE//DISTANCIA

                        tvStatus.visibility = View.VISIBLE//STATUS
                        viewCircle.visibility = View.GONE//STATUS

                        ivBorder.visibility = View.GONE

                        clTrash.visibility = View.GONE
                        clInprogress.visibility = View.GONE
                        ivFinished.visibility = View.VISIBLE

                        tvStatus.text = "Concluido"
                    }

                    Constants.Status.CANCELLED -> {//Listo Sin botones
                        ivIcon.visibility = View.INVISIBLE//ICONO IGLESIA
                        clCall.visibility = View.GONE//ICONO LLAMADA
                        tvDistance.visibility = View.INVISIBLE//DISTANCIA

                        tvStatus.visibility = View.VISIBLE//STATUS
                        viewCircle.visibility = View.VISIBLE//STATUS

                        ivBorder.visibility = View.GONE

                        clTrash.visibility = View.GONE
                        clInprogress.visibility = View.GONE
                        ivFinished.visibility = View.GONE

                        tvStatus.text = "Cancelado"

                        viewCircle.background = binding.root.context.getDrawable(R.drawable.circle_canceled)
                    }
                }
            }
        }
    }
}