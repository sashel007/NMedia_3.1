package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.recyclerview.OnInteractionListener
import ru.netology.nmedia.recyclerview.PostAdapter
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        val adapter = PostAdapter(object: OnInteractionListener {
            override fun like(post: Post) {
                viewModel.like(post.id)
            }
            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }
            override fun edit(post: Post) {
                viewModel.edit(post)
            }
            override fun share(post: Post) {
                viewModel.share(post.id)
            }
        })
        binding.postList?.layoutManager = LinearLayoutManager(this)
        binding.postList?.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts) {
                if (newPost) binding.postList?.smoothScrollToPosition(0)
            }
        }
        viewModel.edited.observe(this) {
            val contentText = it.content
            if (it.id == 0L) {
                binding.editCancelGroup?.visibility = View.GONE
                binding.editCancelGroup?.visibility = View.INVISIBLE
            } else {
                binding.editText?.setText(it.content)
                binding.editText?.focusAndShowKeyboard()
                binding.editCancelGroup?.visibility = View.VISIBLE
                binding.postText?.text = truncateText(contentText,35)
            }
        }
        binding.cancelButton?.setOnClickListener {
            binding.editCancelGroup?.visibility = View.GONE
            binding.editCancelGroup?.visibility = View.INVISIBLE
            binding.editText?.setText("")
//            binding.editText?.clearFocus()
//            AndroidUtils.hideKeyboard(it)
            viewModel.resetEditingState()
        }
        binding.saveButton?.setOnClickListener {
            val text = binding.editText?.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, R.string.error_is_empty,
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.changeContentAndSave(text)
            binding.editText?.setText("")
            binding.editText?.clearFocus()
            AndroidUtils.hideKeyboard(it)
        }

    }

}