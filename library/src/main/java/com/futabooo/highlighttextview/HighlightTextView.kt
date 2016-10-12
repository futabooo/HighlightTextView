package com.futabooo.highlighttextview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.widget.EditText

class HighlightTextView : EditText {

  val DEFAULT_CHARACTER_LIMIT = 200
  val DEFAULT_OVER_LIMIT_BACKGROUND_COLOR = Color.RED

  var characterLimit = DEFAULT_CHARACTER_LIMIT
  var overLimitBackgroundColor = DEFAULT_OVER_LIMIT_BACKGROUND_COLOR

  @JvmOverloads public constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
  : super(context, attrs, defStyleAttr) {
    var a = context.obtainStyledAttributes(attrs, R.styleable.HighlightTextView, defStyleAttr, 0)
    characterLimit = a.getInt(R.styleable.HighlightTextView_characterLimit, DEFAULT_CHARACTER_LIMIT)
    overLimitBackgroundColor = a.getInt(R.styleable.HighlightTextView_overLimitBackgroundColor,
        DEFAULT_OVER_LIMIT_BACKGROUND_COLOR)
    a.recycle()

    watchHighlightText()
  }

  fun setHighlightText(s: CharSequence?) {
    if (s!!.count() < characterLimit) {
      return
    }

    var spannable = Spannable.Factory.getInstance().newSpannable(s)
    spannable.setSpan(BackgroundColorSpan(overLimitBackgroundColor), characterLimit, s.count(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    setText(spannable)
    setSelection(s.count())
  }

  fun watchHighlightText() {
    addTextChangedListener(Watcher(this))
  }

  class Watcher : TextWatcher {

    var highlightTextView: HighlightTextView

    constructor(highlightTextView: HighlightTextView) {
      this.highlightTextView = highlightTextView
    }


    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      highlightTextView.removeTextChangedListener(this)
      highlightTextView.setHighlightText(s)
      highlightTextView.addTextChangedListener(this)
    }

  }
}