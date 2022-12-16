package com.wallia.eamxcomunidades.viewmodel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.wallia.eamxcomunidades.model.CommunityDetailResponse
import com.wallia.eamxcomunidades.model.CompleteCommunityRequest
import com.wallia.eamxcomunidades.model.CreateCommunityRequest
import com.wallia.eamxcomunidades.model.EditCommunityRequest
import com.wallia.eamxcomunidades.repository.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

const val COMUNITYID: String = "ComunityId"
const val PRINCIPAL: String = "isPrincipal"
const val STAR: String = "star"
const val SEARCH: String = "search"

class ComunidadesViewModel(private val repository: Repository) : ViewModel() {


    var errorResponse = repository.errorResponse
    val genericResponse = repository.genericResponse
    val userDetailResponse = repository.userDetailResponse
    val communityModuleResponse = repository.communityModuleResponse
    val completeCommunityResponse = repository.completeCommunityResponse
    val mainCommunityResponse = repository.mainCommunityResponse
    val getCommunityTypesResponse = repository.getCommunityTypesResponse
    val getAllCommunitiesResponse = repository.getAllCommunitiesResponse
    val getCommunitiesByLocationsResponse = repository.getCommunitiesByLocationsResponse
    val getCommunitiesByNameResponse = repository.getCommunitiesByNameResponse
    val getCommunitiesSearchByNameResponse = repository.getCommunitiesByNameResponse
    val getCommunityDetailResponse = repository.getCommunityDetailResponse
    val getActivitiesResponse = repository.getActivitiesResponse
    val getPartnerCommunitiesResponse = repository.getPartnerCommunitiesResponse
    val reviewsResponse = repository.reviewsResponse
    val reviewResponse = repository.reviewResponse
    val locationResponse = repository.locationResponse

    private var communityId: Int = 0

    init {
        communityId = eamxcu_preferences.USER_COMMUNITY_ID
    }

    fun setCommunityID() {
        communityId = eamxcu_preferences.USER_COMMUNITY_ID
    }
    fun getUserDetail(id: Int) {
        GlobalScope.launch {
            repository.getUserDetail(id)
        }
    }

    fun getCommunityModules(location_id: Int) {
        GlobalScope.launch {
            repository.getCommunityModules(location_id)
        }
    }

    fun getMainCommunity() {
        GlobalScope.launch {
            repository.getMainCommunity("COMMUNITY")
        }
    }

    fun postFavoriteCommunity(idIglecia: Int, isPrincipal: Boolean, communityDetail : CommunityDetailResponse) {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("location_id", idIglecia)
        jsonObject.addProperty("is_principal", isPrincipal)
        GlobalScope.launch {
            repository.postFavoriteCommunity(jsonObject, communityDetail, isPrincipal)
        }
    }

    fun deleteCommunity(location_id: Int) {
        GlobalScope.launch {
            repository.deleteCommunity(location_id)
        }
    }

    fun getCommunityTypes() {
        GlobalScope.launch {
            repository.getCommunityTypes()
        }
    }

    fun getAllCommunities(type_location: String) {
        GlobalScope.launch {
            repository.getAllCommunities(type_location)
        }
    }

    fun getCommunitiesByLocations(latitude: String, longitude: String) {
        GlobalScope.launch {
            repository.getCommunitiesByLocations("COMMUNITY", latitude, longitude)
        }
    }

    fun getCommunitiesByName(name: String) {
        GlobalScope.launch {
            repository.getCommunitiesByName("COMMUNITY", name)
        }
    }

    fun getCommunitiesSearchByName(name: String) {
        GlobalScope.launch {
            repository.getCommunitiesSearchByName(name)
        }
    }

    fun getCommunityDetail(location_id: Int) {
        GlobalScope.launch {
            repository.getCommunityDetail(location_id)
        }
    }

    fun getCommunityDetail() {
        GlobalScope.launch {
            repository.getCommunityDetail(communityId)
        }
    }

    fun getActivities() {
        GlobalScope.launch {
            repository.getActivities(communityId)
        }
    }

    fun getActivities(idLocation: Int) {
        GlobalScope.launch {
            repository.getActivities(idLocation)
        }
    }

    fun getPartnerCommunities() {
        GlobalScope.launch {
            repository.getPartnerCommunities(communityId)
        }
    }

    fun createCommunity(createCommunityRequest: CreateCommunityRequest) {
        GlobalScope.launch {
            repository.createCommunity(createCommunityRequest)
        }
    }

    fun editCommunity(editCommunityRequest: EditCommunityRequest) {
        GlobalScope.launch {
            repository.editCommunity(editCommunityRequest)
        }
    }

    fun completeCommunity(completeCommunityRequest: CompleteCommunityRequest) {
        GlobalScope.launch {
            repository.completeCommunity(communityId , completeCommunityRequest)
        }
    }
    fun completeCommunity(id: Int,completeCommunityRequest: CompleteCommunityRequest) {
        GlobalScope.launch {
            repository.completeCommunity(id , completeCommunityRequest)
        }
    }

    fun getLocation() {
        repository.getLocation()
    }
}