package mx.arquidiocesis.misiglesias.model

data class EditarIglesiasModel(
    var image: String,
    var description: String,
    var email: String,
    var phone: String,
    var stream: String,
    var bankAccount: String,
    var parson: String,
    var priest: List<PriestModel>,
    var horary:List<HoraryModelItem>,
    var attention: List<HoraryModelItem>,
    var masses: List<MasseModel>,
    var services: List<ServiceModel>)
