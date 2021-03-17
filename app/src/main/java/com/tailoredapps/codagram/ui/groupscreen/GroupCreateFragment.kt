package com.tailoredapps.codagram.ui.groupscreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.FragmentGroupBinding
import com.tailoredapps.codagram.ui.newStoryScreen.NewStoryFragmentDirections
import com.tailoredapps.codagram.ui.newStoryScreen.NewStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.util.*

class GroupFragment : Fragment() {

    private val adapter1: SearchAdapter by inject()
    private lateinit var binding: FragmentGroupBinding
    private val viewModel: GroupViewModel by inject()
    private var file: File? = null
    private lateinit var input: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchResult.apply {
            adapter = this@GroupFragment.adapter1
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        bindToLiveData()
        createButtonAction()
        uploadClickAction()
        searchKey()

        binding.auto.addTextChangedListener {
            searchKey()
        }
    }

    @ExperimentalCoroutinesApi
    private fun searchKey() {
        input = ""
        input = binding.auto.text.toString()

        if (input.isEmpty()) {
            binding.searchResult.visibility = View.GONE
            binding.btnCreateGroup.isEnabled = false
            binding.btnCreateGroup.setImageResource(R.drawable.ic_baseline_add_box_inactive_24)

        } else {
            viewModel.searchUser(input)
            binding.searchResult.visibility = View.VISIBLE
            binding.btnCreateGroup.setImageResource(R.drawable.ic_baseline_add_box_24)
            binding.btnCreateGroup.isEnabled = true
            Timber.d(input)
        }
    }

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.N)
    fun createButtonAction() {
        binding.btnCreateGroup.setOnClickListener { view ->

            val nameGroup = binding.etCreateGroup.text.toString()
            val selectedUsers =
                viewModel.getSearchedUser().value?.filter { it.selected }?.map { it.user.id }

            if (selectedUsers == null) {
                adapter1.currentList
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackInviteUser),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (nameGroup.isEmpty()) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackGroupName),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (file != null) {
                viewModel.createGroup(nameGroup, Uri.fromFile(file))
            } else if (file == null) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackGroupCreate),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }


    @ExperimentalCoroutinesApi
    fun bindToLiveData() {
        viewModel.getEvents().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is NewStoryViewModel.Event.ShowMessage -> Snackbar.make(
                    requireView(),
                    it.msg,
                    Snackbar.LENGTH_SHORT
                ).show()
                is NewStoryViewModel.Event.Navigate -> {
                    view?.findNavController()
                        ?.navigate(GroupFragmentDirections.actionGroupScreenToMyGroupScreen())
                }
            }
        })
        viewModel.getSearchedUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter1.submitList(it)
        })

    }

    private fun uploadClickAction() {
        binding.groupImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(524)
                .maxResultSize(1080, 1080)
                .start()
            listImages()
        }
    }

    private fun listImages() {
        var i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            binding.groupImage.setImageURI(fileUri)
            //You can get File object from intent
            file = ImagePicker.getFile(data)!!
            val filePath: String = ImagePicker.getFilePath(data)!!
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Snackbar.make(requireView(), ImagePicker.getError(data), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(requireView(), getString(R.string.snackCancelled), Snackbar.LENGTH_SHORT)
                .show()
        }
    }


}
