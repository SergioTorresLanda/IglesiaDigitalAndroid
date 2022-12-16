package mx.arquidiocesis.eamxprofilemodule.repository

import com.google.gson.Gson
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.common.ModuleAdminEnabled
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.log
import mx.arquidiocesis.eamxprofilemodule.model.update.base.BaseUpdate
import mx.arquidiocesis.eamxprofilemodule.model.userdetail.*
import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT = "yyyy-MM-dd"

class LocalRepository {

    fun isDataNeedUpdate(dateSearch : String?) : Boolean{
        if (dateSearch == null) {
            return true
        }
        val format = SimpleDateFormat(DATE_FORMAT)
        val strDate = format.parse(dateSearch)
        val calendar = Calendar.getInstance()
        val dayCurrent = calendar.time.date
        val daySave = strDate.date
        return daySave - dayCurrent != 0
    }

    fun getData(nameCollection : String) : String {
        val dataLocal = eamxcu_preferences.getData(nameCollection, EAMXTypeObject.STRING_OBJECT).toString()
        return if(dataLocal.isEmpty()){
            "{}"
        } else{
            dataLocal
        }
    }

    fun getDateCurrent() : String{
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(DATE_FORMAT)
        return formatter.format(date)
    }


    fun <T> saveData(collection: String, data: T): Boolean {
        return eamxcu_preferences.saveData(collection, Gson().toJson(data).toString())
    }

