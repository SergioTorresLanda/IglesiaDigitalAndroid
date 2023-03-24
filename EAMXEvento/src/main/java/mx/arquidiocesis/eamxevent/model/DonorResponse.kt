package mx.arquidiocesis.eamxevent.model
import com.google.gson.annotations.SerializedName

data class DonorResponse(
    @SerializedName("FCBANCARIOS")
    val fCBANCARIOS: String? = null,
    @SerializedName("FCCOMENTARIOS")
    val fCCOMENTARIOS: String? = null,
    @SerializedName("FCCORREO")
    val fCCORREO: String? = null,
    @SerializedName("FCFECALTA")
    val fCFECALTA: String? = null,
    @SerializedName("FCNOMBRE")
    val fCNOMBRE: String? = null,
    @SerializedName("FCTELEFONO")
    val fCTELEFONO: String? = null,
    @SerializedName("FCTIPODONA")
    val fCTIPODONA: String? = null,
    @SerializedName("FICOMEDORID")
    val fICOMEDORID: String? = null,
    @SerializedName("FIDONANTEID")
    val fIDONANTEID: String? = null,
    @SerializedName("FIUSERID")
    val fIUSERID: String? = null
)