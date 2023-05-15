package com.romankurilenko40.stockquotes.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.StockQuotesApplication
import com.romankurilenko40.stockquotes.databinding.FragmentSearchBinding
import com.romankurilenko40.stockquotes.network.SearchNetworkResult
import com.romankurilenko40.stockquotes.network.SearchResultItem

class SearchFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory((requireActivity().application as StockQuotesApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )

        binding.bindSearch(
            uiState = viewModel.uiState,
            onQueryChanged = viewModel.uiAction
        )


        val searchResultAdapter = SearchResultAdapter{ query -> onItemClick(query)}
        binding.searchResultList.adapter = searchResultAdapter
        binding.searchResultList.layoutManager = LinearLayoutManager(context)

        binding.bindResultList(
            adapter = searchResultAdapter,
            uiState = viewModel.uiState
        )


        return binding.root
    }

    private fun FragmentSearchBinding.bindSearch(
        uiState: LiveData<SearchUiState>,
        onQueryChanged: (UiAction.Search) -> Unit) {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    updateSearchResultFromInput(onQueryChanged)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        searchView.setOnKeyListener {_, keyCode, event ->
            if (event.action ==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateSearchResultFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        uiState
            .map(SearchUiState::searchString)
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) {
                searchView.setQuery(it, true)
            }
    }

    private fun FragmentSearchBinding.updateSearchResultFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchView.query.trim().let {
            if (it.isNotEmpty()) {
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }


    private fun FragmentSearchBinding.bindResultList(
        adapter: SearchResultAdapter,
        uiState: LiveData<SearchUiState>) {

        uiState
            .map(SearchUiState::searchResult)
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { searchResult ->
                when(searchResult) {
                    is SearchNetworkResult.Success -> {
                        adapter.submitList(searchResult.data.result)
                        resultsCountLabel.visibility = View.VISIBLE
                        if (searchResult.data.count!! > 0) {
                            resultsCountLabel.text = resources
                                .getQuantityString(R.plurals.search_counts, searchResult.data.count, searchResult.data.count)
                        } else {
                            resultsCountLabel.text = resources.getQuantityString(R.plurals.search_counts, 0, 0)
                        }
                    }
                    is SearchNetworkResult.Error -> {
                        resultsCountLabel.visibility = View.INVISIBLE
                        Toast.makeText(requireActivity(), "An error occured ${searchResult.error}", Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }
    }

    private fun onItemClick(item: SearchResultItem) {
        setFragmentResult("requestKey", bundleOf("symbolKey" to item.symbol))
        this.findNavController().navigate(R.id.action_navigation_search_to_companyProfileFragment)
    }
}