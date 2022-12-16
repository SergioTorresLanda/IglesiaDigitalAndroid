package mx.arquidiocesis.eamxbiblioteca.retrofit

import mx.arquidiocesis.eamxbiblioteca.config.WebConfi
import mx.arquidiocesis.eamxbiblioteca.models.ContentCategoryResponse
import mx.arquidiocesis.eamxbiblioteca.models.HomeResponse
import mx.arquidiocesis.eamxbiblioteca.models.LibraryModel
import mx.arquidiocesis.eamxbiblioteca.models.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterfaceLibrary {
    @GET(WebConfi.CONTENTHOME)
    fun getContentHome(): Call<HomeResponse>

    @GET(WebConfi.LIBRARYSEARCH)
    fun getLibrarySearch(@Query("tag") tag: String): Call<List<SearchResponse>>

    @GET(WebConfi.CATEGORYCONTENT)
    fun getContentCategory(
        @Header("X-User-Id") userId: String,
        @Path("category") code: String,
        @Query("tag") tagText: String?,
        @Query("order") order: String?,
        @Query("topic") topic: Boolean,
        @Query("age") age: String?
    ): Call<ContentCategoryResponse>

    @GET(WebConfi.CATEGORYCONTENT)
    fun getDefaultContentCategory(
        @Path("category") code: String
    ): Call<ContentCategoryResponse>

    @GET(WebConfi.LIBRARYCONTENT)
    fun getDetalle(@Path("content_id") id: Int): Call<LibraryModel>
}