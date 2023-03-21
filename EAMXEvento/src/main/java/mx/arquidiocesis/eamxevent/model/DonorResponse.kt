package mx.arquidiocesis.eamxevent.model
import com.google.gson.annotations.SerializedName

data class DonorResponse(
    @SerializedName("FCBANCARIOS")
    val fCBANCARIOS: String,
    @SerializedName("FCCOMENTARIOS")
    val fCCOMENTARIOS: String,
    @SerializedName("FCCORREO")
    val fCCORREO: String,
    @SerializedName("FCFECALTA")
    val fCFECALTA: String,
    @SerializedName("FCNOMBRE")
    val fCNOMBRE: String,
    @SerializedName("FCTELEFONO")
    val fCTELEFONO: String,
    @SerializedName("FCTIPODONA")
    val fCTIPODONA: String,
    @SerializedName("FICOMEDORID")
    val fICOMEDORID: String,
    @SerializedName("FIDONANTEID")
    val fIDONANTEID: String,
    @SerializedName("FIUSERID")
    val fIUSERID: String
)