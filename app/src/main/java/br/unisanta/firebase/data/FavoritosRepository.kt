package br.unisanta.firebase.data

import br.unisanta.firebase.model.Favorito
import br.unisanta.firebase.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


object FavoritosRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun colecaoFavoritos() =
        db.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("favoritos")

    fun salvar(produto: Produto, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        val dados = hashMapOf(
            "id" to produto.id,
            "titulo" to produto.title,
            "preco" to produto.price,
            "categoria" to produto.category,
            "salvoEm" to FieldValue.serverTimestamp()
        )
        colecaoFavoritos()
            .document(produto.id.toString())
            .set(dados)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro.message ?: "Erro ao salvar") }
    }

    fun listar(onSucesso: (List<Favorito>) -> Unit, onErro: (String) -> Unit) {
        colecaoFavoritos()
            .orderBy("salvoEm", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.map { doc ->
                    Favorito(
                        id = doc.getLong("id")?.toInt() ?: 0,
                        titulo = doc.getString("titulo") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        salvoEm = doc.getTimestamp("salvoEm")?.toDate()
                    )
                }
                onSucesso(lista)
            }
            .addOnFailureListener { erro -> onErro(erro.message ?: "Erro ao ler favoritos") }
    }

    fun remover(id: Int, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        colecaoFavoritos()
            .document(id.toString())
            .delete()
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro.message ?: "Erro ao remover") }
    }
}
