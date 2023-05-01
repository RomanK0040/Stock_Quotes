package com.romankurilenko40.stockquotes.ui.companyProfile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.github.mikephil.charting.formatter.ValueFormatter
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.databinding.FragmentCompanyProfileBinding
import com.romankurilenko40.stockquotes.network.CandlesDataResult
import java.text.SimpleDateFormat
import java.util.*

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
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_company_profile,
            container,
            false)

        binding.resolutionSelector.setOnCheckedChangeListener { group, checkedId ->
            timeResolutionClicked(group.findViewById(checkedId) as RadioButton)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.company = it.companyProfile
            binding.quote = it.quote
            binding.resolution = it.resolution

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

        viewModel.chartState.observe(viewLifecycleOwner) { chartData ->
            if (chartData.isNotEmpty()) {
                binding.setupChart(chartData)
            }
        }
    }

    private fun FragmentCompanyProfileBinding.setupChart(data: List<Entry>) {
        val chartData = LineDataSet(data, "Quote")

        quoteLineChart.setTouchEnabled(false)
        quoteLineChart.axisLeft.isEnabled = false

        val dateFormat = when(resolution) {
            "M" -> "yyyy-MM"
            "W" -> "yyyy-MM"
            "D" -> "yyyy-MM-dd"
            "60" -> "MM-dd"
            "30" -> "HH:mm"
            "15" -> "HH:mm"
            "5" -> "HH:mm"
            "1" -> "HH:mm"
            else -> "yyyy-MM-dd"
        }

        quoteLineChart.data = LineData(chartData)

        val xAxis = quoteLineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date(value.toLong() * 1000L))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        quoteLineChart.animateY(500);
    }


    private fun timeResolutionClicked(view: RadioButton) {
        if (view.isChecked) {
            viewModel.uiAction(
                UiAction.ActionChecked(view.text.toString())
            )
        }
    }
}