package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT).toString()
        }

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val searchDrawable = R.drawable.ic_search_small
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, 0, 0, 0)
        searchEditText.setText(searchText)
        searchEditText.setupClearButtonWithAction()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.setupClearButtonWithAction() {

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val searchDrawable = R.drawable.ic_search_small
                val clearIcon =
                    if (editable?.isNotEmpty() == true) R.drawable.ic_baseline_clear else 0
                setCompoundDrawablesWithIntrinsicBounds(searchDrawable, 0, clearIcon, 0)
                searchText = editable.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    this.setText("")
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}

