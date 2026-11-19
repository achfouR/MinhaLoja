package br.unisanta.firebase.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.unisanta.firebase.R
import br.unisanta.firebase.api.RetrofitClient
import br.unisanta.firebase.data.FavoritosRepository
import br.unisanta.firebase.databinding.ActivityProdutosBinding
import br.unisanta.firebase.model.Produto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProdutosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdutosBinding
    private lateinit var adapter: ProdutoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Produtos"

        adapter = ProdutoAdapter { produto -> salvarFavorito(produto) }
        binding.recyclerProdutos.layoutManager = LinearLayoutManager(this)
        binding.recyclerProdutos.adapter = adapter

        binding.btnTentarNovamente.setOnClickListener { carregarProdutos() }

        carregarProdutos()
    }

    private fun carregarProdutos() {
        binding.progress.visibility = View.VISIBLE
        binding.layoutErro.visibility = View.GONE
        binding.recyclerProdutos.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val produtos = RetrofitClient.produtoService.listarProdutos()
                adapter.atualizarLista(produtos)
                binding.recyclerProdutos.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.txtErro.text = "Não foi possível carregar os produtos.\n${e.message}"
                binding.layoutErro.visibility = View.VISIBLE
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun salvarFavorito(produto: Produto) {
        FavoritosRepository.salvar(
            produto,
            onSucesso = { Toast.makeText(this, "Salvo nos favoritos", Toast.LENGTH_SHORT).show() },
            onErro = { msg -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_produtos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_favoritos -> {
                startActivity(Intent(this, FavoritosActivity::class.java))
                true
            }
            R.id.menu_sair -> {
                sair()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sair() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
