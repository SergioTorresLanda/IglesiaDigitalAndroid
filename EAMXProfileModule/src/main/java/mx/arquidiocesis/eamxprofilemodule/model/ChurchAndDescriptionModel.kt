package mx.arquidiocesis.eamxprofilemodule.model
import mx.arquidiocesis.eamxprofilemodule.model.update.base.ActivityChurchModel

data class ChurchAndDescriptionModel(
    var church : ChurchModel,
    var activity : ActivityChurchModel
)