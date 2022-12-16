package mx.arquidiocesis.eamxcommonutils.api.core

interface BaseCallback<T> {
    fun onSuccess(t: T)
    fun onFailure(messageError: String)
}