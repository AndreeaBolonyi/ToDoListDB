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
import ro.andreea.bolonyi.todolist.viewmodel.SignUpViewModel

class CreateAccountFragment : Fragment() {
    private lateinit var signUpViewModel: SignUpViewModel

    private fun validateName(name: String): Boolean {
        for(character: Char in name) {
            if(character.isLetter() || character.isWhitespace())
                continue
            else
                return false
        }
        return true
    }

    //https://github.com/shinnn/github-username-regex
    private fun validateGitHubUsername(gitHubUsername: String): Boolean {
        if(gitHubUsername.length > 39)
            return false

        for(i in gitHubUsername.indices) {
            if(gitHubUsername[i].isLetter() || gitHubUsername[i].isDigit() || gitHubUsername[i] == '-')
                continue
            if(i != 0 && gitHubUsername[i] == '-' && gitHubUsername[i-1] == '-')
                return false
            return false
        }

        return true
    }

    private fun validateEmailAddress(email: String): Boolean {
        if (email == "")
            return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.length > 5
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.create_account_fragment, container, false)
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        view.findViewById<Button>(R.id.buttonCreateAccount).setOnClickListener{
            val name: String = view.findViewById<EditText>(R.id.editTextName).text.toString()
            val gitHubUsername: String = view.findViewById<EditText>(R.id.editTextGitHub).text.toString()
            val email: String = view.findViewById<EditText>(R.id.editTextEmailSignUp).text.toString()
            val password: String = view.findViewById<EditText>(R.id.editTextPasswordSignUp).text.toString()
            val confirmPassword: String = view.findViewById<EditText>(R.id.editTextConfirmPassword).text.toString()

            if (!validateName(name)) {
                Toast.makeText(this.context, "Please insert a valid name(only letters)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validateGitHubUsername(gitHubUsername)) {
                Toast.makeText(this.context, "Please insert a valid GitHub username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validateEmailAddress(email)) {
                Toast.makeText(this.context, "Please insert a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!validatePassword(password)) {
                Toast.makeText(this.context, "Please insert a longer password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password != confirmPassword) {
                Toast.makeText(this.context, "Please insert same password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(context, "Please wait, server is processing your request", Toast.LENGTH_LONG).show()
            signUpViewModel.signup(name, gitHubUsername, email, password)

            signUpViewModel.mutableUserId.observe(viewLifecycleOwner, {
                Log.d("observer", "new user id $it")
                if (it != null) {
                    signUpViewModel.newUser.userId = it
                    Utils.currentUser = signUpViewModel.newUser
                    findNavController().navigate(R.id.mainPageFragment)
                }
            })

            signUpViewModel.mutableError.observe(this, {
                if (signUpViewModel.mutableError.value != "") {
                    Toast.makeText(context, "${signUpViewModel.mutableError.value}", Toast.LENGTH_LONG).show()
                }
            })
        }

        return view
    }
}