package com.ebf.instant.ui.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.data.repository.CommentRepository
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CommentScreenViewModel(
    signInViewModelDelegate: SignInViewModelDelegate,
    private val commentRepository: CommentRepository,
    private val externalScope: CoroutineScope,
    private val postId: String
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    private val _state = MutableStateFlow(CommentViewState())
    val state: StateFlow<CommentViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                commentRepository.getAllCommentsFromPost(postId)
            ) { comments ->
                CommentViewState(
                    comments = comments.first()
                )
            }.collect { _state.value = it }
        }
    }

    fun comment(postId: String, message: String) = externalScope.launch {
        userValue?.let { user ->
            commentRepository.publishComment(
                currentUser = user,
                message = message,
                postId = postId
            )
        }
    }

}

data class CommentViewState(
    val comments: List<CommentWithUser> = emptyList()
)
