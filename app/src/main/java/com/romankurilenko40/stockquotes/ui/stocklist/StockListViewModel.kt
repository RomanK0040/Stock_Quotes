package com.romankurilenko40.stockquotes.ui.stocklist

import androidx.lifecycle.*
import com.romankurilenko40.stockquotes.domain.Quote
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.domain.StockExchange
import com.romankurilenko40.stockquotes.network.StockNetworkResult
import com.romankurilenko40.stockquotes.repository.StockQuotesRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val TAG = "StockListViewModel"
private const val LAST_SELECTED_EXCHANGE: String = "last_selected_exchange"
private const val VISIBLE_THRESHOLD = 1

class StockListViewModel(
    private val repository: StockQuotesRepository,
    private val savedStateHandle: SavedStateHandle): ViewModel() {


    /**
     * data to be represented on the UI
     */
    val uiState: LiveData<StockListUiState>

    /**
     * Container to handle actions from UI
     */
    val uiAction: (UiAction) -> Unit

    val exchangeLoadingStatus = MutableLiveData<ApiLoadingStatus>()


    init {
        val selectedExchange = MutableLiveData(savedStateHandle[LAST_SELECTED_EXCHANGE] ?: DEFAULT_EXCHANGE)

        uiState = selectedExchange
            .distinctUntilChanged()
            .switchMap { exchange ->
                exchangeLoadingStatus.value = ApiLoadingStatus.LOADING
                liveData {
                    val state = repository.getStocksBySelectedExchange(exchange.exchangeCountry, exchange.mic)
                        .map {
                            StockListUiState(
                                stockList = it,
                                exchange = exchange
                            )
                        }.asLiveData()

                    emitSource(state)
                    exchangeLoadingStatus.value = ApiLoadingStatus.DONE
                }

            }

        uiAction = { action ->
            when(action) {
                is UiAction.ItemSelected -> selectedExchange.value = action.selectedExchange
                is UiAction.Scroll -> if (action.shouldFetchQuotes) {
                    val exchange = selectedExchange.value
                    /*
                    pageLoadingStatus.value = true

                    if (exchange != null) {
                        viewModelScope.launch {
                            repository.getMoreStocks(exchange.exchangeCountry, exchange.mic)
                        }
                    }
                    */

                }
                is UiAction.BookmarkAction -> {
                    viewModelScope.launch {
                        repository.putStockInBookmark(action.item)
                    }

                }
            }

        }

    }

    suspend fun updateQuote(symbol: String): Quote {
        return repository.requestQuote(symbol)
    }


    override fun onCleared() {
        savedStateHandle[LAST_SELECTED_EXCHANGE] = uiState.value?.exchange
        super.onCleared()
    }


    companion object {
        private val DEFAULT_EXCHANGE: StockExchange = StockExchange(0, "US", "XNAS", "NASDAQ - All Markets")

    }
}

class StockListViewModelFactory(
    private val repository: StockQuotesRepository): AbstractSavedStateViewModelFactory() {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return StockListViewModel(repository, handle) as T
    }

}


enum class ApiLoadingStatus {LOADING, DONE}

data class StockListUiState(
    val stockList: StockNetworkResult,
    val exchange: StockExchange,
    val isPageLoading: Boolean = false
)

private val UiAction.Scroll.shouldFetchQuotes
    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount

sealed class UiAction {
    data class ItemSelected(
        val selectedExchange: StockExchange): UiAction()
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction()
    data class BookmarkAction(
        val item: Stock
    ) : UiAction()
}


data class StockItemUiState(
    val isBookmarked: Boolean,
    val isQuoteIsLoading: Boolean
)

