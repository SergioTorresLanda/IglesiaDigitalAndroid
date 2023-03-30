package mx.arquidiocesis.eamxevent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxevent.databinding.ItemEventVolunteerMultiuserBinding
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.GuestModel
import mx.arquidiocesis.eamxevent.model.VolunteerResponse

class GuestAdapter (
    var guestList: MutableList<GuestModel>,
    private val recyclerView: RecyclerView,
    //val listener:() ->Unit
) : RecyclerView.Adapter<GuestAdapter.ViewHolder>() {

    var items: ArrayList<GuestModel> = ArrayList()
    lateinit var binding : ItemEventVolunteerMultiuserBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemEventVolunteerMultiuserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(guestList[position],this)
    }

    override fun getItemCount(): Int = guestList.size

    fun deleteReceiptsList(guest: GuestModel) {
        guestList.remove(guest)
        recyclerView.post {
            notifyDataSetChanged()
        }
    }

    fun addItem(guest: GuestModel) {
        guestList.add(guest)
        recyclerView.post { notifyItemInserted(guestList.size - 1) }
    }

    fun clear() {
        guestList.clear()
        recyclerView.post { notifyDataSetChanged() }
    }

    class ViewHolder(val binding: ItemEventVolunteerMultiuserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: GuestModel,
            adapter: GuestAdapter
        ) {
            binding.apply {
                tvNombreInvitado.text = item.nombre
                tvTelInvitado.text = item.telefono

                btnEliminar.setOnClickListener {
                    adapter.deleteReceiptsList(item)
                }

            }
        }
    }
}