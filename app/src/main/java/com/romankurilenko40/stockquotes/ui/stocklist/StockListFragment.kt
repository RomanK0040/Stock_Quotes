package com.romankurilenko40.stockquotes.ui.stocklist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.StockQuotesApplication
import com.romankurilenko40.stockquotes.databinding.FragmentStockListBinding
import com.romankurilenko40.stockquotes.domain.Stock
import com.romankurilenko40.stockquotes.domain.StockExchange
import com.romankurilenko40.stockquotes.network.StockNetworkResult

const val SELECTED_SYMBOL = "selected symbol"

class StockListFragment: Fragment() {

    private lateinit var binding: FragmentStockListBinding


    private val viewModel: StockListViewModel by viewModels {
        StockListViewModelFactory((requireActivity().application as StockQuotesApplication).repository)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_stock_list,
            container,
            false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.stockList.addItemDecoration(DividerItemDecoration(context,
            DividerItemDecoration.VERTICAL))

        setupSpinner()

        binding.bindSelection(
            state = viewModel.uiState,
            itemSelected = viewModel.uiAction
        )

        binding.bindList(
            state = viewModel.uiState,
            onScrolled = viewModel.uiAction
        )

        return binding.root
    }


    private fun setupSpinner() {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getSupportedExchanges(),
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.exchangeMenu.adapter = adapter
        }
    }

    /**
     * Retrieve country name, market names, mic codes of supported exchanges from resource file
     */
    private fun getSupportedExchanges(): List<StockExchange> {
        val supportedExchanges = mutableListOf<StockExchange>()
        val exchanges = resources.getStringArray(R.array.supportedExchanges)
        for ((index, exchange) in exchanges.withIndex()) {
            val params = exchange.split(":")
            supportedExchanges.add(StockExchange(index, params[0], params[1], params[2]))
        }
        return supportedExchanges
    }

    private fun FragmentStockListBinding.bindSelection(
        state: LiveData<StockListUiState>,
        itemSelected: (UiAction.ItemSelected) -> Unit
    ) {
        exchangeMenu.setSelection(state.value?.exchange?.id ?: 0)

        exchangeMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val exchange = exchangeMenu.selectedItem as StockExchange
                itemSelected(UiAction.ItemSelected(selectedExchange = exchange))
                //stockList.scrollToPosition(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.exchangeLoadingStatus.observe(viewLifecycleOwner) { status ->
            when(status) {
                ApiLoadingStatus.LOADING -> {
                    statusImage.visibility = View.VISIBLE
                }
                ApiLoadingStatus.DONE -> {
                    statusImage.visibility = View.GONE
                }
            }
        }

        state
            .map(StockListUiState::exchange)
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                exchangeMenu.setSelection(it.id)
            }
    }

    private fun FragmentStockListBinding.bindList(
        state: LiveData<StockListUiState>,
        onScrolled: (UiAction.Scroll) -> Unit) {

        val stocksAdapter = StockListAdapter(
            { stock -> itemClick(stock) },
            { stock -> itemOnBookmarked(stock) }
        )

        stockList.adapter = stocksAdapter

        //setupScrollListener(onScrolled)

        state
            .map(StockListUiState::stockList)
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is StockNetworkResult.Success -> {
                        stocksAdapter.submitList(result.data)
                    }
                    is StockNetworkResult.Error -> {
                        Toast.makeText(requireActivity(), "An error occured ${result.error}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
    }


   private fun FragmentStockListBinding.setupScrollListener(
       onScrolled: (UiAction.Scroll) -> Unit
   ) {
       val layoutManager = stockList.layoutManager as LinearLayoutManager
       stockList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
           override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
               super.onScrolled(recyclerView, dx, dy)
               val totalItemCount = layoutManager.itemCount
               val visibleItemCount = layoutManager.childCount
               val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

               onScrolled(
                   UiAction.Scroll(
                       visibleItemCount = visibleItemCount,
                       lastVisibleItemPosition = lastVisibleItem,
                       totalItemCount = totalItemCount
                   )
               )
           }
       })
   }

    private fun itemOnBookmarked(item: Stock) {
        viewModel.uiAction (
            UiAction.BookmarkAction(item = item)
        )
    }

    private fun itemClick(item: Stock) {
        setFragmentResult("requestKey", bundleOf("symbolKey" to item.symbol))
        this.findNavController().navigate(R.id.action_navigation_stock_list_to_companyProfileFragment)
    }

}