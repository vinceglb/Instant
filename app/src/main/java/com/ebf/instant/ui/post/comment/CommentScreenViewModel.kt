package com.ebf.instant.ui.post.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.repo.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CommentScreenViewModel(private val postRepository: PostRepository) : ViewModel() {

    fun commentsFromPost(postId: String): Flow<List<CommentWithUser>> =
        postRepository.getAllCommentsFromPost(postId = postId)

    fun comment(postId: String, message: String) = viewModelScope.launch {
        postRepository.publishComment(postId = postId, message = message)
    }

}
