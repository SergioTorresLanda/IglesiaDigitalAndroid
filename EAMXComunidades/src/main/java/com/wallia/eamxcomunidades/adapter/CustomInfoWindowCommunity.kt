package com.wallia.eamxcomunidades.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.model.CommunityMapModel
import kotlinx.android.synthetic.main.item_community_map.view.*
import mx.arquidiocesis.eamxcommonutils.util.imagen.FunImagen


class CustomInfoWindowCommunity(
    val context: Context
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker): View {

        var view = (context as Activity).layoutInflater.inflate(R.layout.item_community_map, null)
        var it = p0.tag as CommunityMapModel
        view.tvCommunityMap.text = it.name
        var file = FunImagen().imagenInterna(context, it.id.toString())

        if(file.exists()){
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            view.ivCommunityMap.setImageBitmap(myBitmap)
        }else{
            view.ivCommunityMap.setImageResource(R.drawable.emptychurch)
        }

        // Glide.with(context).load(file.absolutePath)
        //   .apply(RequestOptions().override(100, 100))
        // .into(view.ivChurchMap)


        return view
    }



    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}