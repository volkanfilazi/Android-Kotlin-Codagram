package com.tailoredapps.codagram.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tailoredapps.codagram.R
import com.tailoredapps.codagram.databinding.AlertDialogFilterBinding
import com.tailoredapps.codagram.databinding.FragmentFirstBinding
import com.tailoredapps.codagram.databinding.HomeFeedScreenBinding
import com.tailoredapps.codagram.models.Group
import com.tailoredapps.codagram.models.Post
import com.tailoredapps.codagram.ui.groupscreen.GroupViewModel
import com.tailoredapps.codagram.ui.homeFeedScreen.FilterGroupAdapter
import com.tailoredapps.codagram.ui.homeFeedScreen.HomeFeedAdapter
import com.tailoredapps.codagram.ui.homeFeedScreen.HomeFeedViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import java.util.*


class HomeFeedScreen : Fragment() {

    private val groupViewModel: GroupViewModel by inject()
    private val adapter: HomeFeedAdapter by inject()
    private val viewModel: HomeFeedViewModel by inject()
    private val navController by lazy(::findNavController)  //Method referencing
    private lateinit var binding: FragmentFirstBinding
    private val myGroupsAdapter: FilterGroupAdapter by inject()
    private lateinit var alertDialogBinding: AlertDialogFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeFeedScreen.apply {
            adapter = this@HomeFeedScreen.adapter
        }
        view.findNavController().popBackStack(R.id.action_first_view_to_login, false)

        bindPostLiveData()
        myMessage()
        viewModel.getStoryPost(null.toString())

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getStoryPost(null.toString())
            binding.swipeRefresh.isRefreshing = false

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_groups, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                filter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun filter() {

        alertDialogBinding = AlertDialogFilterBinding.inflate(layoutInflater)
        val alertDialog = MaterialAlertDialogBuilder(requireContext())
        alertDialog.setView(alertDialogBinding.root)
        alertDialog.create()
        alertDialog.show()
        alertDialogBinding.filterView.apply {
            adapter = this@HomeFeedScreen.myGroupsAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        bindgetmyGroupToLiveData()
        viewModel.getAllGroups()

    }

    @ExperimentalCoroutinesApi
    fun bindPostLiveData() {
        viewModel.getMyPost().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
            adapter.setUpListener(object : HomeFeedAdapter.ItemCLickedListener {
                override fun onItemClicked(like: Boolean, post: Post) {
                    likePost(post.id, like)
                }
            })
            adapter.removeUpListener(object : HomeFeedAdapter.ItemGroupRemoveListener {
                override fun onGroupRemoved(post: Post) {
                    if (FirebaseAuth.getInstance().currentUser!!.uid == post.user?.id) {
                        alert(post.id)
                    } else {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.snackOnlyDeleteownPost),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

            })


        })
    }


    fun bindgetmyGroupToLiveData() {
        viewModel.getMyGroups().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            myGroupsAdapter.submitList(it)

            myGroupsAdapter.setUpListener(object : FilterGroupAdapter.ItemFilterListener {
                override fun onItemClicked(group: Group) {
                    viewModel.getStoryPostByQuery(group.id.toString())
                    Snackbar.make(
                        requireView(),
                        "${group.name} ausgewählt",
                        Snackbar.LENGTH_SHORT
                    ).show()

                }
            })
        })


    }

    fun alert(ids: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setMessage(getString(R.string.deletePost))
            .setPositiveButton(
                getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, id ->
                })
            .setNegativeButton(
                getString(R.string.delete),
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.removePost(ids)
                    dialog.cancel()

                })

        val alert = dialogBuilder.create()
        alert.show()
    }


    @ExperimentalCoroutinesApi
    fun likePost(ids: String, like: Boolean) {
        viewModel.likeComment(ids, like)
    }


    fun myMessage() {

        viewModel.message.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            }
        })

    }

}
