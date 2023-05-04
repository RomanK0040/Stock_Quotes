package com.romankurilenko40.stockquotes.ui.companyProfile

import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.romankurilenko40.stockquotes.domain.Company
import com.romankurilenko40.stockquotes.domain.Quote
import com.romankurilenko40.stockquotes.network.CandlesDataResult
import com.romankurilenko40.stockquotes.repository.StockQuotesRepository




class CompanyProfileViewModel(
    private val repository: StockQuotesRepository): ViewModel() {

    companion object {
        private const val DEFAULT_RESOLUTION = "D"
    }

    val profileState: LiveData<CompanyProfileUiState>

    val chartState: LiveData<ChartUiState>

    private val symbolLivedata = MutableLiveData<String>()

    fun setSymbol(symbol: String) {
        symbolLivedata.value = symbol
    }

    /**
     * Container to handle actions from UI
     */
    val uiAction: (UiAction) -> Unit

    init {

        profileState = symbolLivedata.switchMap { symbolData ->
            liveData {
                val state = CompanyProfileUiState(
                    companyProfile = repository.fetchCompanyProfileInfo(symbolData),
                    quote = repository.requestQuote(symbolData)
                )
                emit(state)
            }
        }

        val resolution = MutableLiveData(DEFAULT_RESOLUTION)

        chartState = resolution.switchMap { newResolution ->
            liveData {
                val state = ChartUiState(
                    resolution = newResolution,
                    candlesData = repository.fetchStockCandles(symbolLivedata.value!!, newResolution),
                    //TODO - same request makes twice - need to change
                    chartEntries = repository.getQuoteChartData(symbolLivedata.value!!, newResolution)
                )
                emit(state)
            }

        }

        uiAction = { action ->
            when (action) {
                is UiAction.ActionChecked -> resolution.value = action.chartTimeResolution
            }
        }

    }


    override fun onCleared() {

        super.onCleared()
    }
}

class CompanyProfileViewModelFactory:  ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompanyProfileViewModel(StockQuotesRepository()) as T
    }
}



data class CompanyProfileUiState (
    val companyProfile: Company,
    val quote: Quote)

data class ChartUiState (
    val resolution: String,
    val candlesData: CandlesDataResult,
    val chartEntries: List<Entry>)


sealed class UiAction {
    data class ActionChecked(
        val chartTimeResolution: String): UiAction()
}
