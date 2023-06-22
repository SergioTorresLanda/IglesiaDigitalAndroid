package mx.arquidiocesis.eamxcommonutils.util

import android.content.Intent
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.YouTubeStandalonePlayer
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.FORMAT_BY_GET_GALLERY

fun Fragment.openCamera(code : Int){
    this.startActivityForResult(Intent(ACTION_IMAGE_CAPTURE), code)
}

fun Fragment.openGallery(code : Int, typeImage: String = FORMAT_BY_GET_GALLERY){
    this.startActivityForResult(
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
            type = typeImage
        },code
    )
}

fun Fragment.openYoutubeApi(urlYouTube : String){
    startActivity(
        YouTubeStandalonePlayer.createVideoIntent(
            requireActivity(),
            ConstansApp.ytk(), urlYouTube.extraIdUrlVideoYoutube()
        )
    )
}

fun Fragment.userAllowAccessAsAdmin(module : String) : Boolean{
    "userAllowAccessWithAdmin module -> $module".log()
    val userCanAccessModule = userCanAccessModule(module)
    "userAllowAccessWithAdmin userCanAccessModule -> $userCanAccessModule".log()
    return if(userCanAccessModule){
        userHasPermissionAsAdmin(module)
    }else{
        false
    }
}

fun Fragment.isChurchAvailable(module : String) : Boolean{
    "userAllowAccessWithAdmin module -> $module".log()
    val userCanAccessModule = userCanAccessModule(module)
    "userAllowAccessWithAdmin userCanAccessModule -> $userCanAccessModule".log()
    return if(userCanAccessModule){
        userHasPermissionAsAdmin(module)
    }else{
        false
    }
}

private fun userHasPermissionAsAdmin(module : String) : Boolean {
    return when(module){
        EAMXEnumUser.USER_PERMISSION_CHURCH.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_CHURCH.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        EAMXEnumUser.USER_PERMISSION_ADMIN_MANAGEMENT.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_ADMIN_MANAGEMENT.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        EAMXEnumUser.USER_PERMISSION_DONATION.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_DONATION.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        EAMXEnumUser.USER_PERMISSION_SOCIAL_NETWORK.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_SOCIAL_NETWORK.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        EAMXEnumUser.USER_PERMISSION_SERVICES.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_SERVICES.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        EAMXEnumUser.USER_PERMISSION_SOS.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_SOS.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        else -> false
    }
}

private fun userCanAccessModule(module : String) : Boolean {

    val profileUser = eamxcu_preferences.getData(
        EAMXEnumUser.USER_PROFILE.name,
        EAMXTypeObject.STRING_OBJECT
    ) as String

    "userAllowAccessWithAdmin profileUser -> $profileUser".log()
    return when(module){
        EAMXEnumUser.USER_PERMISSION_CHURCH.name -> {
            return when (profileUser) {
                EAMXProfile.Priest.rol,
                EAMXProfile.PriestAdmin.rol,
                EAMXProfile.DevotedAdmin.rol,
                EAMXProfile.DeanPriest.rol,
                EAMXProfile.VicariaClero.rol -> true
                else -> false
            }
        }
        EAMXEnumUser.USER_PERMISSION_ADMIN_MANAGEMENT.name -> {
             return when (profileUser) {
                EAMXProfile.DeanPriest.rol,
                EAMXProfile.PriestAdmin.rol,
                EAMXProfile.CommunityResponsible.rol,
                EAMXProfile.CommunityAdmin.rol,
                EAMXProfile.DevotedAdmin.rol -> true
                else -> false
            }
        }
        EAMXEnumUser.USER_PERMISSION_DONATION.name -> {
            eamxcu_preferences.getData(EAMXEnumUser.USER_PERMISSION_DONATION.name, EAMXTypeObject.BOOLEAN_OBJECT) as Boolean
        }
        EAMXEnumUser.USER_PERMISSION_SOCIAL_NETWORK.name -> {
            return when (profileUser) {
                EAMXProfile.Devoted.rol,
                EAMXProfile.DevotedAdmin.rol,
                EAMXProfile.Priest.rol,
                EAMXProfile.PriestAdmin.rol,
                EAMXProfile.DeanPriest.rol,
                EAMXProfile.CommunityMember.rol,
                EAMXProfile.CommunityResponsible.rol,
                EAMXProfile.CommunityAdmin.rol -> true
                else -> false
            }
        }
        EAMXEnumUser.USER_PERMISSION_SERVICES.name -> {
            return when (profileUser) {
                EAMXProfile.DevotedAdmin.rol,
                EAMXProfile.Priest.rol,
                EAMXProfile.PriestAdmin.rol,
                EAMXProfile.CommunityAdmin.rol,
                EAMXProfile.CommunityResponsible.rol,
                EAMXProfile.DeanPriest.rol -> true
                else -> false
            }
        }
        EAMXEnumUser.USER_PERMISSION_SOS.name -> {
            return true //profileUser != EAMXProfile.Devoted.rol
        }
        else -> false
    }
}


