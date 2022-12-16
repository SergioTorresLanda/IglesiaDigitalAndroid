
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import mx.arquidiocesis.eamxpagos.model.BillingModel
import mx.arquidiocesis.eamxpagos.model.ZipCodeBillingModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {
    @POST(WebConfig.BILLING)
    fun postBilling(@Body json: JsonObject): Deferred<Response<ResponseBody>>
    @PUT(WebConfig.BILLINGUPDATE)
    fun putBilling(@Path("tax_data_id") tax_data_id: Int, @Body json: JsonObject): Deferred<Response<ResponseBody>>
    @DELETE(WebConfig.BILLINGUPDATE)
    fun delBilling(@Path("tax_data_id") tax_data_id: Int): Deferred<Response<ResponseBody>>
    @GET(WebConfig.BILLING)
    fun getBilling(): Deferred<Response<List<BillingModel>>>
    @GET(WebConfig.GET_ZIP_CODE)
    fun getZipCode(@Query("zip_code") zip_code: String): Deferred<Response<ZipCodeBillingModel>>
}