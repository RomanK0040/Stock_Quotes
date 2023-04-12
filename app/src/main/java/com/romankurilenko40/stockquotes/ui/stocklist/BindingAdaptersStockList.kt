package com.romankurilenko40.stockquotes.ui.stocklist

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.romankurilenko40.stockquotes.R

@BindingAdapter("bookmarkImage")
fun bindBookmarkStateImage(imgView: ImageView, isInBookmark: Boolean) {
    if (isInBookmark) {
        imgView.setImageResource(R.drawable.ic_bookmark_added_36)
    } else {
        imgView.setImageResource(R.drawable.ic_bookmark_not_added_36)
    }
}
