package mx.arquidiocesis.eamxprofilemodule.model.local
import mx.arquidiocesis.eamxprofilemodule.model.DataWithNameCode
class DataModelSharedPreferences2(
        time: String,
        val data: MutableList<DataWithNameCode>
):BaseLocal(time)