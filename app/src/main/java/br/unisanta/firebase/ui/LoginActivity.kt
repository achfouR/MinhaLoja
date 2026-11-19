package br.unisanta.firebase.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.firebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.FirebaseNetworkException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null) {
            abrirProdutos()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener { entrar() }
        binding.btnCriarConta.setOnClickListener { criarConta() }
    }

    private fun entrar() {
        val email = binding.edtEmail.text.toString().trim()
        val senha = binding.edtSenha.text.toString()
        if (!validar(email, senha)) return

        carregando(true)
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { abrirProdutos() }
            .addOnFailureListener { erro ->
                carregando(false)
                mostrarErro(traduzirErro(erro))
            }
    }

    private fun criarConta() {
        val email = binding.edtEmail.text.toString().trim()
        val senha = binding.edtSenha.text.toString()
        if (!validar(email, senha)) return

        carregando(true)
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { abrirProdutos() }
            .addOnFailureListener { erro ->
                carregando(false)
                mostrarErro(traduzirErro(erro))
            }
    }

    private fun validar(email: String, senha: String): Boolean {
        return when {
            email.isEmpty() || senha.isEmpty() -> {
                mostrarErro("Preencha o e-mail e a senha.")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                mostrarErro("E-mail inválido.")
                false
            }
            senha.length < 6 -> {
                mostrarErro("A senha precisa ter pelo menos 6 caracteres.")
                false
            }
            else -> {
                binding.txtErro.visibility = View.GONE
                true
            }
        }
    }

    private fun traduzirErro(erro: Exception): String = when (erro) {
        is FirebaseAuthWeakPasswordException -> "A senha precisa ter pelo menos 6 caracteres."
        is FirebaseAuthUserCollisionException -> "Já existe uma conta com este e-mail."
        is FirebaseAuthInvalidUserException -> "Usuário não encontrado."
        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
        is FirebaseNetworkException -> "Sem conexão com a internet."
        is FirebaseAuthException -> "Erro: ${erro.message}"
        else -> erro.message ?: "Erro desconhecido."
    }

    private fun mostrarErro(mensagem: String) {
        binding.txtErro.text = mensagem
        binding.txtErro.visibility = View.VISIBLE
    }

    private fun carregando(ativo: Boolean) {
        binding.progress.visibility = if (ativo) View.VISIBLE else View.GONE
        binding.btnEntrar.isEnabled = !ativo
        binding.btnCriarConta.isEnabled = !ativo
    }

    private fun abrirProdutos() {
        startActivity(Intent(this, ProdutosActivity::class.java))
        finish()
    }
}
