package br.unisanta.firebase.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.firebase.databinding.ItemFavoritoBinding
import br.unisanta.firebase.model.Favorito
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritoAdapter(
    private val onRemover: (Favorito) -> Unit
) : RecyclerView.Adapter<FavoritoAdapter.FavoritoViewHolder>() {

    private var favoritos: List<Favorito> = emptyList()
    private val formatoData = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    fun atualizarLista(novaLista: List<Favorito>) {
        favoritos = novaLista
        notifyDataSetChanged()
    }

    class FavoritoViewHolder(val binding: ItemFavoritoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritoViewHolder {
        val binding = ItemFavoritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritoViewHolder, position: Int) {
        val favorito = favoritos[position]
        with(holder.binding) {
            txtTitulo.text = favorito.titulo
            txtPreco.text = String.format(Locale.US, "$ %.2f", favorito.preco)
            txtCategoria.text = favorito.categoria
            txtSalvoEm.text = favorito.salvoEm?.let { "Salvo em ${formatoData.format(it)}" } ?: "Salvando..."
            btnRemover.setOnClickListener { onRemover(favorito) }
        }
    }

    override fun getItemCount() = favoritos.size
}
