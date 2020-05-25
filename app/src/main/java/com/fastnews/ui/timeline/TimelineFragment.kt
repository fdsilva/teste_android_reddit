package com.fastnews.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.fastnews.R
import com.fastnews.databinding.FragmentTimelineBinding
import com.fastnews.service.model.PostData
import com.fastnews.ui.detail.DetailFragment.Companion.KEY_POST
import com.fastnews.viewmodel.PostViewModel
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimelineFragment : Fragment() {
    private val viewModel by viewModel<PostViewModel>()
    private lateinit var adapter: TimelineAdapter
    private lateinit var binding: FragmentTimelineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTimelineBinding.inflate(inflater).apply {
        binding = this
        binding.lifecycleOwner = viewLifecycleOwner
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildActionBar()
        buildTimeline()
        setupObservables()
        state_without_conn_timeline.setOnClickListener {
            viewModel.verifyConnectionState()
        }
    }

    private fun setupObservables() {
        with(viewModel) {
            networkState.observe(viewLifecycleOwner, Observer {
                binding.networkStatus = it
            })
            posts.observe(viewLifecycleOwner, Observer<PagedList<PostData>> {
                adapter.submitList(it)
            })
        }
    }

    private fun buildActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false) // disable the button
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false) // remove the left caret
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.app_name)
    }

    private fun buildTimeline() {
        adapter = TimelineAdapter { it, imageView ->
            onClickItem(it, imageView)
        }

        timeline_rv.layoutManager = LinearLayoutManager(context)
        timeline_rv.itemAnimator = DefaultItemAnimator()
        timeline_rv.adapter = adapter
    }

    private fun onClickItem(postData: PostData, imageView: ImageView) {
        val extras = FragmentNavigatorExtras(
            imageView to "thumbnail"
        )
        val bundle = Bundle()
        bundle.putParcelable(KEY_POST, postData)
        findNavController().navigate(R.id.action_timeline_to_detail, bundle, null, extras)
    }
}