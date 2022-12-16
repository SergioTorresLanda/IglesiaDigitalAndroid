package mx.arquidiocesis.eamxcommonutils.managerpictures.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.managerpictures.repository.RepositoryUploadImage
import mx.arquidiocesis.eamxcommonutils.managerpictures.model.FileUtilsNew
import mx.arquidiocesis.eamxcommonutils.managerpictures.model.ImageRequestModel
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Base64 as Base64new

class ManagerUploadImageViewModel(
        private val context: Context,
        private val repository: RepositoryUploadImage
) : ViewModel() {

    companion object{
        const val SOURCE_PROFILE = "PROFILE"
        const val SOURCE_COMMUNITY = "LOCATION" //it´s name to update image of community
        const val SOURCE_CHURCH = "LOCATION" //it´s name to update image of church

        const val FORMAT_IMAGE = "png"
        const val FORMAT_BY_GET_GALLERY = "image/*"
        const val SELECT_CAMERA = "Cámara"
        const val SELECT_GALLERY = "Galería"

        const val CAMERA_CODE = 10001
        const val GALLERY_CODE = 10002
        const val PERMISSION_CAMERA = 10004
        const val PERMISSION_GALLERY = 10005
    }

    lateinit var sourceLocal: String
    val responseUploadImage = repository.responseUpdateImage
    val responseError = repository.errorResponse

    fun buildFileByUploadImage(identifier: Int, data: Intent?, sourceImage : String){
        try {
            var camera = false
            var url: String? = ""
            val imageSelected = if (data?.data == null) {
                val bitmap = data?.extras?.get("data") as Bitmap
                val tempUri: Uri = FileUtilsNew().getImageUri(context, bitmap)
                url = tempUri.path.toString()
                MediaStore.Images.Media.getBitmap(context.contentResolver, tempUri)
            } else {
                camera = true
                url = FileUtilsNew().getPath(context, data.data!!)
                MediaStore.Images.Media.getBitmap(context.contentResolver, data.data)
            }
            if (imageSelected != null) {
                val imageRotate = if (camera) FileUtilsNew().rotateImage(imageSelected, url!!) else null
                val bytes = ByteArrayOutputStream()

                if (camera) {
                    imageRotate?.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                } else {
                    imageSelected.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
                }

                val base64 = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Base64new.getEncoder().encodeToString(bytes.toByteArray())
                } else {
                    Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT)
                }
                updateImageProfile(identifier, base64, sourceImage)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updateImageProfile(identifier: Int, image: String, source: String) {
        this.sourceLocal = source
        val request = createRequestImage(identifier, source, image)
        GlobalScope.launch {
            repository.getUploadImageProfile(request)
        }
    }

    private fun createRequestImage(identifier : Int , source: String, image: String): ImageRequestModel {
        return ImageRequestModel(
                userIdentifier = identifier,
                source = source,
                filename = "${this.fileName(identifier, source)}.$FORMAT_IMAGE".lowercase(),
                content = image
        )
    }

    private fun fileName(identifier : Int , source: String) : String{
        return "${source}_$identifier".lowercase()
    }
}