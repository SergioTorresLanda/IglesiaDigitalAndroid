package mx.arquidiocesis.eamxevent.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewModelEvent(val repositoryEvent: RepositoryEvent) : ViewModel() {

    val topicsModel = repositoryEvent.zonaResponse

    fun getTopics() {
        GlobalScope.launch {
            repositoryEvent.getZone()
        }
    }
}