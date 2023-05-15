package com.romankurilenko40.stockquotes.ui.companyProfile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
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
import com.romankurilenko40.stockquotes.StockQuotesApplication
import com.romankurilenko40.stockquotes.databinding.FragmentCompanyProfileBinding
import com.romankurilenko40.stockquotes.network.ProfileNetworkResult
import java.text.SimpleDateFormat
import java.util.*

private const val DEFAULT_COMPANY_SYMBOL = "AAPL"

class CompanyProfileFragment: Fragment() {

    private lateinit var binding: FragmentCompanyProfileBinding

    var symbol: String? = null


    private val viewModel: CompanyProfileViewModel by viewModels {
        CompanyProfileViewModelFactory((requireActivity().application as StockQuotesApplication).repository)
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
        viewModel.profileState.observe(viewLifecycleOwner) {
            if (it.companyProfile is ProfileNetworkResult.Success) {
                binding.company = it.companyProfile.data
            } else if (it.companyProfile is ProfileNetworkResult.Error){
                Toast.makeText(requireActivity(), "An error occured ${it.companyProfile.error}", Toast.LENGTH_LONG)
                    .show()
            }
            binding.quote = it.quote

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
            if (chartData.chartEntries.isNotEmpty()) {
                binding.setupChart(chartData.chartEntries, chartData.resolution)
            }
        }
    }

    private fun FragmentCompanyProfileBinding.setupChart(data: List<Entry>, resolution: String) {
        val chartData = LineDataSet(data, "Quote")

        quoteLineChart.setTouchEnabled(false)
        quoteLineChart.axisRight.isEnabled = false

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

        quoteLineChart.data.setDrawValues(false)


        val xAxis = quoteLineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date(value.toLong() * 1000L))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMaximum = data.last().x+0.1f

        quoteLineChart.description.isEnabled = false

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