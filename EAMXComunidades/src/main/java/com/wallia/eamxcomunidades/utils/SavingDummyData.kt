package com.wallia.eamxcomunidades.utils

import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXProfile
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences

class SavingDummyData {
    fun savingDummyData(){
        eamxcu_preferences.apply {
            saveData(EAMXEnumUser.USER_EMAIL.name, "lrendon@linko.com")
            saveData(EAMXEnumUser.USER_ROLE.name, "Soltero")
            saveData(EAMXEnumUser.USER_PROFILE.name, EAMXProfile.Devoted.rol)
            saveData(EAMXEnumUser.USER_NAME.name, "Luis Enrique")
            saveData(EAMXEnumUser.USER_LAST_NAME.name, "Rendon")
            saveData(EAMXEnumUser.USER_MIDDLE_NAME.name, "Cortez")
            saveData(EAMXEnumUser.USER_PHONE.name, "+525516131531")
            saveData(EAMXEnumUser.USER_ID.name, 70)
            saveData(EAMXEnumUser.USER_NEED_COMPLETE_PROFILE.name, false)
        }

       /* eamxcu_preferences.apply {
            saveData(EAMXEnumUser.USER_EMAIL.name, "alejandrocontactoapoyo@yopmail.com")
            saveData(EAMXEnumUser.USER_ROLE.name, "Sacerdote")
            saveData(EAMXEnumUser.USER_NAME.name, "Alejandro")
            saveData(EAMXEnumUser.USER_LAST_NAME.name, "Rivera")
            saveData(EAMXEnumUser.USER_MIDDLE_NAME.name, "Moncada")
            saveData(EAMXEnumUser.USER_PHONE.name, "+525525228105")
            saveData(EAMXEnumUser.CHURCH.name, "635")
            saveData(EAMXEnumUser.USER_ID.name, 68)
            saveData(EAMXEnumUser.FIRST_TIME_USE_APP.name, false)
        }*/

        /*eamxcu_preferences.apply {
            saveData(EAMXEnumUser.USER_EMAIL.name, "slopezm@arquidiocesismexico.org")
            saveData(EAMXEnumUser.USER_ROLE.name, "Sacerdote")
            saveData(EAMXEnumUser.USER_NAME.name, "Salvador")
            saveData(EAMXEnumUser.USER_LAST_NAME.name, "López")
            saveData(EAMXEnumUser.USER_MIDDLE_NAME.name, "Mora")
            saveData(EAMXEnumUser.USER_PHONE.name, "+525525228105")
            //saveData(EAMXEnumUser.CHURCH.name, 246)
            saveData(EAMXEnumUser.USER_ID.name, 467)
            saveData(EAMXEnumUser.FIRST_TIME_USE_APP.name, false)
        }*/


        //246 iglesia de pruebas
    }
}