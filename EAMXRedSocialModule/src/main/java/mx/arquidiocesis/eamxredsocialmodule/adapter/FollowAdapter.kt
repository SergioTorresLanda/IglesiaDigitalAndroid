package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.loadByUrlIntDrawableerror
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemFollowBinding
import mx.arquidiocesis.eamxredsocialmodule.model.FollowModel


class FollowAdapter(
    var list: MutableList<FollowModel>,
    val listener: (FollowModel) -> Unit
) :
    RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFollowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listener, list[position])

    override fun getItemCount(): Int = list.size

    class ViewHolder(val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {}
        }

        fun bind(
            listener: (FollowModel) -> Unit,
            item: FollowModel
        ) {
            binding.apply {
                txtName.text = item.name
                item.image.let {
                    imgPriest.loadByUrlIntDrawableerror(
                        it.toString(),
                        R.drawable.user
                    )
                }
                val profileId = eamxcu_preferences.getData(
                    EAMXEnumUser.USER_ID_REDSOCIAL.name,
                    EAMXTypeObject.INT_OBJECT
                ) as Int
                if (profileId != item.id) {
                    ivSegir.visibility = View.VISIBLE
                    ivSeguiendo.visibility = View.GONE
                    item.relationshipStatus?.let { r ->
                        if (r == 1 || r == 3) {
                            ivSegir.visibility = View.GONE
                            ivSeguiendo.visibility = View.VISIBLE
                        }
                    }
                    ivSegir.setOnClickListener {
                        listener(item)
                    }
                } else {
                    ivSegir.visibility = View.GONE
                    ivSeguiendo.visibility = View.GONE
                    ivSeguiendo.setOnClickListener {
                        listener(item)
                    }
                }
            }
        }
    }
}