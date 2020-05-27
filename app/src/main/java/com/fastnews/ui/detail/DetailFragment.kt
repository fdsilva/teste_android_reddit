package com.fastnews.ui.detail

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionInflater
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fastnews.R
import com.fastnews.databinding.FragmentDetailPostBinding
import com.fastnews.mechanism.TimeElapsed
import com.fastnews.service.model.CommentData
import com.fastnews.service.model.PostData
import com.fastnews.ui.web.CustomTabsWeb
import com.fastnews.viewmodel.PostDetailViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_detail_post.*
import kotlinx.android.synthetic.main.include_detail_post_thumbnail.*
import kotlinx.android.synthetic.main.include_detail_post_title.*
import kotlinx.android.synthetic.main.include_header_detail_post_share.*
import kotlinx.android.synthetic.main.include_item_timeline_ic_score.*
import kotlinx.android.synthetic.main.include_item_timeline_timeleft.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    companion object {
        const val KEY_POST = "KEY_POST"
        const val TYPE_TEXT = "text/html"
    }

    private var post: PostData? = null

    private val detailViewModel by viewModel<PostDetailViewModel>()
    private lateinit var binding: FragmentDetailPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailPostBinding.inflate(inflater).apply {
        binding = this
        binding.lifecycleOwner = viewLifecycleOwner
    }.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        getExtras()
        buildActionBar()
        populateUi()
        setupObservables()
    }

    private fun getExtras() {
        this.arguments.let {
            post = it?.getParcelable(KEY_POST)
        }
    }

    private fun buildActionBar() {
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun populateUi() {
        populateAuthor()
        populateTimeLeftValue()
        populateTitle()
        populateThumbnail()
        buildOnClickDetailThumbnail()
        populateScore()
        fetchComments()
    }

    private fun setupListeners() {
        item_timeline_bt_share.setOnClickListener{
            showShareScreen()
        }
    }

    private fun fetchComments() {
            post.let {
                post?.id?.let { it1 ->
                    detailViewModel.getComments(postId = it1)
                }
            }
    }

    private fun setupObservables() {
        with(detailViewModel) {
            comments.observe(viewLifecycleOwner, Observer<List<CommentData>> { comments ->
                populateComments(comments)
            })

            networkStatus.observe(viewLifecycleOwner, Observer {
                binding.networkStatus = it
            })
        }
    }

    private fun populateComments(comments: List<CommentData>) {
        if (isAdded) {
            activity?.runOnUiThread(Runnable {
                detail_post_comments.removeAllViews()

                for (comment in comments) {
                    val itemReview = CommentItem.newInstance(requireActivity(), comment)
                    detail_post_comments.addView(itemReview)
                }
            })
        }
    }

    private fun populateAuthor() {
        post?.author.let {
            item_timeline_author.text = it
            (activity as AppCompatActivity).supportActionBar?.title = it
        }
    }

    private fun populateTimeLeftValue() {
        post?.created_utc.let {
            val elapsed = it?.toLong()?.let { it1 -> TimeElapsed.getTimeElapsed(it1) }
            item_timeline_timeleft.text = elapsed
        }
    }

    private fun populateTitle() {
        post?.title.let {
            item_detail_post_title.text = it
        }
    }

    private fun populateThumbnail() {
        var thumbnailUrl = ""
        if(post?.preview != null) {
            post?.preview?.images?.map {
                if (!TextUtils.isEmpty(it.source.url)) {
                    thumbnailUrl = HtmlCompat.fromHtml(it.source.url, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                }
            }
        }

        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(item_detail_post_thumbnail.context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(item_detail_post_thumbnail)
            item_detail_post_thumbnail.visibility = View.VISIBLE
        }
    }

    private fun buildOnClickDetailThumbnail() {
        item_detail_post_thumbnail.setOnClickListener {
            if(!post?.url.isNullOrEmpty()) {
                context.let {
                    val customTabsWeb = post?.url?.let { it1 ->
                        CustomTabsWeb(requireContext(),
                            it1
                        )
                    }
                    customTabsWeb?.openUrlWithCustomTabs()
                }
            } else {
                Snackbar.make(item_detail_post_thumbnail, R.string.error_detail_post_url, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private fun populateScore() {
        post?.score.let {
            item_timeline_bt_score_text.text = it.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showShareScreen() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post?.url)
            type = TYPE_TEXT
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}