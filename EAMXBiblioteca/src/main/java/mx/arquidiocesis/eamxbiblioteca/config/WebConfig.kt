package mx.arquidiocesis.eamxbiblioteca.config

import mx.arquidiocesis.eamxbiblioteca.BuildConfig
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp

object WebConfi {
    val HOST = ConstansApp.hostEncuentro()
    const val CONTENTHOME = "/library/home"
    const val LIBRARYSEARCH = "/library"
    const val LIBRARYCONTENT = "/library/{content_id}"
    const val CATEGORYCONTENT =  "/library/category/{category}"
}