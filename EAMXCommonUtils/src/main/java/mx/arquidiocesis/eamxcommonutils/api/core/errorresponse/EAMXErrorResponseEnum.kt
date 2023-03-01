package mx.arquidiocesis.eamxcommonutils.api.core.errorresponse

enum class EAMXErrorResponseEnum(val messageError: String, val defaultError: String, val errorCode: Int) {
    ERROR_DB("Ha ocurrido un error en el servidor", "NO SE ENCUENTRA REGISTRADO FIELES CON LA INFORMACION COMPARTIDA", 101),
    ERROR_PASSWORD_FORMAT("Error en formato de password", "Error en formato de password", 102),
    USER_NOT_EXIST_ERROR("El usuario no existe","User do not exists", 105),
    CONEXION_ERROR("Tu conexion ha fallado", "Error conexion",1012),
    INVALID_CONFIRM_CODE("Codigo de verificacion invalido", "Invalid verification code", 106),
    USER_ALREADY_CONFIRMED("El correo o numero ya esta registrado", "User is already confirmed", 104),
    USER_NOT_PRIEST("No encontramos ningún registro de sacerdote asociado a este número. Verifícalo y/o ponte en contacto con un administrador.", "User is not a priest",104),
    USER_IS_NOT_CONFIRMED("Su usuario no se encuentra confirmado, confirme para inicar sesión.", "Username is unconfirmed", 103),
    USER_IS_NOT_CONFIRMED_QA("Su usuario no se encuentra confirmado, confirme para iniciar sesión.", "Username is unconfirmed", 103),
    USER_NAME_PASSWORD_INCORRECT("El usuario o la contraseña son incorrectos", "Username / password incorrect", 107),
    MALFORMED_JSON("El modelo de respuesta es incorrecto", "Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 1 path \$", 1000),
    SEND_SMS_LIMITED_EXCEEDED("Superó el límite de SMS de envío intente más tarde", "Send SMS limited exceeded", 108)

//    USER_EXIST_ERROR("El usuario ya existe","Username already exists", 104),
//    DEBUG_ERROR("ERROR", "Error Default", ),
//    ERROR_500("500","Error 500", 101),
//    DEFAULT_ERROR("ERROR", "Error Default", 101),
//    USER_IS_UNCONFIRMED("El usuario no ha sido confirmado", "Username is unconfirmed", 103),//register
}