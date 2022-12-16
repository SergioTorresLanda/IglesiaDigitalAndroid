package mx.arquidiocesis.eamxprofilemodule.model.local
import mx.arquidiocesis.eamxprofilemodule.model.DataWithName
class DataModelSharedPreferences(
        time: String,
        val data: MutableList<DataWithName>
):BaseLocal(time)