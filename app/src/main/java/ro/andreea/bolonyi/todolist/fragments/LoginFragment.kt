package ro.andreea.bolonyi.todolist.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ro.andreea.bolonyi.todolist.R
import ro.andreea.bolonyi.todolist.Utils
import ro.andreea.bolonyi.todolist.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel

    private fun validateEmailAddress(email: String): Boolean {
        if (email == "")
            return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.length > 5
    }

    @SuppressLint("FragmentLiveDataObserve", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email: String = view.findViewById<EditText>(R.id.emailEditText).text.toString()
            val password: String = view.findViewById<EditText>(R.id.passwordEditText).text.toString()
            Log.d("loginPage", "login for $email $password")

            if (!validateEmailAddress(email)) {
                Toast.makeText(context, "Email address is not valid", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!validatePassword(password)) {
                Toast.makeText(context, "Invalid format for password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            loginViewModel.login(email, password)

            loginViewModel.mutableLoginResult.observe(this, {
                Log.d("observer", "$it")
                if (it != null) {
                    Utils.currentUser = it
                    findNavController().navigate(R.id.mainPageFragment)
                } else {
                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_LONG).show()
                }
            })

            loginViewModel.mutableError.observe(this, {
                if (loginViewModel.mutableError.value != "") {
                    Toast.makeText(context, "${loginViewModel.mutableError.value}", Toast.LENGTH_LONG).show()
                }
            })
        }

        view.findViewById<Button>(R.id.buttonSignUp).setOnClickListener{
            findNavController().navigate(R.id.createAccountFragment)
        }

        return view
    }
}