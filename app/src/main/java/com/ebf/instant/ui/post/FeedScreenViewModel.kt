package com.ebf.instant.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.model.PostWithData
import com.ebf.instant.repo.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedScreenViewModel(private val postRepository: PostRepository) : ViewModel() {

    val posts: Flow<List<PostWithData>> = postRepository.getAllPosts()

    fun likeOrDislikePost(postId: String) = viewModelScope.launch {
        postRepository.likeOrDislikePost(postId = postId)
    }

}