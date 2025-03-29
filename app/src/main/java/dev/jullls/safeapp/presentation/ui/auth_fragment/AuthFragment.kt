package dev.jullls.safeapp.presentation.ui.auth_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.jullls.safeapp.R
import dev.jullls.safeapp.databinding.FragmentAuthBinding
import dev.jullls.safeapp.presentation.data.shared_preference.SharedPrefs
import dev.jullls.safeapp.presentation.exception.AuthException

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefs: SharedPrefs
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            navigateToHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        sharedPrefs = SharedPrefs(requireContext())

        // Инициализация Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            addTestUsers()
            setupUI()
            setupGoogleSignIn()
        } catch (e: AuthException) {
            handleAuthError(e)
        }
    }

    private fun addTestUsers() {
        val testUsers = listOf(
            "user1" to "password1",
            "user2" to "password2"
        )

        if (!sharedPrefs.checkUser("user1", "password1")) {
            testUsers.forEach { (login, pass) ->
                try {
                    sharedPrefs.saveUser(login, pass)
                    Log.d("Auth", "Test user $login added successfully")
                } catch (e: AuthException) {
                    Log.e("Auth", "Failed to add test user $login", e)
                }
            }
        }
    }

    private fun setupUI() {
        // Очистка ошибок при вводе
        binding.tfLogin.editText?.doOnTextChanged { _, _, _, _ ->
            binding.tfLogin.error = null
        }

        binding.tfPassword.editText?.doOnTextChanged { _, _, _, _ ->
            binding.tfPassword.error = null
        }

        binding.btnAuth.setOnClickListener {
            try {
                val login = binding.tfLogin.editText?.text.toString().trim()
                val password = binding.tfPassword.editText?.text.toString().trim()

                when {
                    login.isBlank() -> {
                        binding.tfLogin.error = "Введите логин"
                        return@setOnClickListener
                    }
                    password.isBlank() -> {
                        binding.tfPassword.error = "Введите пароль"
                        return@setOnClickListener
                    }
                    login.length < 4 -> {
                        showError("Логин должен быть не менее 4 символов")
                        return@setOnClickListener
                    }
                    password.length < 6 -> {
                        showError("Пароль должен быть не менее 6 символов")
                        return@setOnClickListener
                    }
                }

                if (sharedPrefs.checkUser(login, password)) {
                    navigateToHome()
                } else {
                    showError("Неверный логин или пароль")
                }
            } catch (e: AuthException.HashingError) {
                showError("Ошибка безопасности. Попробуйте позже")
                Log.e("Auth", "Hashing error", e)
            } catch (e: AuthException.StorageError) {
                showError("Ошибка доступа к данным")
                Log.e("Auth", "Storage error", e)
            } catch (e: Exception) {
                showError("Произошла непредвиденная ошибка")
                Log.e("Auth", "Unexpected error", e)
            }
        }

        binding.btnOauth2.setOnClickListener {
            Toast.makeText(requireContext(), "Google auth not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGoogleSignIn() {
        binding.btnOauth2.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                handleGoogleSignInResult(account)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Sign-in failed: ${e.statusCode}")
                showError("Ошибка входа через Google")
            }
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        try {
            sharedPrefs.saveOAuthUser(account.id!!, account.email!!, account.displayName)

            navigateToHome()
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Error saving user data", e)
            showError("Ошибка сохранения данных")
        }
    }

    private fun handleAuthError(e: AuthException) {
        when (e) {
            is AuthException.StorageError -> {
                showError("Ошибка хранилища данных")
                Log.e("Auth", "Critical storage error", e)
            }
            is AuthException.HashingError -> {
                showError("Критическая ошибка безопасности")
                Log.e("Auth", "Critical hashing error", e)
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_authFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}