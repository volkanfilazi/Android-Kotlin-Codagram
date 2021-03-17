package com.tailoredapps.codagram.ui.newStoryScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.FragmentSecondBinding
import com.tailoredapps.codagram.ui.groupscreen.GroupDetailsAdapter
import com.tailoredapps.codagram.ui.groupscreen.SearchAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.util.*


class NewStoryFragment : Fragment() {
    private val viewModel: NewStoryViewModel by inject()
    private val groupDetailsAdapter: GroupDetailsAdapter by inject()
    private val searchAdapter: SearchAdapter by inject()
    private lateinit var binding: FragmentSecondBinding
    lateinit var file: File
    val REQUEST_IMAGE_CAPTURE = 2
    private lateinit var getSpinnerItem: String
    private lateinit var adapter: SpinnerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SpinnerAdapter(requireContext(), EmptyArray(), EmptyArray())
        binding.spinner1.adapter = adapter

        binding.searchResult.apply {
            adapter = this@NewStoryFragment.searchAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        spinnerSelectedItem()
        bindToLiveData()
        bindGetMyGroupsLiveData()
        uploadClickAction()
        postButtonAction()
        getGroups()
        bindToGetMyGroupMemberLiveData()
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
            binding.tvUpload.setImageURI(fileUri)
            //You can get File object from intent
            file = ImagePicker.getFile(data)!!
            //You can also get File Path from intent
            val filePath: String = ImagePicker.getFilePath(data)!!
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Snackbar.make(requireView(), ImagePicker.getError(data), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(requireView(), getString(R.string.ausgew_hlt), Snackbar.LENGTH_SHORT)
                .show()
        }
    }


    private fun uploadClickAction() {
        binding.tvUpload.setOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_LONG).show()
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
            listImages()
        }
    }


    @ExperimentalCoroutinesApi
    private fun postButtonAction() {
        binding.btnPost.setOnClickListener {
            val description = binding.etDescription.text.toString()

            if (description.isEmpty()) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackDescription),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (::file.isInitialized && ::getSpinnerItem.isInitialized) {
                viewModel.post(description, getSpinnerItem, Uri.fromFile(file))
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackRequireToPost),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun spinnerSelectedItem() {
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getSpinnerItem = adapterView?.getItemAtPosition(position).toString()
                if (getSpinnerItem.isEmpty()) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.snackTagOneMember),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.searchUser(getSpinnerItem)

                }
                Timber.e(getSpinnerItem)
            }
        }
    }

    private fun getGroups() {
        val test = viewModel.getGroups()

    }

    private fun EmptyArray(): ArrayList<String> {

        val list = ArrayList<String>()
        for (i in 0 until 20) {
        }
        return list
    }

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.N)
    fun bindGetMyGroupsLiveData() {
        viewModel.getEvents().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                is NewStoryViewModel.Event.ShowMessage -> Snackbar.make(
                    requireView(),
                    it.msg,
                    Snackbar.LENGTH_SHORT
                ).show()
                is NewStoryViewModel.Event.Navigate -> {
                    view?.findNavController()
                        ?.navigate(NewStoryFragmentDirections.actionSecondViewToFirstView())
                }
            }
        })
        viewModel.getMyGroups().observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
            it.forEach {
                val groupName = it.name
                val groupId = it.id
                adapter.data.add(groupId)
                adapter.nameList.add(groupName)
                adapter.notifyDataSetChanged()
            }
        })
    }

    @ExperimentalCoroutinesApi
    fun bindToLiveData() {
        viewModel.getSearchedUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            searchAdapter.submitList(it)
        })
    }

    @ExperimentalCoroutinesApi
    fun bindToGetMyGroupMemberLiveData() {
        viewModel.getMyGroupMembers().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            groupDetailsAdapter.submitList(it)
        })
    }
}
