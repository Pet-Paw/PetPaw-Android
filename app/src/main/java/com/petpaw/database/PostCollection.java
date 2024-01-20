package com.petpaw.database;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petpaw.models.Post;
import com.petpaw.models.User;

import java.util.List;

public class PostCollection {
    public interface Callback {
        void onCallback(List<Post> postList);
        void onCallback(Post post);
    }

    private static final String COLLECTION_NAME = "POSTS";
    private static PostCollection instance;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference postsCollectionReference;

    public static PostCollection newInstance() {
        if (instance == null) {
            instance = new PostCollection();
        }
        return instance;
    }

    private PostCollection() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        postsCollectionReference = firebaseFirestore.collection(COLLECTION_NAME);
    }

    public void createPost(Post post) {
        postsCollectionReference.add(post);
    }

    public void getAllPosts(Callback callback) {
        postsCollectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> postList = task.getResult().toObjects(Post.class);
                callback.onCallback(postList);
            } else {
                System.out.println("Error getting documents: " + task.getException());
                throw new RuntimeException(task.getException());
            }
        });
    }

    public void getPostById(String postId, Callback callback) {
        postsCollectionReference.document(postId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String tId = postId;
                Post post = task.getResult().toObject(Post.class);
                Post postTemp = new Post(post.getAuthorId(), post.getDateModified(), post.getContent(), post.isModified(), post.getImageURL(), post.getLikes(), post.getComments(), post.getPostId(), post.getTags(), post.getPetIdList(), post.getCommunityId());
                callback.onCallback(postTemp);
            } else {
                System.out.println("Error getting documents: " + task.getException());
                throw new RuntimeException(task.getException());
            }
        });
    }

    public void getPostByAuthorId(String authorId, Callback callback) {
        postsCollectionReference.whereEqualTo("authorId", authorId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> postList = task.getResult().toObjects(Post.class);
                callback.onCallback(postList);
            } else {
                System.out.println("Error getting documents: " + task.getException());
                throw new RuntimeException(task.getException());
            }
        });
    }


}
