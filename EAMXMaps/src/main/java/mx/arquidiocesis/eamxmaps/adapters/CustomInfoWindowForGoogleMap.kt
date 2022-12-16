package mx.arquidiocesis.eamxmaps.adapters

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.View

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.item_map.view.*
import mx.arquidiocesis.eamxcommonutils.util.imagen.FunImagen
import mx.arquidiocesis.eamxmaps.R
import mx.arquidiocesis.eamxmaps.model.IgleciasModel

class CustomInfoWindowGoogleMap(
    val context: Context
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker): View {

        var view = (context as Activity).layoutInflater.inflate(R.layout.item_map, null)
        var igleciasModel: IgleciasModel = p0.tag as IgleciasModel
        view.tvAssociation.text = igleciasModel.name
        var file = FunImagen().imagenInterna(context, igleciasModel.id.toString())

        if(file.exists()){
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            view.ivChurchMap.setImageBitmap(myBitmap)
        }else{
            view.ivChurchMap.setImageResource(R.drawable.emptychurch)
        }

       // Glide.with(context).load(file.absolutePath)
         //   .apply(RequestOptions().override(100, 100))
           // .into(view.ivChurchMap)


        return view
    }



    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}