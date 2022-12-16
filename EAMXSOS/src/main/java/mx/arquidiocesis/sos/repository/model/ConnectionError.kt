package mx.arquidiocesis.sos.repository.model

import java.io.IOException

class ConnectionError : IOException(){
    override val message: String?
        get() = "Tu dispositivo no cuenta con conexión a internet, revisa tu wifi o datos moviles"
}