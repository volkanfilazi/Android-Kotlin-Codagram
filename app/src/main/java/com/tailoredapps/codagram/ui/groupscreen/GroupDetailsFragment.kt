package com.tailoredapps.codagram.ui.groupscreen

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.FragmentGroupDetailsBinding
import com.tailoredapps.codagram.databinding.RegisterDialogBinding
import com.tailoredapps.codagram.models.KEY
import com.tailoredapps.codagram.models.UpdateGroup
import com.tailoredapps.codagram.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File


class GroupDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGroupDetailsBinding
    @ExperimentalCoroutinesApi
    private val viewModel: GroupDetailsViewModel by inject()
    private val adapter: SearchAdapter by inject()
    private val groupAdapter: GroupDetailsAdapter by inject()
    private lateinit var groupId: String
    private var creatorId: String? = null
    private lateinit var alertDialogBinding: RegisterDialogBinding
    private lateinit var file: File
    private lateinit var groupName: String
    private lateinit var input:String


    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllGroups()
        bindToLiveData()
        bindGetMyGroupToLiveData()
        myMessage()


        binding.searchEditRecyclerview.apply {
            adapter = this@GroupDetailsFragment.adapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        binding.membersRecyclerview.apply {
            adapter = this@GroupDetailsFragment.groupAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        binding.auto.addTextChangedListener {
            searchKey()
        }

        binding.inviteButton.setOnClickListener {
            val selectedUsers =
                viewModel.getSearchedUser().value?.filter { it.selected }?.map { it.user.id }

            if (selectedUsers == null) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.snackInviteUser),
                    Snackbar.LENGTH_SHORT
                ).show()

            } else {
                viewModel.sendGroupInvites(groupId.toString())
            }


        }
        binding.ivSaveButton.setOnClickListener {
            updateGroupImage()
        }

        alert()
        groupName = arguments?.getString(KEY.groupNameKey).toString()
        groupId = arguments?.getString(KEY.groupIdKey).toString()
        creatorId = arguments?.getString(KEY.creatorIdKey).toString()


        viewModel.getGroupById(groupId.toString())
        viewModel.getAllGroupsByObject(groupId)
        bindImage()
        groupDetailPageAccess()
        searchKey()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    fun alert() {
        binding.ivProfileImage.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())

            dialogBuilder.setMessage(getString(R.string.alertDeleteOrEdit))
                .setPositiveButton(
                    getString(R.string.alertEdit),
                    DialogInterface.OnClickListener { dialog, id ->
                        if (FirebaseAuth.getInstance().currentUser!!.uid == creatorId) {
                            uploadClickAction()
                            binding.ivSaveButton.visibility = View.VISIBLE

                        } else {
                            Snackbar.make(
                                requireView(),
                                getString(R.string.alertOnlyCreatorEdit),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
                .setNegativeButton(
                    getString(R.string.alertDelete),
                    DialogInterface.OnClickListener { dialog, id ->
                        deleteGroupImage()
                        viewModel.getGroupById(groupId.toString())
                        dialog.cancel()
                    })
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.GroupPictureUpdate))
            alert.show()
        }
    }

    @ExperimentalCoroutinesApi
    fun bindToLiveData() {
        viewModel.getSearchedUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
        })
    }

    @ExperimentalCoroutinesApi
    private fun searchKey() {

         input = binding.auto.text.toString()
        if (input.isEmpty()) {
            binding.searchEditRecyclerview.visibility = View.GONE
            binding.inviteButton.isEnabled = false
            binding.inviteButton.setImageResource(R.drawable.ic_baseline_add_box_inactive_24)
        } else {
            viewModel.searchUser(input)
            binding.searchEditRecyclerview.visibility = View.VISIBLE
            binding.inviteButton.isEnabled = true
            binding.inviteButton.setImageResource(R.drawable.ic_baseline_add_box_24)
            Timber.d(input)
        }
    }

    private fun groupDetailPageAccess(){

        if (FirebaseAuth.getInstance().currentUser?.uid == creatorId){
            binding.searchEditRecyclerview.isVisible = true
            binding.auto.isVisible = true
            binding.inviteButton.isVisible = true
        }else{
            binding.searchEditRecyclerview.isVisible = false
            binding.auto.isVisible = false
            binding.inviteButton.isVisible = false
        }
    }

    @ExperimentalCoroutinesApi
    private fun bindGetMyGroupToLiveData() {
        viewModel.getMyGroupMembers().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            groupAdapter.submitList(it)

            groupAdapter.setUpListener(object : GroupDetailsAdapter.ItemRemoveClickListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onItemClicked(user: User) {
                    val dialogBuilder = AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(getString(R.string.alertText,"${user.firstname}"))
                        .setCancelable(true)
                        .setPositiveButton(
                            getString(R.string.alertCancel),
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                        .setNegativeButton(
                            getString(R.string.alertDelete),
                            DialogInterface.OnClickListener { dialog, id ->
                                if (FirebaseAuth.getInstance().currentUser?.uid == creatorId) {
                                    viewModel.deleteMember(groupId.toString(), user.id.toString())

                                } else {
                                    Snackbar.make(
                                        requireView(),
                                        getString(R.string.snackOnlyCreatorCanDeleteMember),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                viewModel.getGroupById(groupId.toString())
                                dialog.cancel()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle(getString(R.string.alertAlert))
                    alert.show()
                }
            })
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    fun createAlertDialog() {
        binding.tvGroupTitle.setOnClickListener {
            alertDialogBinding = RegisterDialogBinding.inflate(layoutInflater)
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
            alertDialog.setView(alertDialogBinding.root)
            alertDialog.create()
            val alert = alertDialog.show()

            alertDialogBinding.updateBtn.setOnClickListener {
                if (FirebaseAuth.getInstance().currentUser!!.uid == creatorId) {
                    val firstNameFire = alertDialogBinding.groupName.text.toString()
                    viewModel.updateGroup(groupId.toString(), UpdateGroup(firstNameFire))
                    alert.dismiss()

                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.snackOnlyCreatorCanEditTitle),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                when {

                    alertDialogBinding.groupName.text.toString()
                        .isEmpty() -> Toast.makeText(
                        context,
                        getString(R.string.alertInputText),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            alertDialogBinding.cancelbtn.setOnClickListener {
                alert.dismiss()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group_delete, menu)
        val down = menu.findItem(R.id.delete)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    fun getDelete() {


        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setMessage(getString(R.string.alertDeleteMemberOrGroup,"${groupName}"))
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                })
            // negative button text and action
            .setNegativeButton(
                getString(R.string.alertDelete),
                DialogInterface.OnClickListener { dialog, id ->
                    if (FirebaseAuth.getInstance().currentUser!!.uid == creatorId) {
                        viewModel.deleteGroup(groupId.toString())
                        view?.findNavController()?.popBackStack()
                        Snackbar.make(
                            requireView(),
                            requireContext().getString(R.string.alertDeleteGroup,"${groupName}"),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.exitGroup(groupId.toString())
                        view?.findNavController()?.popBackStack()
                    }


                    dialog.cancel()

                })

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.alertAlert))
        alert.show()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                getDelete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    fun deleteGroupImage() {
        viewModel.deleteGroupImage(groupId.toString())
        viewModel.getGroupById(groupId.toString())
    }


    private fun listImages() {
        var i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
    }

    @ExperimentalCoroutinesApi
    fun updateGroupImage() {
        viewModel.updateGroupImage(groupId.toString(), Uri.fromFile(file))
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


    private fun uploadClickAction() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()

        listImages()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalCoroutinesApi
    fun bindImage() {
        viewModel.getMyImage().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            createAlertDialog()
            binding.tvGroupTitle.text = it.name.toString()
            binding.tvGroupCreatedBy.text = it.creator?.firstname.toString()

            Glide.with(requireContext())
                .load(it.image?.url)
                .placeholder(R.drawable.ic_baseline_image_48)
                .into(binding.ivProfileImage)
        })
    }

    fun myMessage() {

        viewModel.message.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                Snackbar.make(requireView(),it,Snackbar.LENGTH_SHORT).show()
            }
        })

    }
}
