import * as functions from "firebase-functions";
import { initializeApp } from "firebase-admin/app";
import { firestore } from "firebase-admin";
import { CallableContext } from "firebase-functions/v1/https";

initializeApp();

const builder = functions.region("europe-west1");

interface UserInfo {
  userId: string
  imageUrl: string
  name: string
  username: string
}

const helper = {
  auth(context: CallableContext): string {
    if (!context.auth) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new functions.https.HttpsError("failed-precondition", "The function must be called while authenticated.");
    }
    return context.auth.uid;
  },

  argument(arg: string, errorMessage: string): string {
    if (!(typeof arg === "string") || arg.length === 0) {
      // Throwing an HttpsError so that the client gets the error details.
      throw new functions.https.HttpsError("invalid-argument", errorMessage);
    }
    return arg;
  },

  async userInfo(userId: string): Promise<UserInfo | undefined> {
    const userRef = firestore().collection("users").doc(userId);

    // Get user info
    const userInfoSnap = await userRef.get();
    const userInfo = userInfoSnap.data();

    // Check if user info are valid
    if (!userInfo || !userInfo.imageUrl || !userInfo.name || !userInfo.username) {
      return undefined
    }

    return {
      userId: userId,
      name: userInfo.name,
      username: userInfo.username,
      imageUrl: userInfo.imageUrl,
    }
  },
};

/**
 * Create user account in the database
 *
 * @param data.name - The user name
 * @param data.username - The user username
 * @param data.imageUrl - The user profile image url
 * @returns The id of the user
 */
export const createAccount = builder.https.onCall(async (data, context) => {
  // Checking that the user is authenticated.
  const uid = helper.auth(context)

  // Checking attributes
  const name = helper.argument(data.name, "The function must be called with the `data.name` of the user.")
  const username = helper.argument(data.username, "The function must be called with the `data.username` of the user.")
  const imageUrl = helper.argument(data.imageUrl, "The function must be called with the `data.imageUrl`.")

  await firestore().collection("users").doc(uid).set({
    name,
    username,
    imageUrl,
  })

  return uid
})

/**
 * Publish a new post
 *
 * @param data - The image url of the post
 * @returns The id of the new post document
 */
export const publishPost = builder.https.onCall(async (data, context) => {
  // Checking that the user is authenticated.
  const uid = helper.auth(context)

  // Checking attribute.
  const imageUrl = helper.argument(data, "The function must be called with the image url.")

  // Get user info
  const userInfo = await helper.userInfo(uid)
  if (userInfo === undefined) {
    throw new functions.https.HttpsError("failed-precondition", "The user info are invalid")
  }

  // Add the new post
  const postRef = await firestore().collection("posts").add({
    createTimestamp: firestore.FieldValue.serverTimestamp(),
    user: userInfo,
    imageUrl: imageUrl,
    published: true,
  })

  return postRef.id
})

/**
 * Comment a post
 *
 * @param data.postId - The post id where the comment is published
 * @param data.content - The message of the comment
 * @returns The id of the comment document
 */
export const comment = builder.https.onCall(async (data, context) => {
  // Checking that the user is authenticated.
  const uid = helper.auth(context)

  // Checking attribute.
  const postId = helper.argument(data.postId, "The function must be called with the `data.postId`.")
  const content = helper.argument(data.content, "The function must be called with the `data.content`.")

  // Get user info
  const userInfo = await helper.userInfo(uid)
  if (userInfo === undefined) {
    throw new functions.https.HttpsError("failed-precondition", "The user info are invalid")
  }

  // Add the comment
  const commentRef = await firestore().collection("posts").doc(postId).collection("comments").add({
    createTimestamp: firestore.FieldValue.serverTimestamp(),
    userId: uid,
    content: content,
  })

  return commentRef.id
})

/**
 * Like a post
 *
 * @param data - The post id to be like
 * @returns The id of the new like document
 */
export const like = builder.https.onCall(async (data, context) => {
  // Checking that the user is authenticated.
  const uid = helper.auth(context);

  // Checking attribute.
  const postId = helper.argument(data, "The function must be called with the id of the post to be like.");

  // Here is the post ref at posts/{postId}/likes/{uid}
  const likeRef = firestore().collection("posts").doc(postId).collection("likes").doc(uid);

  functions.logger.log("Like", uid, postId);

  // Add the like. We merge if the like already exists.
  await likeRef.set({
    createTimestamp: firestore.FieldValue.serverTimestamp(),
    userId: uid,
  }, { merge: true });

  // Returns the like doc id
  return uid;
});

/**
 * Remove like from a post
 *
 * @param data - The post id to remove the like
 * @returns true when it succeed
 */
export const removeLike = builder.https.onCall(async (data, context) => {
  // Checking that the user is authenticated.
  const uid = helper.auth(context)

  // Checking attribute.
  const postId = helper.argument(data, "The function must be called with the id of the post.")

  // Ref
  const likeRef = firestore().collection("posts").doc(postId).collection("likes").doc(uid)

  // Remove like
  functions.logger.log("Remove like", uid, postId);
  await likeRef.delete()

  return true
})

/**
 * When a new like is added, check if it can exists
 * and then add user info
 *
 * Check if:
 * - The doc where the like is created exists
 * - The user is valid
 *
 * Add this data
 * - user { id, imageUrl, name, username }
 * - published: true
 *
 * The `published` field can be use by the client to know
 * that the like document is usable for display
 */
export const addLikeInfo = builder.firestore.document("/posts/{postId}/likes/{likeId}")
  .onCreate(async (snap, context) => {
    const userId = snap.data().userId;
    const postId = context.params.postId;
    const likeId = context.params.likeId;

    const postRef = firestore().collection("posts").doc(postId);
    const likeRef = postRef.collection("likes").doc(likeId);

    // Check if the post exists
    const postDoc = await postRef.get();
    if (!postDoc.exists) {
      await likeRef.delete();
      return functions.logger.error("Delete the like because the post doesn't exists.", postId, userId, likeId);
    }

    const userInfo = await helper.userInfo(userId)
    if (userInfo === undefined) {
      await likeRef.delete();
      return functions.logger.error("Delete the like because the user info are invalid.", postId, userId, likeId, userInfo);
    }

    // Add userinfo and valid field
    await likeRef.update({
      user: userInfo,
      published: true, // tell the client it can display this like with the new fresh user info
    });
  });

/**
 * When a new comment is added, check if it can exists
 * and then add user info
 *
 * Check if:
 * - The doc where the comment is created exists
 * - The user is valid
 *
 * Add this data
 * - user { id, imageUrl, name, username }
 * - published: true
 *
 * The `published` field can be use by the client to know
 * that the comment document is usable for display
 */
export const addCommentInfo = builder.firestore.document("/posts/{postId}/comments/{commentId}")
  .onCreate(async (snap, context) => {
    const userId = snap.data().userId
    const postId = context.params.postId
    const commentId = context.params.commentId

    const postRef = firestore().collection("posts").doc(postId)
    const commentRef = firestore().collection("comments").doc(commentId)

    const postDoc = await postRef.get()
    if (!postDoc.exists) {
      await commentRef.delete();
      return functions.logger.error("Delete the comment because the post doesn't exists.", postId, userId, commentId);
    }

    const userInfo = await helper.userInfo(userId)
    if (userInfo === undefined) {
      await commentRef.delete();
      return functions.logger.error("Delete the comment because the user info are invalid.", postId, userId, commentId, userInfo);
    }

    await commentRef.update({
      user: userInfo,
      published: true, // Tell the client that this comment is published
    })
  })
