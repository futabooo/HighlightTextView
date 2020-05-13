package com.futabooo.highlighttextview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.withStyledAttributes
import androidx.core.text.getSpans

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

    private fun watchHighlightText() {
        addTextChangedListener(Watcher(this, characterLimit))
    }

    private fun applyBgColorSpan(spannable: Spannable) {
        spannable.removeBgColorSpan()
        spannable.setSpan(
            BackgroundColorSpan(overLimitBackgroundColor),
            characterLimit,
            spannable.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun Spannable.removeBgColorSpan() {
        val bgColorSpans = this.getSpans<BackgroundColorSpan>()
        bgColorSpans.forEach { this.removeSpan(it) }
    }

    private class Watcher(val view: HighlightTextView, val limit: Int) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s == null || s.isEmpty() || s.length < limit) {
                return
            }

            // prevent backspace key in editing text
            val spannable: Spannable = view.editableText ?: SpannableString(view.text)
            if (spannable.isEditing()) {
                return
            }

            view.applyBgColorSpan(spannable)

            if (spannable !is Editable) {
                // prevent call back loop when can't get editable
                view.removeTextChangedListener(this)
                view.setTextKeepState(spannable)
                view.addTextChangedListener(this)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        private fun Spannable.isEditing(): Boolean {
            val spans = this.getSpans<Any>()
            return spans.any { this.getSpanFlags(it) and Spannable.SPAN_COMPOSING == Spannable.SPAN_COMPOSING }
        }
    }
}