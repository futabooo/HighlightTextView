package com.futabooo.highlighttextview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.widget.EditText

class HighlightTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : EditText(context, attrs, defStyleAttr, defStyleRes) {

  companion object {
    @JvmStatic
    val DEFAULT_CHARACTER_LIMIT = 200
    @JvmStatic
    val DEFAULT_OVER_LIMIT_BACKGROUND_COLOR = Color.RED
  }

  var characterLimit = DEFAULT_CHARACTER_LIMIT
  var overLimitBackgroundColor = DEFAULT_OVER_LIMIT_BACKGROUND_COLOR

  constructor(context: Context): this(context, null)

  constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

  init {
    val a = context.obtainStyledAttributes(attrs, R.styleable.HighlightTextView, defStyleAttr, 0)
    characterLimit = a.getInt(R.styleable.HighlightTextView_characterLimit, DEFAULT_CHARACTER_LIMIT)
    overLimitBackgroundColor = a.getInt(R.styleable.HighlightTextView_overLimitBackgroundColor,
        DEFAULT_OVER_LIMIT_BACKGROUND_COLOR)
    a.recycle()

    watchHighlightText()
  }

  fun setHighlightText(s: CharSequence) {
    if (s.count() < characterLimit) {
      return
    }

    val spannable = Spannable.Factory.getInstance().newSpannable(s)
    spannable.setSpan(BackgroundColorSpan(overLimitBackgroundColor), characterLimit, s.count(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    setText(spannable)
    setSelection(s.count())
  }

  fun watchHighlightText() {
    addTextChangedListener(Watcher(this))
  }

  class Watcher(var highlightTextView: HighlightTextView) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      highlightTextView.removeTextChangedListener(this)
      s?.let { highlightTextView.setHighlightText(it) }
      highlightTextView.addTextChangedListener(this)
    }

  }
}