package com.romankurilenko40.stockquotes.ui.companyProfile

import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.romankurilenko40.stockquotes.domain.Company
import com.romankurilenko40.stockquotes.domain.Quote
import com.romankurilenko40.stockquotes.network.asQuoteEntryData
import com.romankurilenko40.stockquotes.repository.StockQuotesRepository




class CompanyProfileViewModel(
    private val repository: StockQuotesRepository): ViewModel() {


    val uiState: LiveData<CompanyProfileUiState>

    private val symbolLivedata = MutableLiveData<String>()


    fun setSymbol(symbol: String) {
        symbolLivedata.value = symbol
    }

    init {

        uiState = symbolLivedata.switchMap { symbolData ->
            liveData {
                val state = CompanyProfileUiState(
                    companyProfile = repository.fetchCompanyProfileInfo(symbolData),
                    quote = repository.requestQuote(symbolData),
                    chartData = repository.getQuoteChartData(symbolData, "D")
                )
                emit(state)
            }
        }

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
    val chartData: List<Entry>)