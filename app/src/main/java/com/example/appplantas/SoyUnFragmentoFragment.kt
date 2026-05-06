package com.example.appplantas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class SoyUnFragmentoFragment : Fragment(R.layout.fragment_soy_un_fragmento) {

    private val viewModel: VideosViewModel by activityViewModels()

    // Referencias a las 4 vistas del player para release en onDestroyView
    private var playerView1: YouTubePlayerView? = null
    private var playerView2: YouTubePlayerView? = null
    private var playerView3: YouTubePlayerView? = null
    private var playerView4: YouTubePlayerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uiState = viewModel.uiState.value

        // Configurar cada card con su video ID
        setupVideoCard(
            view = view,
            playerViewId = R.id.youtubePlayerView1,
            fabId = R.id.fabPlay1,
            btnId = R.id.btnYoutube1,
            videoId = uiState.videoId1,
            assignPlayerView = { playerView1 = it }
        )
        setupVideoCard(
            view = view,
            playerViewId = R.id.youtubePlayerView2,
            fabId = R.id.fabPlay2,
            btnId = R.id.btnYoutube2,
            videoId = uiState.videoId2,
            assignPlayerView = { playerView2 = it }
        )
        setupVideoCard(
            view = view,
            playerViewId = R.id.youtubePlayerView3,
            fabId = R.id.fabPlay3,
            btnId = R.id.btnYoutube3,
            videoId = uiState.videoId3,
            assignPlayerView = { playerView3 = it }
        )
        setupVideoCard(
            view = view,
            playerViewId = R.id.youtubePlayerView4,
            fabId = R.id.fabPlay4,
            btnId = R.id.btnYoutube4,
            videoId = uiState.videoId4,
            assignPlayerView = { playerView4 = it }
        )
    }

    private fun setupVideoCard(
        view: View,
        playerViewId: Int,
        fabId: Int,
        btnId: Int,
        videoId: String,
        assignPlayerView: (YouTubePlayerView) -> Unit
    ) {
        val playerView = view.findViewById<YouTubePlayerView>(playerViewId)
        val fab = view.findViewById<FloatingActionButton>(fabId)
        val btn = view.findViewById<MaterialButton>(btnId)

        assignPlayerView(playerView)

        // Registrar en el lifecycle del fragment — igual que el ejemplo
        lifecycle.addObserver(playerView)

        var youtubePlayer: YouTubePlayer? = null
        var requiresExternalPlayback = false

        // Deshabilitar botones hasta que el player esté listo
        btn.isEnabled = false
        fab.isEnabled = false

        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

            override fun onReady(inicializadoPlayer: YouTubePlayer) {
                youtubePlayer = inicializadoPlayer
                // cueVideo: carga el video con thumbnail sin autoplay
                inicializadoPlayer.cueVideo(videoId, 0f)
                btn.isEnabled = true
                fab.isEnabled = true
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                // Ocultar FAB cuando reproduce, mostrar cuando pausa/termina
                if (state == PlayerConstants.PlayerState.PLAYING) {
                    fab.visibility = View.GONE
                } else if (
                    state == PlayerConstants.PlayerState.PAUSED ||
                    state == PlayerConstants.PlayerState.ENDED ||
                    state == PlayerConstants.PlayerState.VIDEO_CUED
                ) {
                    fab.visibility = View.VISIBLE
                }
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                // Si el video no permite embebido, cambiar botón para abrir en YouTube
                if (error == PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER) {
                    requiresExternalPlayback = true
                    btn.text = getString(R.string.abrir_video_en_youtube)
                    fab.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.video_no_permite_embebido),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        // Acción del botón y del FAB
        fun reproducir() {
            if (requiresExternalPlayback) {
                abrirEnYoutube(videoId)
            } else if (youtubePlayer != null) {
                youtubePlayer!!.loadVideo(videoId, 0f)
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_se_pudo_abrir_video), Toast.LENGTH_SHORT).show()
            }
        }

        btn.setOnClickListener { reproducir() }
        fab.setOnClickListener { reproducir() }
    }

    private fun abrirEnYoutube(videoId: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        try {
            startActivity(appIntent)
        } catch (_: ActivityNotFoundException) {
            try {
                startActivity(webIntent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(requireContext(), getString(R.string.no_se_pudo_abrir_video), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        // Release explícito igual que el ejemplo
        listOf(playerView1, playerView2, playerView3, playerView4).forEach { pv ->
            pv?.let {
                lifecycle.removeObserver(it)
                it.release()
            }
        }
        playerView1 = null
        playerView2 = null
        playerView3 = null
        playerView4 = null
        super.onDestroyView()
    }
}
