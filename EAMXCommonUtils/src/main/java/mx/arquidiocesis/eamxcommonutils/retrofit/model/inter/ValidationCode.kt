package mx.arquidiocesis.eamxcommonutils.retrofit.model.inter

interface ValidationCode<in T> {
    fun executeValidation(response : T)
}