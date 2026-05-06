package com.example.appplantas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * UiState que contiene los 4 IDs de video de YouTube para la pantalla de Videos.
 */
data class VideosUiState(
    val videoId1: String = "k3Wx5mefRGc",
    val videoId2: String = "YsJdGuTlU3I",
    val videoId3: String = "pL9K173yvoc",
    val videoId4: String = "CJKAXVhBlno",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de Videos de Plantas.
 * Expone el estado de la UI a través de LiveData para que el fragmento lo observe.
 */
class VideosViewModel : ViewModel() {

    private val _uiState = MutableLiveData(VideosUiState())
    val uiState: LiveData<VideosUiState> = _uiState

    /**
     * Retorna el video ID según el índice (1-based) para usarlo en el player.
     */
    fun getVideoId(index: Int): String {
        return when (index) {
            1 -> _uiState.value?.videoId1 ?: "k3Wx5mefRGc"
            2 -> _uiState.value?.videoId2 ?: "YsJdGuTlU3I"
            3 -> _uiState.value?.videoId3 ?: "pL9K173yvoc"
            4 -> _uiState.value?.videoId4 ?: "CJKAXVhBlno"
            else -> "k3Wx5mefRGc"
        }
    }
}
