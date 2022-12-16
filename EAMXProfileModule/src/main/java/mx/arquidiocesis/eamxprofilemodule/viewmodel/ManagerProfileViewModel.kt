package mx.arquidiocesis.eamxprofilemodule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxprofilemodule.model.ImageRequestModel
import mx.arquidiocesis.eamxprofilemodule.repository.RepositoryProfile
import java.io.FileNotFoundException
import java.io.IOException


class ManagerProfileViewModel(val repositoryProfile: RepositoryProfile) : ViewModel() {

    companion object {
        const val SOURCE_PROFILE = "PROFILE"
        const val SOURCE_CHURCH = "LOCATION" //it´s name to update image church
        const val FORMAT_IMAGE = "png"
        const val SELECT_CAMERA = "Cámara"
        const val SELECT_GALLERY = "Galería"
    }

    val responseUploadImage = repositoryProfile.responseUpdateImage
    val responseError = repositoryProfile.errorResponse

    fun buildFileByUploadImage(base64: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repositoryProfile.getUploadImage(createRequestImage(SOURCE_PROFILE, base64))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun createRequestImage(source: String, image: String): ImageRequestModel {
        return ImageRequestModel(
            userIdentifier = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int,
            source = source,
            filename = "${this.fileName(source)}.${FORMAT_IMAGE}".toLowerCase(),
            content = image
        )
    }

    private fun fileName(source: String): String {
        return "${source}_${
            eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int
        }".toLowerCase()
    }

    fun deleteUser(onRemoveUser: (Boolean) -> Unit) {
        viewModelScope.launch {
            repositoryProfile.deleteAccount { success, message ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (success) eamxcu_preferences.removeFile()
                    else responseError.value = message
                    onRemoveUser(success)
                }
            }

        }
    }
}