package com.practicum.playlistmaker.search.view_model

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.api.ResourceProvider
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor

class SearchViewModel(
    private val resourceProvider: ResourceProvider,
    private val interactor: SearchInteractor
) : ViewModel() {

    //todo save search state when rotate screen

    private val handler = Handler(Looper.getMainLooper())
    private var lastUnsuccessfulSearch: String = ""

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private val clearTextState = MutableLiveData<ClearTextState>(ClearTextState.None)
    fun observeClearTextState(): LiveData<ClearTextState> = clearTextState
    fun textCleared() {
        clearTextState.value = ClearTextState.None
    }

    fun onClearTextPressed() {
        clearTextState.value = ClearTextState.ClearText
    }

    public override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun onTextChanged(searchText: String?) {
        if (searchText.isNullOrEmpty()) {
            if (interactor.getSearchHistory().isNotEmpty()) renderState(
                SearchState.HistoryContent(
                    interactor.getSearchHistory()
                )
            ) else renderState(SearchState.EmptyScreen)
        } else {
            searchDebounce(searchText)
        }
    }

    fun onClearSearchHistoryPressed() {
        interactor.clearSearchHistory()
        renderState(SearchState.EmptyScreen)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRefreshSearchButtonPressed() {
        searchRequest(lastUnsuccessfulSearch)
    }

    fun onTrackPressed(track: Track) {
        interactor.addTrackToSearchHistory(track)
    }

    fun onFocusChanged(hasFocus: Boolean, searchText: String) {
        if (hasFocus && searchText.isEmpty() && interactor.getSearchHistory().isNotEmpty()) {
            renderState(SearchState.HistoryContent(interactor.getSearchHistory()))
        }
    }

    private fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            interactor.searchTracks(searchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (foundTracks != null) {
                        if (foundTracks.isNotEmpty()) {
                            renderState(SearchState.SearchContent(foundTracks))
                        } else {
                            renderState(
                                SearchState.EmptySearch(
                                    resourceProvider.getString(
                                        R.string.nothing_is_found
                                    )
                                )
                            )
                        }
                    }
                    if (errorMessage != null) {
                        lastUnsuccessfulSearch = searchText
                        renderState(SearchState.Error(errorMessage))
                    }
                }
            })
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}