package com.ebf.instant.ui.comment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ebf.instant.model.Comment
import com.ebf.instant.model.CommentWithUser
import com.ebf.instant.model.User
import com.ebf.instant.ui.theme.InstantTheme
import org.junit.Rule
import org.junit.Test
import java.util.*

class CommentScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun salut() {
        val user = User(
            id = "abc",
            username = "vince.app",
            name = "Vince",
            imageUrl = "url"
        )

        val comments = listOf(
            CommentWithUser(
                comment = Comment(
                    id = "com1",
                    content = "Hey",
                    createDate = Date(),
                    userId = user.id,
                    postId = "post1"
                ),
                user = user
            ),
            CommentWithUser(
                comment = Comment(
                    id = "com2",
                    content = "What's up",
                    createDate = Date(),
                    userId = user.id,
                    postId = "post1"
                ),
                user = user
            ),
        )

        composeTestRule.setContent {
            InstantTheme {
                CommentScreenContent(
                    comments = comments,
                    onCommentSend = {},
                )
            }
        }

        composeTestRule.onNodeWithText("vince.app Hey").assertIsDisplayed()
    }

}