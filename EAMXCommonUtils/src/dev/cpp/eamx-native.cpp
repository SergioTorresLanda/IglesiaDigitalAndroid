#include <jni.h>
#include <string>
//-----------ENCRIPTADOS OLD-----------
extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_urlHost(JNIEnv *env, jobject) {
    std::string base_url = "https://api-develop.arquidiocesis.mx/";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_hostApi(JNIEnv *env, jobject) {
    std::string base_url = "https://api-develop.arquidiocesis.mx/";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_hostUser(JNIEnv *env, jobject) {
    std::string base_url = "https://auth.arquidiocesis.mx/";
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
    std::string base_url = "arquidiocesis-dev";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appStorageBucket(JNIEnv *env, jobject) {
    std::string base_url = "arquidiocesis-dev.appspot.com";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appId(JNIEnv *env, jobject) {
    std::string base_url = "1:13723654053:android:26eac9deefc1a2ac6f3a4c";
    return env->NewStringUTF(base_url.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_mx_arquidiocesis_eamxcommonutils_application_ConstansApp_appIdKey(JNIEnv *env, jobject) {
    std::string base_url = "AIzaSyDco0lrvfh9us-vkiUVnRqm1u8kPBoATTg";
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