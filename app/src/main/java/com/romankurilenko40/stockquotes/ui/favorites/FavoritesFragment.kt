package com.romankurilenko40.stockquotes.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.StockQuotesApplication
import com.romankurilenko40.stockquotes.databinding.FragmentFavoritesBinding
import com.romankurilenko40.stockquotes.domain.Stock

class FavoritesFragment: Fragment() {

    private lateinit var binding: FragmentFavoritesBinding


    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory((requireActivity().application as StockQuotesApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_favorites,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.favoritesList.addItemDecoration(
            DividerItemDecoration(context,
            DividerItemDecoration.VERTICAL)
        )

        binding.bindList(
            viewModel.favoritesList
        )

        return binding.root
    }



    private fun FragmentFavoritesBinding.bindList(
        state: LiveData<List<Stock>>
    ) {
        val favoritesAdapter = FavoritesAdapter(
            { stock -> itemClick(stock) },
            { stock -> itemOnBookmarked(stock) }
        )

        favoritesList.adapter = favoritesAdapter

        state
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { result ->
                favoritesAdapter.submitList(result)
            }
    }


    private fun itemOnBookmarked(item: Stock) {
        viewModel.uiAction (
            UiAction.BookmarkAction(stock = item)
        )
    }

    private fun itemClick(item: Stock) {
        setFragmentResult("requestKey", bundleOf("symbolKey" to item.symbol))
        this.findNavController().navigate(R.id.action_navigation_favorites_to_companyProfileFragment)
    }
}