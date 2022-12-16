package mx.arquidiocesis.eamxredsocialmodule.news.create.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxredsocialmodule.R
import java.security.AccessControlContext

class GetPerfilImagen {
     fun loadImageProfile(context: Context,url: String,imagen:ImageView) {
        val file = PublicFunctionsMaps().imagenInterna(
            context, eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name,
                EAMXTypeObject.STRING_OBJECT
            ).toString()
        )
        "Home File-> $file".log()
        if(file.exists()){
            "file.exists()-> ${file.exists()}".log()
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            "myBitmap-> $myBitmap".log()
            imagen.setImageBitmap(myBitmap)
        }else{
            val imageNew = eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
            val name = eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
            PublicFunctionsMaps().updateImage(context, imageNew, name){
                if(it){
                    val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    imagen.setImageResource(R.drawable.user)
                    imagen.setImageBitmap(myBitmap)
                }else{
                    imagen.setImageResource(R.drawable.user)
                }
            }
        }
    }
}