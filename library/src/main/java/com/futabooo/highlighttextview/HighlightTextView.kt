package com.futabooo.highlighttextview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.withStyledAttributes

class HighlightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        @JvmStatic
        val DEFAULT_CHARACTER_LIMIT = 200

        @JvmStatic
        val DEFAULT_OVER_LIMIT_BACKGROUND_COLOR = Color.RED
    }

    var characterLimit = DEFAULT_CHARACTER_LIMIT
    var overLimitBackgroundColor = DEFAULT_OVER_LIMIT_BACKGROUND_COLOR

    init {
        context.withStyledAttributes(set = attrs, attrs = R.styleable.HighlightTextView, defStyleAttr = defStyleAttr) {
            characterLimit = getInt(R.styleable.HighlightTextView_characterLimit, DEFAULT_CHARACTER_LIMIT)
            overLimitBackgroundColor =
                getInt(R.styleable.HighlightTextView_overLimitBackgroundColor, DEFAULT_OVER_LIMIT_BACKGROUND_COLOR)
        }
        watchHighlightText()
    }

    private fun setHighlightText(s: CharSequence) {
        if (s.count() < characterLimit) {
            return
        }

        val spannable = Spannable.Factory.getInstance().newSpannable(s)
        spannable.setSpan(
            BackgroundColorSpan(overLimitBackgroundColor), characterLimit, s.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setText(spannable)
        setSelection(s.count())
    }

    private fun watchHighlightText() {
        addTextChangedListener(Watcher(this))
    }

    private class Watcher(var highlightTextView: HighlightTextView) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Prevent onTextChanged when apply backgroundcolor span
            highlightTextView.removeTextChangedListener(this)
            s?.let { highlightTextView.setHighlightText(it) }
            highlightTextView.addTextChangedListener(this)
        }
    }
}