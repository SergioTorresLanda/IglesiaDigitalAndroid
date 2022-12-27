#include <jni.h>
#include <string>
//-----------ENCRIPTADOS OLD-----------
extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_urlHost(JNIEnv *env, jobject) {
    std::string base_url = "https://api.iglesia-digital.com.mx/";
    return env->NewStringUTF(base_url.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_hostApi(JNIEnv *env, jobject) {
    std::string base_url = "https://api.iglesia-digital.com.mx/arquidiocesis/encuentro/v1/";
    return env->NewStringUTF(base_url.c_str());
}

// usuarios

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_hostUser(JNIEnv *env, jobject) {
    std::string base_url = "https://api.iglesia-digital.com.mx/arquidiocesis/gestion-usuarios/v1/";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_urlDonaciones(JNIEnv *env, jobject) {
    std::string base_url = "https://miofrenda.mx/";
    return env->NewStringUTF(base_url.c_str());
}

// encuentro

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_hostEncuentro(JNIEnv *env, jobject) {
    std::string base_url = "https://api.iglesia-digital.com.mx/arquidiocesis/encuentro/v1/";
    return env->NewStringUTF(base_url.c_str());
}

// redsocial
extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_hostRedSocial(JNIEnv *env, jobject) {
    std::string base_url = "https://os4jfceox2.execute-api.us-east-1.amazonaws.com/v1/";
    return env->NewStringUTF(base_url.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_ytk(JNIEnv *env, jobject) {
    std::string base_url = "AIzaSyAqcJAgBOE_cPCZYlsRbc7pL1Z0vCTayok";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appName(JNIEnv *env, jobject) {
    std::string base_url = "ARQUIDIOCESIS_DEV_INSTANCE";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appProjectId(JNIEnv *env, jobject) {
    std::string base_url = "encuentro-app-prod";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appStorageBucket(JNIEnv *env, jobject) {
    std::string base_url = "encuentro-app-prod.appspot.com";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appId(JNIEnv *env, jobject) {
    std::string base_url = "1:726779829818:android:f57a8c53e7097ee0725f03";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appIdKey(JNIEnv *env, jobject) {
    std::string base_url = "AIzaSyANZX1E3b3-A-j0YVTL0JdDU4sqV4NLTCI";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_kwv(JNIEnv *env, jobject) {
    std::string base_url = "2DcNxhMSgqwnyE48Hpw6CQ==";
    return env->NewStringUTF(base_url.c_str());
}
//Llaves
extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_chSalt(JNIEnv *env, jobject) {
    std::string base_url = "QWlGNHNhMTJTQWZ2bGhpV3U=";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_chSecretKey(JNIEnv *env, jobject) {
    std::string base_url = "616C1B47BE32D6246105DB2E12345678";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_chIv(JNIEnv *env, jobject) {
    std::string base_url = "MTczNGRmOWEzYzVmYmE4OA==";
    return env->NewStringUTF(base_url.c_str());
}