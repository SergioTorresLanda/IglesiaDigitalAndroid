package mx.arquidiocesis.eamxcommonutils.util.imagen

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log


class ImagenProfile {
     fun loadImageProfile(img: ImageView,context: Context) {
        val file = FunImagen().imagenInterna(
            context, eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name,
                EAMXTypeObject.STRING_OBJECT
            ).toString()
        )
        "Home File-> $file".log()
        if (file.exists()) {
            "file.exists()-> ${file.exists()}".log()
            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            "myBitmap-> $myBitmap".log()
            img.setImageBitmap(myBitmap)
        } else {
            val imageNew = eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
            val name = eamxcu_preferences.getData(
                EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name,
                EAMXTypeObject.STRING_OBJECT
            ) as String
            FunImagen().updateImage(context, imageNew, name) {
                if (it) {
                    val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    img.setImageResource(R.drawable.user)
                    img.setImageBitmap(myBitmap)
                } else {
                    img.setImageResource(R.drawable.user)
                }
            }
        }
    }
}