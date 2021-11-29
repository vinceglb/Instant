package com.ebf.instant.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebf.instant.data.repository.PostRepository
import com.ebf.instant.model.PostWithData
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FeedViewModel(
    signInViewModelDelegate: SignInViewModelDelegate,
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    private val _state = MutableStateFlow(FeedViewState())
    val state: StateFlow<FeedViewState>
        get() = _state

    init {
        viewModelScope.launch {
            postRepository.getAllPosts()
                .catch { throwable ->
                    // TODO: emit a UI error here. For now we'll just rethrow
                    throw throwable
                }
                .collect {
                    _state.value = FeedViewState(it)
                }
        }
    }

    fun likeOrDislikePost(postId: String) {
        externalScope.launch {
            userValue?.let { user ->
                postRepository.likeOrDislikePost(currentUser = user, postId = postId)
            }
        }
    }

}

data class FeedViewState(
    val posts: List<PostWithData> = emptyList()
)
