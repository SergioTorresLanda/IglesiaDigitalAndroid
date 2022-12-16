package mx.upax.formacion.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upax.formacion.R
import kotlinx.coroutines.launch
import mx.arquidiocesis.eamxcommonutils.util.live.SingleLiveEvent
import mx.upax.formacion.model.FeaturedModel
import mx.upax.formacion.model.ItemMenu
import mx.upax.formacion.repository.Repository

class FormationViewModel(private val repository: Repository) : ViewModel() {


    val searchTabNewsResponse = repository.searchTabNewsResponse
    val tabNavigationResponse = repository.tabNavigationResponse

    private val searchInfoContentMD = SingleLiveEvent<List<FeaturedModel>>()
    val searchInfoContent: SingleLiveEvent<List<FeaturedModel>>
        get() = searchInfoContentMD

    var errorResponse = repository.errorResponse

    fun searchInformation(tabCategory: String) {
        viewModelScope.launch {
            val response = repository.searchInformation(null, tabCategory)
            if (response.sucess)
                searchInfoContentMD.postValue(response.data ?: listOf())
            else
                repository.errorResponse.postValue(response.exception?.message)
        }
    }

    fun updateViewInItem(itemIdentifier: Int) {
        viewModelScope.launch {
            repository.updateViewInItem(itemIdentifier)
        }
    }

    fun loadViewMenu() {
        viewModelScope.launch {
            repository.getTabNavigation()
        }
    }
}