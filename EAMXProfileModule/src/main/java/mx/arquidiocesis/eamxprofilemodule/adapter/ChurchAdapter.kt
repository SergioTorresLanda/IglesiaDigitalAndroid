package mx.arquidiocesis.eamxprofilemodule.adapter

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.eamx_profile_info_fragment.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemChurchProfileBinding
import mx.arquidiocesis.eamxprofilemodule.model.ChurchAndDescriptionModel
import mx.arquidiocesis.eamxprofilemodule.model.DataWithDescription
import mx.arquidiocesis.eamxprofilemodule.model.update.base.ActivityChurchModel
import mx.arquidiocesis.eamxprofilemodule.viewmodel.SELECT_OTHER

class ChurchAdapter(
    private var churchList: MutableList<ChurchAndDescriptionModel>,
    private val recyclerView: RecyclerView,
    private val list : List<DataWithDescription>,
    val listener:() ->Unit
) : RecyclerView.Adapter<ChurchAdapter.ViewHolder>() {

    private var enabledChild : Boolean = true
    lateinit var binding : ItemChurchProfileBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemChurchProfileBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(churchList[position], list,this, enabledChild)
    }

    override fun getItemCount(): Int = churchList.size

    fun deleteReceiptsList(church: ChurchAndDescriptionModel) {
        eamxcu_preferences.saveData(EAMXEnumUser.CHURCH_UPDATE_FROM_PROFILE.name, true)
        val index = churchList.indexOf(church)
        churchList.remove(church)
        listener()
        recyclerView.post {
            notifyDataSetChanged()
        }
    }

    fun addItem(church: ChurchAndDescriptionModel, enabled : Boolean = true) {
        churchList.add(church)
        enabledChild = enabled;
        recyclerView.post { notifyItemChanged(churchList.size) }
    }

    fun existId(id: Int): Boolean  = churchList.any { it.church.id == id }

    fun clear() {
        churchList.clear()
        recyclerView.post { notifyDataSetChanged() }
    }

    class ViewHolder(val binding: ItemChurchProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ChurchAndDescriptionModel,
            list: List<DataWithDescription>,
            adapter: ChurchAdapter,
            enabled : Boolean,
        ) {
            binding.apply {

                tvTitleChurch.text = item.church.name

                if (item.church.imageUrl != null) {
                    Glide.with(root.context)
                        .load(Uri.parse(item.church.imageUrl))
                        .error(root.context.getDrawable(R.drawable.emptychurch))
                        .into(ivChurch)
                }

                setFunctionSpinner(binding, list) { data ->
                    item.activity = data
                }

                if (item.activity.description.isNotEmpty()) {
                    var idxServProv = list.indexOf(DataWithDescription(item.activity.description,item.activity.id))
                    "idServProv: ".plus(item.activity.id).plus(" idxServProv: ").plus(idxServProv).log()
                    if (idxServProv != -1) {
                        spServicesProvided.setSelection(idxServProv)
                    }
                }

                setOtherEditText(binding) { data ->
                    item.activity.otherDescription = data.toString()
                }

                //the id 10 is other activity
                if (item.activity.id == 10) {
                    etDescribeService.setText(item.activity.otherDescription)
                }

                ivCleanChurch.setOnClickListener {
                    adapter.deleteReceiptsList(item)
                }

                //spServicesProvided.isEnabled = enabled
                //etDescribeService.isEnabled = enabled
                //ivCleanChurch.isEnabled = enabled
            }
        }

        private fun setOtherEditText(binding: ItemChurchProfileBinding, listener: (Any) -> Unit) {
            binding.etDescribeService.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                   listener(s.toString())
                }

            })
        }

        private fun setFunctionSpinner(binding : ItemChurchProfileBinding, list: List<DataWithDescription>,
                                       listener : (ActivityChurchModel) -> Unit){

            binding.spServicesProvided.apply {
                adapter  = AdapterCustomSpinner(
                    binding.root.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
                )
            }

            binding.spServicesProvided.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = binding.spServicesProvided.selectedItem as DataWithDescription

                    listener(ActivityChurchModel(
                            id = item.id,
                            description = item.description
                    ))

                    if(item.description == SELECT_OTHER){
                        binding.etDescribeService.visibility = View.VISIBLE
                        //binding.etDescribeService.setText("")
                    }else{
                        binding.etDescribeService.visibility = View.GONE
                        //binding.etDescribeService.setText("")
                    }
                }
            }
        }

    }
}