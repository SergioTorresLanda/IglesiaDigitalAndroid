package mx.arquidiocesis.servicios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_ACCEPT
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CANCEL
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.ACTION_CLOSE
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.ItemServicesAdminHistoryBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminHistoryModel
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel

class AdminHistoryAdapter(
    val list : MutableList<AdminHistoryModel>,
    val listener: (AdminServiceModel) -> Unit,
    val listenerDelete: (Pair<Int, Int>)-> Unit,
    val childFragmentManager: FragmentManager,
) : RecyclerView.Adapter<AdminHistoryAdapter.AdminServicesAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminServicesAdapterHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServicesAdminHistoryBinding.inflate(inflater, parent, false)
        return AdminServicesAdapterHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminServicesAdapterHolder, position: Int) {
        return holder.bind(list[position], listener, listenerDelete, childFragmentManager)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class AdminServicesAdapterHolder(val binding : ItemServicesAdminHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(historyItem : AdminHistoryModel, listener: (AdminServiceModel) -> Unit,
                 listenerDelete: (Pair<Int, Int>) -> Unit,
                 childFragmentManager : FragmentManager
        ){
            binding.apply {
                item = historyItem

                val adapterAdmin = AdminServicesAdapter(historyItem.list.toMutableList()){
                    listener(it)
                }

                rvServices.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                    adapter = adapterAdmin
                }

                val swipeHandler = object : SwipeToDeleteCallback(binding.root.context) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rvServices.adapter as AdminServicesAdapter
                        UtilAlert.Builder()
                            .setTitle(binding.root.context.getString(R.string.title_dialog_warning))
                            .setMessage(binding.root.context.getString(R.string.message_confirm_delete_service))
                            .setTextButtonOk(binding.root.context.getString(R.string.button_yes))
                            .setTextButtonCancel(binding.root.context.getString(R.string.button_no))
                            .setListener{ action ->
                                when(action){
                                    ACTION_ACCEPT -> {
                                        val item = adapter.list[viewHolder.position]
                                        adapter.removeAt(viewHolder.position)
                                        listenerDelete(Pair(item.id, viewHolder.position))
                                    }
                                    ACTION_CANCEL,
                                    ACTION_CLOSE -> {
                                        listenerDelete(Pair(-1,-1))
                                    }
                                }
                            }
                            .build()
                            .show(childFragmentManager, "")
                    }
                }

                val itemTouchHelper = ItemTouchHelper(swipeHandler)
                itemTouchHelper.attachToRecyclerView(rvServices)
            }
        }
    }

    fun clearList(){
        list.clear()
        notifyDataSetChanged()
    }
}