package com.tailoredapps.codagram.ui.loginscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.SharedPreferencesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.LoginFragmentBinding
import org.koin.android.ext.android.inject
import java.net.InetAddress

class LoginFragment : Fragment() {


    private val viewModel: LoginViewModel by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: LoginFragmentBinding

    private val action = LoginFragmentDirections.actionLoginToHome()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        switchLoadData()

        binding.btnLogin.setOnClickListener {

           saveData()

            when{
                binding.etEmail.text.trim().toString().isEmpty() ->Toast.makeText(requireContext(), "email can not be empty", Toast.LENGTH_LONG).show()
                binding.etPassword.text.trim().toString().isEmpty()-> Toast.makeText(requireContext(), "password can not be empty", Toast.LENGTH_LONG).show()
                binding.etEmail.text.trim().toString().isEmpty() && binding.etPassword.text.trim().toString().isEmpty() -> Toast.makeText(requireContext(), "Can not be empty!", Toast.LENGTH_LONG).show()
                else -> login(binding.etEmail.text.trim().toString(), binding.etPassword.text.trim().toString())
            }

        }

        binding.btnNew.setOnClickListener {
            it.findNavController().navigate(LoginFragmentDirections.actionLoginToRegister())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    fun login(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(action)
                    Log.e("task message", "Successfully")
                    val user = auth.currentUser
                    viewModel.getToken()
                    viewModel.getUser()

                } else {
                    Log.e("task message", "Failed" + task.exception)
                }
            }
    }

    fun updateUI(currentUser: FirebaseUser?, email: String) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                findNavController().navigate(action)
                //var intent = Intent(context,MainView::class.java)
                //ContextCompat.startActivity(intent)
            } else {
                Toast.makeText(
                    context, "Email address is not verified.Please verify your email address!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (binding.switch1.isChecked){
            val user = auth.currentUser
            if (user != null) {
                viewModel.getToken()
                viewModel.getUser()
                findNavController().navigate(action)
            }
        }
        else{
            Toast.makeText(context,getString(R.string.toastSizung),Toast.LENGTH_LONG).show()
        }
    }

    private fun saveData(){
        val sharedPreferences:SharedPreferences = context?.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)!!
        val editor = sharedPreferences.edit()
        editor.apply{
            putBoolean("BOOLEAN_KEY",binding.switch1.isChecked)
        }.apply()
    }

    private fun switchLoadData(){
        val sharedPreferences:SharedPreferences = context?.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)!!
        val savedBoolean = sharedPreferences.getBoolean("BOOLEAN_KEY",false)
        binding.switch1.isChecked = savedBoolean
    }

}




