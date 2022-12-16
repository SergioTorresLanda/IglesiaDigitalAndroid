package mx.arquidiocesis.oraciones.utils

import android.view.View
import com.airbnb.lottie.LottieAnimationView

class PublicFunctions {

    fun mostrarLottie(lottie: LottieAnimationView, mostrar: Boolean) {
        if (mostrar) {
            lottie.visibility = View.VISIBLE
            lottie.playAnimation()
        } else {
            lottie.cancelAnimation()
            lottie.visibility = View.GONE
        }

    }
 }
