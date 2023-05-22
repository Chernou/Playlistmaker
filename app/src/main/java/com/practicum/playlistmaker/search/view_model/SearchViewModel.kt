package com.practicum.playlistmaker.search.view_model

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.utils.Creator

class SearchViewModel(
    application: App
) : AndroidViewModel(application) {

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    private val interactor = Creator.provideSearchInteractor(application)
    private val handler = Handler(Looper.getMainLooper())

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

    fun onCreate() {
        if (interactor.getSearchHistory().isNotEmpty()) {
            renderState(SearchState.HistoryContent(interactor.getSearchHistory()))
        } else {
            renderState(SearchState.EmptyScreen)
        }
    }

    fun searchEditTextFocusChanged(hasFocus: Boolean, searchText: String?) {
        if (searchText?.isEmpty() == true && interactor.getSearchHistory()
                .isNotEmpty()
        ) {
            renderState(SearchState.HistoryContent(interactor.getSearchHistory()))
        } else if (searchText != null && searchText.isNotEmpty()) {
            searchDebounce(searchText)
        } else {
            renderState(SearchState.EmptyScreen)
        }
    }

    fun onClearSearchHistoryPressed() {
        interactor.clearSearchHistory()
        renderState(SearchState.EmptyScreen)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRefreshSearchButtonPressed(searchRequest: String) {
        searchRequest(searchRequest)
    }

    fun onTrackPressed(track: Track) {
        interactor.addTrackToSearchHistory(track)
    }

    public override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
                            renderState(SearchState.EmptySearch(getApplication<Application>().getString(
                                R.string.nothing_is_found)))
                        }
                    }
                    if (errorMessage != null) {
                        renderState(SearchState.Error(errorMessage))
                    }
                }
            })
        }
    }
}