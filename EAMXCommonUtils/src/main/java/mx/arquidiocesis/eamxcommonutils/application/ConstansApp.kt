package mx.arquidiocesis.eamxcommonutils.application

object ConstansApp {

    init {
        System.loadLibrary("eamx-native")
    }

    external fun hostApi(): String
    external fun urlHost(): String

    external fun hostUser(): String
    external fun hostEncuentro(): String
    external fun hostRedSocial(): String
    external fun urlDonaciones(): String

    external fun ytk(): String

    external fun appName(): String
    external fun appProjectId(): String
    external fun appStorageBucket(): String
    external fun appId(): String
    external fun appIdKey(): String
    external fun kwv(): String
    external fun chSalt(): String
    external fun chSecretKey(): String
    external fun chIv(): String
}