package mx.arquidiocesis.eamxredsocialmodule.Repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.retrofit.build.RetrofitApp
import mx.arquidiocesis.eamxcommonutils.retrofit.managercall.ManagerCall
import mx.arquidiocesis.eamxredsocialmodule.Retrofit.Validation.ValidationRedCodes
import mx.arquidiocesis.eamxredsocialmodule.Retrofit.ApiInterface
import mx.arquidiocesis.eamxredsocialmodule.config.WebConfig
import mx.arquidiocesis.eamxredsocialmodule.model.*
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Repository(val context: Context) : ManagerCall() {
    //val profileResponse
    val allPost = MutableLiveData<AllPostModel?>()
    val post = MutableLiveData<ResponsePostModel?>()
    val delete = MutableLiveData<ResponsePostModel?>()
    val react = MutableLiveData<ResponsePostModel?>()
    val followPost = MutableLiveData<ResponsePostModel?>()
    val follow = MutableLiveData<ResponseFollowModel?>()
    val followes = MutableLiveData<ResponseFollowModel?>()
    val search = MutableLiveData<ResponseSearchModel>()
    val comment = MutableLiveData<ResponseCommentModel>()
    val commentPost = MutableLiveData<ResponsePostModel>()
    val errorResponse = MutableLiveData<String>()
    val errorBottom = MutableLiveData<String>()
    val profile = MutableLiveData<ResponseProfileModel>()
    val multiProfile = MutableLiveData<ResponseMultiProfileModel>()
    val postMultimedia = MutableLiveData<List<ResponseMultimediaModel>>()
    val putMultimedia = MutableLiveData<Boolean>()

    private val retrofitInstance = RetrofitApp.Build<ApiInterface>()
        .setContext(context)
        .setEnvironment(true)
        .setClass(ApiInterface::class.java)

    suspend fun getAllPost(userId: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().getAllPost(userId.toString()).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    allPost.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getAllPost(userId: Int, page: String) {

        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().getAllPost(userId.toString(), page).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    allPost.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun setPost(body: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance.setHost(WebConfig.HOST_BAZ).builder().instance().setPost(
                    body
                ).await()
            },
            validation = ValidationRedCodes()

        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && response.data != null) {
                    post.value = response.data
                } else if (response.exception != null) {
                    errorBottom.value = "No se ha podido publicar, inténtalo más tarde"
                } else {
                    errorBottom.value = "No se ha podido publicar, inténtalo más tarde"
                }
            }
        }
    }

    suspend fun deletePost(idPost: Int, userId: Int) {

        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().deletePost(idPost, userId).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    delete.value = response.data!!
                } else {
                    errorResponse.value = "No se pudo eliminar la publicación, inténtalo más tarde"
                }
            }
        }
    }

    suspend fun updatePost(idPost: Int, body: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().updatePost(idPost, body).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    post.value = response.data!!
                } else {
                    errorBottom.value = "No se pudo actualizar la publicación, inténtalo más tarde"
                }
            }
        }
    }
    suspend fun reactDel(idPost: Int, userId: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().reactDel(idPost, userId).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    react.value = response.data!!
                } else {
                    errorResponse.value =
                        "No se pudo quitar la reaccion de la publicación, inténtalo más tarde"
                }
            }
        }
    }
    suspend fun reactPost(idPost: Int, body: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().reactPost(idPost, body).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    react.value = response.data!!
                } else {
                    errorResponse.value =
                        "No se pudo reaccionar a la publicación, inténtalo más tarde"
                }
            }
        }
    }

    suspend fun followPost(body: JsonObject) {
        managerCallApi(
            call = {

                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().followPost(body).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    followPost.value = response.data!!
                } else {

                    errorResponse.value = "No se pudo seguir, inténtalo más tarde"
                }
            }
        }
    }

    suspend fun unfollowPost(userId: Int,entityId: Int, entityType: Int,) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().unfollowPost(entityId,userId,entityType).await()

            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    followPost.value = response.data!!
                } else {
                    errorResponse.value = "No se pudo dejar de seguir, inténtalo más tarde"

                }
            }
        }
    }

    suspend fun getFollow(type: Int, userId: Int, page: String? = null) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().followGet(userId, type, page).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    if(type==1){
                        follow.value = response.data!!
                    }else{
                        followes.value = response.data!!
                    }
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getSearch(q: String, userId: Int) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().searchGet(q, userId).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    search.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getSearch(q: String, userId: Int, page: String) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().searchGet(q, userId, page).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    search.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }


    suspend fun getComment(idPost: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().getComment(idPost).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    comment.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getComment(idPost: Int, page: String) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().getComment(idPost, page).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    comment.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun setComment(body: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().commentPost(body).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    commentPost.value = response.data!!
                } else {
                    errorResponse.value = "No se pudo agregar el comentario, inténtalo más tarde"
                }
            }
        }
    }

    suspend fun deleteComment(idPost: Int, userId: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().deleteComment(idPost, userId).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    delete.value = response.data!!
                } else {
                    errorResponse.value = "No se pudo eliminar el comentario, inténtalo más tarde"
                }
            }
        }
    }
    suspend fun updateComment(idPost: Int, body: JsonObject) {
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().commentUpdate(idPost, body).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    commentPost.value = response.data!!
                } else {
                    errorResponse.value = "No se pudo agregar el comentario, inténtalo más tarde"
                }
            }
        }
    }

    suspend fun getProfile(idProfile: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().getProfile(idProfile).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    profile.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun getMultiProfile(idProfile: Int) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_BAZ).setUseToken(false)
                    .builder().instance().getMultiProfile(idProfile).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess) {
                    multiProfile.value = response.data!!
                } else {
                    errorResponse.value = response.exception?.message
                }
            }
        }
    }

    suspend fun postMultimedia(body: JsonObject) {
        managerCallApi(
            context = context,
            call = {
                retrofitInstance
                    .setHost(WebConfig.HOST_API).setUseToken(true)
                    .builder().instance().uploadMultimedia(body).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                if (response.sucess && response.data != null) {
                    postMultimedia.value = response.data!!
                } else {
                    errorResponse.value = "Error al subir archivos , inténtalo más tarde"
                }
            }
        }
    }

    suspend fun putMultimedia(url: String, img: ByteArray) {
        val link = url.split("posts/")
        val url = link[0]
        val data = link[1]
        managerCallApi(
            call = {
                retrofitInstance
                    .setHost(url).setUseToken(false)
                    .builder().instance().putMutimedia(
                        data, RequestBody.create(
                            "application/octet".toMediaTypeOrNull(), img
                        )
                    ).await()
            },
            validation = ValidationRedCodes()
        ).let { response ->
            GlobalScope.launch(Dispatchers.Main) {
                when {
                    response.sucess -> {
                        putMultimedia.value = true
                    }
                    response.exception != null -> {
                        putMultimedia.value = false
                    }
                    else -> {
                        putMultimedia.value = false
                    }
                }
            }
        }
    }

    fun getFileMetaData(uri: Uri): EAMXMultimedia? {
        val fileMetaData = EAMXMultimedia()
        return if ("file".equals(uri.scheme, ignoreCase = true)) {
            val file = File(uri.path)
            fileMetaData.displayName = file.name
            fileMetaData.size = file.length()
            fileMetaData.path = file.path
            fileMetaData
        } else {
            if (context == null) return null
            val contentResolver: ContentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            fileMetaData.mimeType = contentResolver.getType(uri)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                    fileMetaData.displayName =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    // fileMetaData.displayName =getHora()
                    if (!cursor.isNull(sizeIndex)) fileMetaData.size =
                        cursor.getLong(sizeIndex) else fileMetaData.size = -1
                    try {
                        fileMetaData.path = cursor.getString(cursor.getColumnIndexOrThrow("_data"))
                    } catch (e: Exception) {
                        // DO NOTHING, _data does not exist
                    }
                    return fileMetaData
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            null
        }
    }

    fun getHora(): String {
        val sdf = SimpleDateFormat("hh:mm:ss")
        return sdf.format(Date()) + "jpg"
    }
}