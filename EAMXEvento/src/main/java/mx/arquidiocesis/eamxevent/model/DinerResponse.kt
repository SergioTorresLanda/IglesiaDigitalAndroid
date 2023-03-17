package mx.arquidiocesis.eamxevent.model


import com.google.gson.annotations.SerializedName
import mx.arquidiocesis.eamxcommonutils.util.live.SingleLiveEvent

data class DinerResponse(
    @SerializedName("FCCOBRO")
    val fCCOBRO: String? = null,
    @SerializedName("FCCOMEDORID")
    val fCCOMEDORID: String? = null,
    @SerializedName("FCNOMBRECOM")
    val fCNOMBRECOM: String? = null,
    @SerializedName("FCRESPONSABLE")
    val fCRESPONSABLE: String? = null,
    @SerializedName("FCCORREO")
    val fCCORREO: String? = null,
    @SerializedName("FCDIRECCION")
    val fCDIRECCION: String? = null,
    @SerializedName("FCDONACOMS")
    val fCDONACOMS: String? = null,
    @SerializedName("FCDONANTES")
    val fCDONANTES: String? = null,
   // @SerializedName("FCHORARIOS")
   // val fCHORARIOS:  MutableList<Schedules>? = null,
    @SerializedName("FCREQUISITOS")
    val fCREQUISITOS: String? = null,
    @SerializedName("fIUSERID")
    val fIUSERID: String? = null,
    @SerializedName("FCSTATUS")
    val fCSTATUS: String? = null,
    @SerializedName("FCTELEFONO")
    val fCTELEFONO: String? = null,
    @SerializedName("FCVOLUNTARIOS")
    val fCVOLUNTARIOS: String? = null,
    @SerializedName("FDFECHAALTA")
    val fDFECHAALTA: String? = null,
    @SerializedName("FDFECULTMOD")
    val fDFECULTMOD: String? = null,
    @SerializedName("FIZONA")
    val fIZONA: String? = null,
    @SerializedName("FNLATITUD")
    val fNLATITUD: String? = null,
    @SerializedName("FNLONGITUD")
    val fNLONGITUD: String? = null
)
