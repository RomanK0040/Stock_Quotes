package com.romankurilenko40.stockquotes.ui.newsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.romankurilenko40.stockquotes.R
import com.romankurilenko40.stockquotes.databinding.FragmentMarketNewsBinding



class MarketNewsFragment: Fragment() {

    private var newsAdapter: NewsListAdapter? = null

    private val viewModel: MarketNewsViewModel by viewModels {
        MarketNewsViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMarketNewsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_market_news,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner


        newsAdapter = NewsListAdapter()

        binding.marketNewsList.adapter = newsAdapter
        binding.marketNewsList.layoutManager = LinearLayoutManager(context)

        binding.newsCategoriesChipGroup.setOnCheckedStateChangeListener { group, checkedId ->
            val title = group.findViewById<Chip>(checkedId[0]).text.toString()
            viewModel.setCategory(title)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.news.observe(viewLifecycleOwner) { news ->
            newsAdapter?.submitList(news)
        }

    }
}