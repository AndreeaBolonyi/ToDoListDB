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
    private lateinit var viewModel: LoginViewModel

    private fun validateEmailAdress(email: String): Boolean {
        if (email == "")
            return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        if (password.length < 5)
            return false
        return true
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = view.findViewById<EditText>(R.id.passwordEditText).text.toString()
            Log.d("loginPage", "login for $email $password")

            if (!validateEmailAdress(email) || !validatePassword(password)) {
                Toast.makeText(this.context, "Credentials are not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)

            viewModel.loginResult.observe(this, {
                Log.d("observer", "$it")
                if (it.name != "") {
                    Utils.currentUser = it
                    findNavController().navigate(R.id.mainPageFragment)
                } else {
                    Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }
}