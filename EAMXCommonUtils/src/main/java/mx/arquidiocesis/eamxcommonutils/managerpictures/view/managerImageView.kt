package mx.arquidiocesis.eamxcommonutils.managerpictures.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.fragment.app.Fragment
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.common.EAMXAction
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert
import mx.arquidiocesis.eamxcommonutils.customui.alert.UtilAlert.Companion.TYPE_ALERT_MULTI_SELECTION
import mx.arquidiocesis.eamxcommonutils.managerpictures.model.FileUtilsNew
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.CAMERA_CODE
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.FORMAT_BY_GET_GALLERY
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.GALLERY_CODE
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.SELECT_CAMERA
import mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel.ManagerUploadImageViewModel.Companion.SELECT_GALLERY
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.openCamera
import mx.arquidiocesis.eamxcommonutils.util.openGallery
import mx.arquidiocesis.eamxcommonutils.util.permission.UtilValidPermission
import mx.arquidiocesis.eamxcommonutils.util.toast
import java.io.FileNotFoundException
import java.io.InputStream

fun openWindowToChooseUploadImage(fragment : Fragment, typeImage : String = FORMAT_BY_GET_GALLERY) {
    val context = fragment.context
    context?.let {
        UtilAlert.Builder()
            .setTitle(it.getString(R.string.message_select_option))
            .setTypeAlert(TYPE_ALERT_MULTI_SELECTION)
            .setOptions(arrayListOf(SELECT_CAMERA, SELECT_GALLERY))
            .setListener { item ->
                when (item as String) {
                    SELECT_CAMERA -> {
                        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                                fragment,
                                arrayListOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                ManagerUploadImageViewModel.PERMISSION_CAMERA
                            )
                        ) {
                            fragment.openCamera(CAMERA_CODE)
                        }
                    }
                    SELECT_GALLERY -> {
                        if (UtilValidPermission().validListPermissionsAndBuildRequest(
                                fragment,
                                arrayListOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                ManagerUploadImageViewModel.PERMISSION_GALLERY
                            )
                        ) {
                            fragment.openGallery(GALLERY_CODE, typeImage = typeImage)
                        }
                    }
                }
            }
            .build()
            .show(fragment.childFragmentManager, fragment.javaClass.name)
    }
}

fun managerResultGallery(activity : Activity, uri : Uri) : Bitmap ?{
    var imageStream: InputStream? = null
    var bitmap: Bitmap? = null
    try {
        imageStream = activity.contentResolver.openInputStream(uri)
        bitmap = imageStream?.let {
            FileUtilsNew().rotateImage(
                BitmapFactory.decodeStream(it),
                FileUtilsNew().getPath(activity, uri)!!
            )
        }
    }catch ( ex : Exception){
        ex.printStackTrace()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return bitmap
}

fun Fragment.managerResultActivity(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    identifierFile : Int,
    sourceFile : String,
    viewModelImageManager : ManagerUploadImageViewModel,
    listener: (EAMXAction) -> Unit
) {
    when (resultCode) {
        Activity.RESULT_OK -> {
            when (requestCode) {
                CAMERA_CODE -> {
                    data?.extras?.get("data")?.let {
                        listener(EAMXAction.SHOW)
                        viewModelImageManager.buildFileByUploadImage(identifierFile, data, sourceFile)
                    }
                }
                GALLERY_CODE -> {
                    data?.data?.let { image ->
                        val bitMapImageLocal = managerResultGallery(requireActivity(), image)
                        bitMapImageLocal?.let {
                            listener(EAMXAction.SHOW)
                            viewModelImageManager.buildFileByUploadImage(
                                eamxcu_preferences.USER_COMMUNITY_ID,
                                data, ManagerUploadImageViewModel.SOURCE_COMMUNITY
                            )
                        } ?: kotlin.run {
                            listener(EAMXAction.HIDDEN)
                            toast(getString(R.string.message_error_upload_image))
                        }
                    }
                }
            }
        }
    }
}