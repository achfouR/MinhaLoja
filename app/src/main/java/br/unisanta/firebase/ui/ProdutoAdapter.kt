package br.unisanta.firebase.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.firebase.databinding.ItemProdutoBinding
import br.unisanta.firebase.model.Produto
import java.util.Locale

class ProdutoAdapter(
    private val onClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    private var produtos: List<Produto> = emptyList()

    fun atualizarLista(novaLista: List<Produto>) {
        produtos = novaLista
        notifyDataSetChanged()
    }

    class ProdutoViewHolder(val binding: ItemProdutoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val binding = ItemProdutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]
        with(holder.binding) {
            txtTitulo.text = produto.title
            txtPreco.text = String.format(Locale.US, "$ %.2f", produto.price)
            txtCategoria.text = produto.category
            root.setOnClickListener { onClick(produto) }
        }
    }

    override fun getItemCount() = produtos.size
}
