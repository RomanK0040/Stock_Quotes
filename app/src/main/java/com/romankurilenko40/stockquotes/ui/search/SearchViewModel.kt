package com.romankurilenko40.stockquotes.ui.search

import androidx.lifecycle.*
import com.romankurilenko40.stockquotes.network.SearchResultItem
import com.romankurilenko40.stockquotes.repository.StockQuotesRepository
import kotlinx.coroutines.flow.map

private const val LAST_SEARCH_REQUEST: String = "last_search_request"

class SearchViewModel(
    private val repository: StockQuotesRepository,
    private val savedStateHandle: SavedStateHandle): ViewModel() {


    /**
     * data to be represented on the UI
     */
    val uiState: LiveData<SearchUiState>

    val uiAction: (UiAction) -> Unit

    init {
        val searchStringRequest = MutableLiveData(savedStateHandle[LAST_SEARCH_REQUEST] ?: "")

        uiState = searchStringRequest
            .distinctUntilChanged()
            .switchMap { searchRequest ->
                liveData {
                    if (searchRequest.trim().isNotEmpty()) {
                        val state = repository.searchSymbol(searchRequest)
                            .map {
                                SearchUiState(
                                    searchString = searchRequest,
                                    searchResultCount = it.count,
                                    searchResult = it.result
                                )
                            }.asLiveData()
                        emitSource(state)
                    }
                }
        }

        uiAction = { action ->
            when(action) {
                is UiAction.Search -> searchStringRequest.value = action.query
            }

        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_REQUEST] = uiState.value?.searchString
        super.onCleared()
    }


}

class SearchViewModelFactory(
    private val repository: StockQuotesRepository):AbstractSavedStateViewModelFactory() {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(repository, handle) as T
    }
}

data class SearchUiState(
    val searchString: String?,
    val searchResultCount :Int?,
    val searchResult: List<SearchResultItem>?
)

sealed class UiAction {
    data class Search(val query: String): UiAction()
}