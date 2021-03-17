package com.tailoredapps.codagram.ui.loginscreen

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.red
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.scwang.wave.MultiWaveHeader
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.RegisterFragmentBinding
import com.tailoredapps.codagram.models.SendUser
import com.tailoredapps.codagram.models.User
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException

class RegisterFragment : Fragment() {

    private val viewModel: LoginViewModel by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: RegisterFragmentBinding
    var image: String? = null
    private lateinit var file: File
    private lateinit var waveHeader: MultiWaveHeader
    val check: Boolean? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        statusInfo()
        createActive(view)
        uploadClickAction()

        binding.btnDialogCancel.setOnClickListener {
            view.findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            binding.ivImageView.setImageURI(fileUri)
            //You can get File object from intent
            file = ImagePicker.getFile(data)!!
            //You can also get File Path from intent
            val filePath: String = ImagePicker.getFilePath(data)!!
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Snackbar.make(requireView(), ImagePicker.getError(data), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(requireView(), "Task Cancelled", Snackbar.LENGTH_SHORT).show()
        }
    }


    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.e("task message", "Successfully")
                                viewModel.getToken()
                                val nickname = binding.dialogNickName.text.toString()
                                val firstName = binding.dialogFirstName.text.toString()
                                val lastName = binding.dialogLastName.text.toString()
                                val password = binding.dialogPassword.text.toString()
                                viewModel.postUser(SendUser(nickname, firstName, lastName))
                                view?.findNavController()
                                    ?.navigate(RegisterFragmentDirections.actionLoginToHome())

                                if (this::file.isInitialized) { viewModel.addPhoto(Uri.fromFile(file)) }

                            } else {
                                Log.e("task message", "Failed" + task.exception)
                            }
                        }
                }
            }
    }

    private fun listImages() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
    }


    private fun uploadClickAction() {
        binding.ivImageView.setOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_LONG).show()
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
            listImages()
        }
    }

    private fun statusInfo() {
        viewModel.infoMessage(
            binding.ivFirstNameStatus,
            binding.ivLastNameStatus,
            binding.ivNickNameStatus,
            binding.ivEmailStatus,
            binding.ivPasswordStatus
        )

    }

    private fun createActive(view: View) {
        binding.btnDialogCreate.setOnClickListener {

            if (!viewModel.statusRulesFirstName(
                    binding.dialogFirstName,
                    binding.ivFirstNameStatus
                ) || !viewModel.statusRulesLastName(
                    binding.dialogLastName,
                    binding.ivLastNameStatus
                ) || !viewModel.statusRulesNickName(
                    binding.dialogNickName,
                    binding.ivNickNameStatus
                ) || !viewModel.statusRulesEmail(binding.dialogEmail) || !viewModel.statusRulesPassword(
                    binding.dialogPassword,
                    binding.ivPasswordStatus
                )
            ) {
                return@setOnClickListener
            } else {
                createUser(
                    binding.dialogEmail.text.toString(),
                    binding.dialogPassword.text.toString()
                )

            }

        }
    }


}