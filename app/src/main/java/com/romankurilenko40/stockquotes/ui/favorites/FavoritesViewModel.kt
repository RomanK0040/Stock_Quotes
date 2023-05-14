package com.romankurilenko40.stockquotes.ui.favorites

import androidx.lifecycle.*
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.repository.StockQuotesRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: StockQuotesRepository): ViewModel() {


    val favoritesList: LiveData<List<Stock>> = repository.favoritesFlow.asLiveData()


    /**
     * Container to handle actions from UI
     */
    val uiAction: (UiAction) -> Unit


    init {

        uiAction = {action ->
            when(action) {
                is UiAction.BookmarkAction -> {
                    viewModelScope.launch {
                        repository.putStockInBookmark(action.stock)
                    }
                }
            }
        }
    }


}

class FavoritesViewModelFactory(private val repository: StockQuotesRepository):  ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}


sealed class UiAction {
    data class BookmarkAction(
        val stock: Stock
    ) : UiAction()
}