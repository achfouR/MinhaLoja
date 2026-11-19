package br.unisanta.firebase.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.firebase.data.FavoritosRepository
import br.unisanta.firebase.databinding.ActivityFavoritosBinding

class FavoritosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritosBinding
    private lateinit var adapter: FavoritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Meus favoritos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = FavoritoAdapter { favorito ->
            FavoritosRepository.remover(
                favorito.id,
                onSucesso = {
                    Toast.makeText(this, "Removido", Toast.LENGTH_SHORT).show()
                    carregarFavoritos()
                },
                onErro = { msg -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
            )
        }
        binding.recyclerFavoritos.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavoritos.adapter = adapter

        carregarFavoritos()
    }

    private fun carregarFavoritos() {
        binding.progress.visibility = View.VISIBLE
        FavoritosRepository.listar(
            onSucesso = { lista ->
                binding.progress.visibility = View.GONE
                adapter.atualizarLista(lista)
                binding.txtVazio.text = "Nenhum favorito ainda"
                binding.txtVazio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            },
            onErro = { msg ->
                binding.progress.visibility = View.GONE
                binding.txtVazio.text = msg
                binding.txtVazio.visibility = View.VISIBLE
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
