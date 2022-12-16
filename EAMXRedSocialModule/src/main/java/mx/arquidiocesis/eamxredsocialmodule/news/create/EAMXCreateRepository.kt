package mx.arquidiocesis.eamxredsocialmodule.news.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import retrofit2.Call
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXInitServices
import mx.arquidiocesis.eamxcommonutils.api.core.EAMXValidResponse
import mx.arquidiocesis.eamxcommonutils.api.core.response.EAMXGenericResponse
import mx.arquidiocesis.eamxcommonutils.application.AppMyConstants
import mx.arquidiocesis.eamxcommonutils.application.ConstansApp
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostRequest
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXCreatePostResponse
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

object EAMXCreateRepository {

    private val storage = getInstance()
    private var storageRef = storage.reference
    private var imagesRef: StorageReference? = storageRef.child("android/100/")

    fun storeImages(file: File, name: String, cb: (success: Boolean, uri: Uri?) -> Unit) {
        imagesRef
            ?.child(System.currentTimeMillis().toString() + "_" + name)
            ?.putStream(FileInputStream(file))
            ?.addOnFailureListener {
                it.printStackTrace()
                cb(false, null)
            }?.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    cb(true, it)
                }
            }
    }

    fun callServiceCreatePost(requestModel: EAMXCreatePostRequest,
        observer: Observer<EAMXGenericResponse<EAMXCreatePostResponse, String, EAMXCreatePostRequest>>
    ) {
        val initService: EAMXInitServices<EAMXCreatePostRequest, Call<EAMXCreatePostResponse>> =
            EAMXInitServices(AppMyConstants.BASE_NEWS_URL)

        EAMXValidResponse(observer, requestModel, EAMXCreatePostResponse::class).validationMethod(
            initService.postExecuteService(requestModel, AppMyConstants.PUBLICATIONS_MAKE_END_POINT)
        )
    }


    private fun getInstance(): FirebaseStorage {
        return try {
            val instance =
                FirebaseApp.getInstance(ConstansApp.appName())
            Firebase.storage(instance)
        } catch (e: Exception) {
            // Only return null inside Module because the instance its created in EAMXSplashActivity
            Firebase.storage
        }
    }

}