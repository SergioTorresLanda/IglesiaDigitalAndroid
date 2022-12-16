package mx.arquidiocesis.eamxcommonutils.repostiory

sealed interface EAMXResponse{
    class Success<T>(val data: T): EAMXResponse
    class Error(val message: String,val code: Int,val codeHttp: Int): EAMXResponse
}

