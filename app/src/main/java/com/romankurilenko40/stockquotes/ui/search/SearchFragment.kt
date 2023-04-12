package com.romankurilenko40.stockquotes.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.romankurilenko40.stockquotes.databinding.FragmentSearchBinding
import com.romankurilenko40.stockquotes.network.SearchResultItem

class SearchFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.Factory
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

        bindResultList(
            adapter = searchResultAdapter,
            uiState = viewModel.uiState
        )

        binding.bindQuantityResult(viewModel.uiState)

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

    private fun FragmentSearchBinding.bindQuantityResult(uiState: LiveData<SearchUiState>) {

        resultsCountLabel.visibility = View.INVISIBLE

        uiState
            .map(SearchUiState::searchResultCount)
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { count ->
                if (count != null) {
                    resultsCountLabel.text = resources.getQuantityText(R.plurals.search_counts, count)
                    resultsCountLabel.visibility = View.VISIBLE
                } else {
                    resultsCountLabel.text = resources.getQuantityText(R.plurals.search_counts, 0)
                    resultsCountLabel.visibility = View.VISIBLE
                }
            }
    }

    private fun bindResultList(
        adapter: SearchResultAdapter,
        uiState: LiveData<SearchUiState>) {

        uiState
            .map(SearchUiState::searchResult)
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { searchResult ->
                adapter.submitList(searchResult)
            }
    }

    private fun onItemClick(item: SearchResultItem) {
        setFragmentResult("requestKey", bundleOf("symbolKey" to item.symbol))
        this.findNavController().navigate(R.id.action_navigation_search_to_companyProfileFragment)
    }
}