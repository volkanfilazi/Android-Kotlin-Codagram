package com.tailoredapps.codagram.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.FragmentGroupBinding
import com.tailoredapps.codagram.databinding.FragmentSettingsBinding
import com.tailoredapps.codagram.databinding.RegisterDialogBinding
import com.tailoredapps.codagram.models.UpdateGroup
import com.tailoredapps.codagram.ui.groupscreen.GroupViewModel
import com.tailoredapps.codagram.ui.loginscreen.LoginFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import java.io.File

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var file: File
    private val viewModel: SettingsViewModel by inject()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        changeFirstLastNick()
        bindToLiveData()
        viewModel.getUsers()
        deleteUser()
        myMessage()
        alert()


        binding.btnUpload.setOnClickListener {
            if (::file.isInitialized) {
                viewModel.addPhoto(Uri.fromFile(file))
            }
        }

        binding.btnTest.setOnClickListener {
            it.findNavController().navigate(SettingsFragmentDirections.actionSettingsToTestFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group_exit, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when {
            R.id.logout == id -> {
                auth.signOut()
                findNavController().navigate(SettingsFragmentDirections.actionSettingsViewToLogin())
            }
        }
        return true
    }


    fun alert() {
        binding.ivProfileImage.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage(getString(R.string.deleteOrEdit))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.alertEdit), DialogInterface.OnClickListener { dialog, id ->
                    uploadClickAction()
                    viewModel.getUsers()
                })
                .setNegativeButton(getString(R.string.alertDelete), DialogInterface.OnClickListener { dialog, id ->
                    deleteUserImage()
                    dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.show()
        }
    }

    fun deleteUserImage() {
        viewModel.deleteUserImage()
    }

    private fun uploadClickAction() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
        listImages()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            binding.ivProfileImage.setImageURI(fileUri)
            //You can get File object from intent
            file = ImagePicker.getFile(data)!!
            //You can also get File Path from intent
            val filePath: String = ImagePicker.getFilePath(data)!!
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Snackbar.make(requireView(), ImagePicker.getError(data), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(requireView(), getString(R.string.cancelled), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun listImages() {
        var i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
    }

    private fun changeFirstLastNick() {
        binding.tvUserName.setOnClickListener {

        }
    }


    @ExperimentalCoroutinesApi
    fun bindToLiveData() {
        viewModel.getMyGroupMembers().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvLastName.text = Editable.Factory.getInstance().newEditable(it.firstname)
            binding.tvUserName.text = Editable.Factory.getInstance().newEditable(it.lastname)
            binding.tvNickName.text = Editable.Factory.getInstance().newEditable(it.nickname)
            Glide.with(requireContext())
                .load(it.image?.url)
                .placeholder(R.drawable.ic_baseline_image_48)
                .into(binding.ivProfileImage)
        })
        binding.btnSaveChanges.setOnClickListener {

            viewModel.updateNickName(
                binding.tvNickName.text.toString(),
                binding.tvUserName.text.toString(),
                binding.tvLastName.text.toString()
            )
        }
    }

    @ExperimentalCoroutinesApi
    fun deleteUser() {
        binding.deleteUser.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())

            dialogBuilder.setMessage(getString(R.string.dialogDelteOwnProfile))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.abbrechen),
                    DialogInterface.OnClickListener { dialog, id ->
                    })
                .setNegativeButton(
                    getString(R.string.delete),
                    DialogInterface.OnClickListener { dialog, id ->
                        viewModel.deleteUser()
                        dialog.cancel()
                        dialog.dismiss()

                    })


            val alert = dialogBuilder.create()
            alert.cancel()
            alert.show()
        }

    }

    fun myMessage() {

        viewModel.message.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        })
    }
}