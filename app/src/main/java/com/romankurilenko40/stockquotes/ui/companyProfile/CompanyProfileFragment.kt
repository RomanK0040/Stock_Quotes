package com.romankurilenko40.stockquotes.ui.companyProfile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.databinding.FragmentCompanyProfileBinding

private const val DEFAULT_COMPANY_SYMBOL = "AAPL"

class CompanyProfileFragment: Fragment() {

    private lateinit var binding: FragmentCompanyProfileBinding

    var symbol: String? = null


    private val viewModel: CompanyProfileViewModel by viewModels {
        CompanyProfileViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val symbol = bundle.getString("symbolKey") ?: DEFAULT_COMPANY_SYMBOL
            viewModel.setSymbol(symbol)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_company_profile,
            container,
            false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.company = it.companyProfile
            binding.quote = it.quote
            val chartData = LineDataSet(it.chartData, "Quote")
            binding.quoteLineChart.data = LineData(chartData)
            it.quote.priceChange?.let { priceChange ->
                if (priceChange > 0) {
                    binding.priceChange.setTextColor(Color.parseColor("#008000"))
                    binding.currentPrice.setTextColor(Color.parseColor("#008000"))
                }
                if (priceChange < 0) {
                    binding.priceChange.setTextColor(Color.parseColor("#ff0000"))
                    binding.currentPrice.setTextColor(Color.parseColor("#ff0000"))
                }
            }

        }
    }


}