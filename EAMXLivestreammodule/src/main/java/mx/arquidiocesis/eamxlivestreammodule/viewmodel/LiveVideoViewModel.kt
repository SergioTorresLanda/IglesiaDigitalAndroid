package mx.arquidiocesis.eamxlivestreammodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxlivestreammodule.repository.LiveStreamRepository

class LiveVideoViewModel(private val liveStreamRepository: LiveStreamRepository) : ViewModel() {

    var errorResponse = liveStreamRepository.errorResponse
    val videoResponse = liveStreamRepository.videoResponse
    private val existsLiveStreamingMD = MutableLiveData<Boolean>()
    val searchInfoContent: LiveData<Boolean>
        get() = existsLiveStreamingMD

    fun getVideos(){
        GlobalScope.launch {
            liveStreamRepository.getLiveVideos()
        }
    }
    fun getExistsLiveVideos(){
        GlobalScope.launch {
            val response = liveStreamRepository.getExistsLiveVideos()
            if(response.sucess){
                response.data?.let { data ->
                    existsLiveStreamingMD.postValue(data.isNotEmpty())
                }?: kotlin.run {
                    existsLiveStreamingMD.postValue(false)
                }
            }else{
                existsLiveStreamingMD.postValue(false)
            }
        }
    }


}