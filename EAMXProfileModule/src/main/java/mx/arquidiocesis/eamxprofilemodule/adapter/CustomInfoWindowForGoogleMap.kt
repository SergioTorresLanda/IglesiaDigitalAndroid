package mx.arquidiocesis.eamxprofilemodule.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.item_church_profile_map.view.*
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.model.ChurchModel

class CustomInfoWindowGoogleMap(
    val context: Context
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker): View {

        var view =
            (context as Activity).layoutInflater.inflate(R.layout.item_church_profile_map, null)
        var churchModel: ChurchModel = p0.tag as ChurchModel

        view.tvAssociation.text = churchModel.name
        if(!churchModel.imageUrl.isNullOrEmpty()){
            Glide.with(context).load(Uri.parse(churchModel.imageUrl))
                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.emptychurch)
                .into(view.ivChurchMap)
        }



        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}