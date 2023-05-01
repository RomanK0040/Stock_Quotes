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

    val uiState: LiveData<CompanyProfileUiState>

    val chartState: LiveData<List<Entry>>

    private val symbolLivedata = MutableLiveData<String>()

    fun setSymbol(symbol: String) {
        symbolLivedata.value = symbol
    }

    /**
     * Container to handle actions from UI
     */
    val uiAction: (UiAction) -> Unit

    init {

        val resolution = MutableLiveData(DEFAULT_RESOLUTION)

        uiState = symbolLivedata.switchMap { symbolData ->
            liveData {
                val state = CompanyProfileUiState(
                    companyProfile = repository.fetchCompanyProfileInfo(symbolData),
                    quote = repository.requestQuote(symbolData),
                    resolution = resolution.value!!
                )
                emit(state)
            }
        }

        chartState = resolution.switchMap { newResolution ->
            liveData {
                val chartData = repository.getQuoteChartData(symbolLivedata.value!!, newResolution)
                emit(chartData)
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
    val quote: Quote,
    val resolution: String)


sealed class UiAction {
    data class ActionChecked(
        val chartTimeResolution: String): UiAction()
}