    fun saveDataUser(user: UserResponse?) {
        user?.let {
            eamxcu_preferences.apply {
                saveData(EAMXEnumUser.USER_NAME.name, user.data.User.name)
                saveData(EAMXEnumUser.USER_LAST_NAME.name, user.data.User.first_surname)
                saveData(EAMXEnumUser.USER_MIDDLE_NAME.name, user.data.User.second_surname ?: "")
                saveData(EAMXEnumUser.USER_PHONE.name, user.data.User.phone_number)
                user.data.User.image?.let { it1 -> saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER.name, it1.replace("\"","")) }
                saveData(EAMXEnumUser.URL_PICTURE_PROFILE_USER_LOCAL.name, "picture_profile_encuentro_${Calendar.getInstance().timeInMillis}")
                saveData(EAMXEnumUser.USER_PROFILE.name, user.data.User.profile ?: "")
                saveData(EAMXEnumUser.USER_COMMUNITY_ID.name, user.data.User.community?.id ?: 0)

                saveChurch(user)

                user.data.User.location_modules?.let {
                    if(it.isNotEmpty()) {
                        saveOnlyPermissions(it)
                        saveLocations(it)
                    }
                }?: kotlin.run {
                    cleanPermissions()
                    cleanLocations()
                }

                saveData(USER, Gson().toJson(user).toString())
                val userHasImage = user.data.User.image?.let {
                    it.isNotEmpty() && it != "s/n"
                } ?: false

                "Profile user HasImage -> $userHasImage".log()
                if (userHasImage) {
                    "Profile user save image -> ${user.data.User.image}".log()
                    "Profile user save local flag image -> ${user.data.User.image}".log()
                    saveData(EAMXEnumUser.URL_PHOTO_UPDATE.name, true)
                }
            }

            "Profile user -> finish".log()
        }
    }

    private fun saveChurch(user: UserResponse?) {
        user?.let {
            when(it.data.User.profile){
                EAMXProfile.DevotedAdmin.rol -> {
                    it.data.User.location_modules?.let { modules ->
                        modules.firstOrNull()?.id?.let { church ->
                            "Profile user -> Save church module $church".log()
                            eamxcu_preferences.saveData(EAMXEnumUser.CHURCH.name, church.toString())
                        }
                    }
                }
                EAMXProfile.CommunityAdmin.rol -> {
                    it.data.User.location_modules?.let { modules ->
                        modules.firstOrNull()?.id?.let { church ->
                            "Profile user -> Save church module $church".log()
                            eamxcu_preferences.saveData(EAMXEnumUser.CHURCH.name, church.toString())
                            eamxcu_preferences.saveData(EAMXEnumUser.USER_COMMUNITY_ID.name,church)
                        }
                    }
                }
                EAMXProfile.CommunityResponsible.rol -> {
                            it.data.User.community?.let { community ->
                                "Profile user -> Save community id ${community.id}".log()
                                eamxcu_preferences.saveData(
                                    EAMXEnumUser.CHURCH.name,
                                    community.id?.toString() ?: "0"
                                )
                                eamxcu_preferences.saveData(
                                    EAMXEnumUser.USER_COMMUNITY_ID.name,
                                    community.id ?: 0
                                )
                            }
                }
                else -> {
                    it.data.User.location_id?.let { church ->
                        "Profile user -> Save church location id $church".log()
                        eamxcu_preferences.saveData(EAMXEnumUser.CHURCH.name, church.toString())
                    }
                }
            }

        }
    }

    private fun saveOnlyPermissions(moduleAdminList: List<ModuleAdminEnabled>){
        val moduleAdminEnabled = moduleAdminList.first()
        val permissionType = moduleAdminEnabled.type
        val permissionChurch = moduleAdminEnabled.modules.firstOrNull{ item -> item == "LOCATION_INFORMATION"} != null
        val permissionServices = moduleAdminEnabled.modules.firstOrNull{ item -> item == "SERVICES"} != null
        val permissionSocialNetwork = moduleAdminEnabled.modules.firstOrNull{ item -> item == "SOCIAL_NETWORKS"} != null
        val permissionDonation = moduleAdminEnabled.modules.firstOrNull{ item -> item == "DONATIONS"} != null
        val permissionSos = moduleAdminEnabled.modules.firstOrNull{ item -> item == "SOS"} != null
        val permissionAdminManagement = moduleAdminEnabled.modules.firstOrNull{ item -> item == "APPOINT_ADMINISTRATOR"} != null

        eamxcu_preferences.apply {
            saveData(EAMXEnumUser.USER_PERMISSION_CHURCH.name, permissionChurch)
            saveData(EAMXEnumUser.USER_PERMISSION_SERVICES.name, permissionServices)
            saveData(EAMXEnumUser.USER_PERMISSION_SOCIAL_NETWORK.name, permissionSocialNetwork)
            saveData(EAMXEnumUser.USER_PERMISSION_DONATION.name, permissionDonation)
            saveData(EAMXEnumUser.USER_PERMISSION_SOS.name, permissionSos)
            saveData(EAMXEnumUser.USER_PERMISSION_ADMIN_MANAGEMENT.name, permissionAdminManagement)
            saveData(EAMXEnumUser.USER_PERMISSION_TYPE.name, permissionType)
        }
    }


    private fun cleanPermissions(){
        eamxcu_preferences.apply {
            saveData(EAMXEnumUser.USER_PERMISSION_CHURCH.name, false)
            saveData(EAMXEnumUser.USER_PERMISSION_SERVICES.name, false)
            saveData(EAMXEnumUser.USER_PERMISSION_SOCIAL_NETWORK.name, false)
            saveData(EAMXEnumUser.USER_PERMISSION_DONATION.name, false)
            saveData(EAMXEnumUser.USER_PERMISSION_SOS.name, false)
            saveData(EAMXEnumUser.USER_PERMISSION_ADMIN_MANAGEMENT.name, false)
            saveData(EAMXEnumUser.USER_PERMISSION_TYPE.name, "")
        }
    }

    private fun saveLocations(moduleAdminList: List<ModuleAdminEnabled>){
            saveData(EAMXEnumUser.USER_CHURCH_ALLOW_EDIT.name, moduleAdminList)
    }

    private fun cleanLocations(){
            saveData(EAMXEnumUser.USER_CHURCH_ALLOW_EDIT.name, listOf<ModuleAdminEnabled>())
    }

    fun saveDataUser(user: BaseUpdate) {
        eamxcu_preferences.apply {
            saveData(EAMXEnumUser.USER_NAME.name, user.name)
            saveData(EAMXEnumUser.USER_LAST_NAME.name, user.first_surname)
            saveData(EAMXEnumUser.USER_MIDDLE_NAME.name, user.second_surname)
            saveData(EAMXEnumUser.USER_PHONE.name, user.phone_number)
            saveData(USER, Gson().toJson(user).toString())
        }
    }
}