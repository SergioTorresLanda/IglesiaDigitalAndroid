package mx.arquidiocesis.eamxredsocialmodule.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxredsocialmodule.Repository.Repository
import mx.arquidiocesis.eamxredsocialmodule.news.create.model.EAMXMultimedia


class RedSocialViewModel(private val repository: Repository) : ViewModel() {
    val responseAllPost = repository.allPost
    val responsePost = repository.post
    val responseDelete = repository.delete
    val responseReact = repository.react
    val responseComment = repository.comment
    val responseCommentPost = repository.commentPost
    val responseProfile = repository.profile
    val responseMultiProfile = repository.multiProfile
    val responseMultimedia = repository.postMultimedia
    val responsePutMulti = repository.putMultimedia
    val reponseFollowPost = repository.followPost
    val reponseFollow = repository.follow
    val reponseFollowes = repository.followes
    val reponseSearch = repository.search

    val error = repository.errorResponse
    val errorBottom = repository.errorBottom

    var profileId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID_REDSOCIAL.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int
    val userId = eamxcu_preferences.getData(
        EAMXEnumUser.USER_ID.name,
        EAMXTypeObject.INT_OBJECT
    ) as Int

    fun setProfileId() {
        profileId = eamxcu_preferences.getData(
            EAMXEnumUser.USER_ID_REDSOCIAL.name,
            EAMXTypeObject.INT_OBJECT
        ) as Int
    }
    fun getSuper():Boolean {
        return userId==1
    }
    fun setPost(
        idUser: Int, scope: Int, content: String,
        multimediaList: List<EAMXMultimedia>
    ) {

        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("userId", profileId)
        jsonObject.addProperty("content", content)
        var multimediaArray = JsonArray()
        multimediaList.forEach {
            var multimedia: JsonObject = JsonObject()
            //   multimedia.addProperty("id", serviceId)
            multimedia.addProperty("format", it.mimeType)
            multimedia.addProperty("fileKey", it.url)
            multimediaArray.add(multimedia)
        }
        jsonObject.add("multimedia", multimediaArray)
        jsonObject.addProperty("groupId", idUser)
        jsonObject.addProperty("scope", scope)
        jsonObject = getAs(scope, jsonObject)
        GlobalScope.launch {
            repository.setPost(jsonObject)
        }
    }

    fun updatePost(
        content: String, idPost: Int, multimediaDelList: List<EAMXMultimedia>,
        multimediaList: List<EAMXMultimedia>
    ) {
        var jsonObject: JsonObject = JsonObject()

        jsonObject.addProperty("userId", profileId)

        jsonObject.addProperty("statusId", 1)
        jsonObject.addProperty("content", content)
        var multimediaDelArray = JsonArray()
        multimediaDelList.forEach {
            multimediaDelArray.add(it.id!!.toInt())
        }
        jsonObject.add("multimediaRemoved", multimediaDelArray)
        var multimediaArray = JsonArray()
        multimediaList.forEach {
            var multimedia: JsonObject = JsonObject()
            //   multimedia.addProperty("id", serviceId)
            multimedia.addProperty("format", it.mimeType)
            multimedia.addProperty("fileKey", it.url)
            multimediaArray.add(multimedia)
        }
        jsonObject.add("multimedia", multimediaArray)
        GlobalScope.launch {
            repository.updatePost(idPost, jsonObject)
        }
    }

    fun requestAllpost() {
        GlobalScope.launch {
            repository.getAllPost(profileId)
        }
    }

    fun requestAllpost(page: String) {
        GlobalScope.launch {
            repository.getAllPost(profileId, page)
        }
    }

    fun deletePost(idPost: Int) {
        GlobalScope.launch {
            repository.deletePost(idPost, profileId)
        }
    }
    fun deleteComment(idPost: Int) {
        GlobalScope.launch {
            repository.deleteComment(idPost, profileId)
        }
    }
    fun reactDel(idPost: Int) {
        GlobalScope.launch {
            repository.reactDel(idPost, profileId)
        }
    }


    fun reactPost(idPost: Int) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("userId", profileId)
        jsonObject.addProperty("reactionId", 1)

        GlobalScope.launch {
            repository.reactPost(idPost, jsonObject)
        }
    }

    fun followPost(entityId: Int, entityType: Int, follower: Boolean) {
        val jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("userId", profileId)
        jsonObject.addProperty("entityId", entityId)
        jsonObject.addProperty("entityType", entityType)

        GlobalScope.launch {
            if(follower){
                repository.unfollowPost(profileId,entityId,entityType)
            }else{
            repository.followPost(jsonObject)}
        }
    }

    fun getFollow(type: Int, page: String? = null) {
        GlobalScope.launch {
            repository.getFollow(type, profileId, page)
        }
    }

    fun getSearch(q: String) {
        GlobalScope.launch {
            repository.getSearch(q, profileId)
        }
    }

    fun getSearch(q: String, page: String) {
        GlobalScope.launch {
            repository.getSearch(q, profileId, page)
        }
    }

    fun getComment(idPost: Int) {
        GlobalScope.launch {
            repository.getComment(idPost)
        }
    }

    fun getComment(idPost: Int, page: String) {
        GlobalScope.launch {
            repository.getComment(idPost, page)
        }
    }

    fun setComment(idPost: Int, idComent: Int? = null, comment: String, scope: Int, idUser: Int) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("postId", idPost)
        jsonObject.addProperty("commentId", idComent)
        jsonObject.addProperty("userId", profileId)
        jsonObject.addProperty("content", comment)
        jsonObject.addProperty("scope", scope)
        jsonObject.addProperty("groupId", idUser)
        jsonObject = getAs(scope, jsonObject)
        GlobalScope.launch {
            repository.setComment(jsonObject)
        }
    }

    fun updateComment(idCommen: Int, comment: String) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("userId", profileId)
        jsonObject.addProperty("content", comment)
        GlobalScope.launch {
            repository.updateComment(idCommen, jsonObject)
        }
    }

    fun getProfile() {
        GlobalScope.launch {
            repository.getProfile(userId)
        }
    }

    fun getMultiProfile() {
        GlobalScope.launch {
            repository.getMultiProfile(profileId)
        }
    }

    fun setMultimedia(array: ArrayList<String>, id: Int) {
        var jsonObject = JsonObject()

        if (id != 0) {
            jsonObject.addProperty("upload_type", "AS_MANAGER")
            jsonObject.addProperty("resource_id", id)
        } else {
            jsonObject.addProperty("upload_type", "PERSONAL")
            jsonObject.addProperty("resource_id", profileId)
        }
        var multimediaArray = JsonArray()
        array.forEach {
            multimediaArray.add(it)
        }
        jsonObject.add("content", multimediaArray)
        GlobalScope.launch {
            repository.postMultimedia(jsonObject)
        }
    }

    fun putMultimedia(url: String, img: ByteArray) {
        // var jsonObject: JsonObject = JsonObject()
        //jsonObject.add("postId", img)

        GlobalScope.launch {
            repository.putMultimedia(url, img)
        }
    }

    fun getAs(scope: Int, jsonObject: JsonObject): JsonObject {
        if (scope > 1) {
            jsonObject.addProperty("as", 2)
        } else {
            jsonObject.addProperty("as", 1)
        }
        return jsonObject
    }

    fun getFileMetaData(uri: Uri): EAMXMultimedia? {
        return repository.getFileMetaData(uri)
    }

    fun getDateMane(): String {
        return repository.getHora()
    }
}