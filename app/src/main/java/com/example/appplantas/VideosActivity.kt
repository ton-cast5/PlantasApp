package com.example.appplantas

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class VideosActivity : AppCompatActivity() {

    private val viewModel: VideosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos)

        // Toolbar con botón de regreso
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Agregar el fragmento por código — igual que el ejemplo (no con android:name en XML)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SoyUnFragmentoFragment())
                .commit()
        }

        // Acceder al ViewModel para asegurar su inicialización — igual que el ejemplo
        viewModel.uiState.value
    }
}
