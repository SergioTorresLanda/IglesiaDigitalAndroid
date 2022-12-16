package mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.viewmodel
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.ConfirmPasswordModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.RequestUserInfoModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.model.SendCodeModel
import mx.arquidiocesis.eamxregistromodule.ui.forgotpaswword.repository.EAMXForgotPasswordRepository
import kotlin.collections.set

const val ERROR_CODE = "ERROR_CODE"
const val ERROR_PASSWORD = "PASSWORD"
const val ERROR_CONFIRM_PASSWORD = "CONFIRM_PASSWORD"
const val ERROR_EMAIL = "EMAIL"

class EAMXForgotPasswordViewModel (
        private val repository: EAMXForgotPasswordRepository) : ViewModel() {

    private var _launchRequestCode = MutableLiveData<Boolean>();
    var launchRequestCode: LiveData<Boolean> = _launchRequestCode

    val responseInfoUser = repository.userInfoResponse
    val responseSendCode = repository.sendCodeResponse
    val responseConfirmPassword = repository.confirmPasswordResponse
    val responseError = repository.errorResponse
    val responseErrorConfirmPassword = repository.errorConfirmPassword


    private var _timer: MutableLiveData<String> = MutableLiveData()
    val timer: LiveData<String> = _timer

    private val regexEmail = "[a-zA-Z0-9]+[a-zA-Z0-9._%+-]+[^\\.]@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"

    private val _validationInputs = MutableLiveData<HashMap<String, String>>()
    val validationInputs: LiveData<HashMap<String, String>> = _validationInputs


    fun getInfoCode(userName: String) {
        GlobalScope.launch {
            val request = RequestUserInfoModel(userName)
            repository.getUserInfo(request)
        }
    }

    fun sendCode(user: String, listenerShowLoader: () -> (Unit)) {
        val validationInput = this.validationConfirmPassword(user)

        val userOk = if (user.isDigitsOnly()) {
            "+52$user"
        } else {
            user
        }

        if (validationInput) {
            listenerShowLoader()
            val request = SendCodeModel(userOk)
            this.launchSendCode(request)
        }
    }

    fun confirmPassword(
            user: String,
            code: String,
            password: String,
            confirmPassword: String,
            listenerShowLoader: () -> (Unit)
    ) {
        val validationInput = this.validationConfirmPassword(code, password, confirmPassword)

        val userOk = if (user.isDigitsOnly()) {
            "+52$user"
        } else {
            user
        }

        if (validationInput) {
            listenerShowLoader()
            val request = ConfirmPasswordModel(
                    userOk,
                    code,
                    password
            )
            this.launchConfirmPassword(request)
        }
    }

    private fun validationConfirmPassword(user: String) : Boolean {
        val mapError = HashMap<String, String>()

        if (user.isEmpty()) {
            mapError[ERROR_EMAIL] = "Debes ingresar un correo electrónico válido o un número telefónico válido"
        }

        val isNumber = user.isDigitsOnly()
        if (isNumber) {
            if (user.isNotEmpty() && user.length != 10) {
                mapError[ERROR_EMAIL] = "Debes ingresar un número telefónico válido"
            }
        } else {
            if (user.isNotEmpty() && !user.matches(Regex(regexEmail))) {
                mapError[ERROR_EMAIL] = "Debes ingresar un correo electrónico válido"
            }
        }


        if (mapError.isNotEmpty()) {
            _validationInputs.value = mapError
        }

        return mapError.isEmpty()
    }
     fun cleanErrors(){
        _validationInputs.value?.clear()
    }

    private fun validationConfirmPassword(code: String,
                                          password : String,
                                          confirmPassword : String,
    ) : Boolean{
         val mapError = HashMap<String, String>()

        if(code.isEmpty()){
            mapError[ERROR_CODE] = "Debes ingresar el código de confirmación"
        }

        if (code.isNotEmpty() && code.length != 6) {
            mapError[ERROR_CODE] = "Debes ingresar los seis dígitos del código de confirmación"
        }

        if (password.isEmpty()) {
            mapError[ERROR_PASSWORD] = "Debes ingresar la contraseña"
        }

        if (password.length < 8) {
            mapError[ERROR_PASSWORD] = "La contraseña debe ser de mínimo 8 caracteres"
        } else if (!passwordValidation(password)) {
            mapError[ERROR_PASSWORD] = "Valida la estructura de la contraseña"
        }

        if (password != confirmPassword) {
            mapError[ERROR_CONFIRM_PASSWORD] = "Ingresa una confirmación de contraseña válida"
        }

        if (mapError.isNotEmpty()) {
            _validationInputs.value = mapError
        }

        return mapError.isEmpty()
    }

    private fun launchConfirmPassword(request : ConfirmPasswordModel){
        GlobalScope.launch {
            repository.confirmPassword(request)
        }
    }

     private fun launchSendCode(request: SendCodeModel) {
        GlobalScope.launch {
            repository.sendCode(request)
        }
    }

    private fun passwordValidation(password: String): Boolean {
        var clave: Char
        var containNumber: Byte = 0
        var containToUpper: Byte = 0
        var containEspecial: Byte = 0

        for (element in password) {
            clave = element
            val passValue = clave.toString()
            when {
                passValue.matches("[A-Z]".toRegex()) -> {
                    containToUpper++
                }
                passValue.matches("[0-9]".toRegex()) -> {
                    containNumber++
                }
                passValue.matches("[=+^\$*.\\[\\]\\{}()?\\\"!-?@#%&/\\,><':;|_~`]".toRegex()) -> {
                    containEspecial++
                }
            }
        }

        return containToUpper >= 1.toByte() && containNumber >= 1.toByte() && containEspecial >= 1.toByte()
    }


    fun counterJob() {

        var minutes = 0
        var seconds = 59

        _timer.value = "$minutes:${seconds}"

        GlobalScope.launch(Dispatchers.IO) {
            while (minutes >= 0) {
                Thread.sleep(1000)
                if (seconds == 0) {
                    if (minutes == 0) {
                        withContext(Dispatchers.Main) {
                            _timer.value = "0:00"
                        }
                        break
                    } else {
                        minutes--
                        val minuteString = if (minutes < 10) {
                            "0$minutes"
                        } else {
                            minutes.toString()
                        }
                        seconds = 59
                        withContext(Dispatchers.Main) {
                            _timer.value = "0$minuteString:${seconds}"
                        }
                    }
                } else {
                    seconds--
                    val secondString = if (seconds < 10) {
                        "0$seconds"
                    } else {
                        seconds.toString()
                    }
                    withContext(Dispatchers.Main) {
                        _timer.value = "$minutes:${secondString}"
                    }
                }
            }
        }
    }
}