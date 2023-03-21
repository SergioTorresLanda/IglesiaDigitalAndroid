package mx.arquidiocesis.eamxevent.model
import com.google.gson.annotations.SerializedName

data class VolunteerResponse(
    @SerializedName("FCCORREO")
    val fCCORREO: String? = null,
    @SerializedName("FCDIRECCION")
    val fCDIRECCION: String? = null,
    @SerializedName("FCFECALTA")
    val fCFECALTA: String? = null,
    @SerializedName("FCMULTIUSER")
    val fCMULTIUSER: ArrayList<String>? = ArrayList(),
    @SerializedName("FCNOMBRECOM")
    val fCNOMBRECOM: String? = null,
    @SerializedName("FCRESPONSABLE")
    val fCRESPONSABLE: String? = null,
    @SerializedName("FCTELEFONO")
    val fCTELEFONO: String? = null,
    @SerializedName("FCVOLUNTARIO")
    val fCVOLUNTARIO: String? = null,
    @SerializedName("FIVOLUNTARIOID")
    val fIVOLUNTARIOID: String? = null
)