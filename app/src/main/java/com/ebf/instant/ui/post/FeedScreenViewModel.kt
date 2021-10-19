package com.ebf.instant.ui.post

import androidx.lifecycle.ViewModel
import com.ebf.instant.model.PostWithUser
import com.ebf.instant.repo.PostRepository
import kotlinx.coroutines.flow.Flow

class FeedScreenViewModel(postRepository: PostRepository) : ViewModel() {

    val posts: Flow<List<PostWithUser>> = postRepository.getAllPosts()

}