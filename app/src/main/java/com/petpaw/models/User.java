package com.petpaw.models;



import org.w3c.dom.Comment;

import java.util.List;

interface UserInterface {

    /*POST MANAGEMENT*/
    void createPost(Post newPost);
    void deletePost(String postId);
    void editPost(String postId, Post newPost);
    void commentPost(String postId, PostComment comment);
    void likePost(String postId);
    List<Post> searchPosts(FilterBy filterBy);

    /*FRIEND AND SOCIAL MANAGEMENT*/
    void addFriend(User newFriend);
    void removeFriend(User friend);
    List<User> searchFriends(FilterBy filterBy);
    void follow(User user);
    void unfollow(User user);

    /*PET PROFILE MANAGEMENT*/
    void addPet(Pet newPet);
    void removePet(String petId);
    void editPet(String petId, Pet pet);
    List<Pet> searchPets(FilterBy filterBy);
    void addPetToPost(String postId, Pet pet);
    void removePetFromPost(String postId, Pet pet);
}
public class User implements UserInterface {
    private String name;
    private final int id;
    private String bio;
    private String email;
    private String phone;
    private String address;
    private String imageURL;
    private List<User> friends;
    private List<User> followers;
    private List<User> following;
    private List<Post> posts;
    private List<Pet> pets;

    private User(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
        this.bio = builder.bio;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
        this.imageURL = builder.imageURL;
        this.friends = builder.friends;
        this.followers = builder.followers;
        this.following = builder.following;
        this.posts = builder.posts;
        this.pets = builder.pets;
    }

    @Override
    public void createPost(Post newPost) {

    }

    @Override
    public void deletePost(String postId) {

    }

    @Override
    public void editPost(String postId, Post newPost) {

    }

    @Override
    public void commentPost(String postId, PostComment comment) {

    }

    @Override
    public void likePost(String postId) {

    }

    @Override
    public List<Post> searchPosts(FilterBy filterBy) {
        return null;
    }

    @Override
    public void addFriend(User newFriend) {

    }

    @Override
    public void removeFriend(User friend) {

    }

    @Override
    public List<User> searchFriends(FilterBy filterBy) {
        return null;
    }

    @Override
    public void follow(User user) {

    }

    @Override
    public void unfollow(User user) {

    }

    @Override
    public void addPet(Pet newPet) {

    }

    @Override
    public void removePet(String petId) {

    }

    @Override
    public void editPet(String petId, Pet pet) {

    }

    @Override
    public List<Pet> searchPets(FilterBy filterBy) {
        return null;
    }

    @Override
    public void addPetToPost(String postId, Pet pet) {

    }

    @Override
    public void removePetFromPost(String postId, Pet pet) {

    }

    public static class Builder {
        private final int id;
        private String name;
        private String bio;
        private String email;
        private String phone;
        private String address;
        private String imageURL;
        private List<User> friends;
        private List<User> followers;
        private List<User> following;
        private List<Post> posts;
        private List<Pet> pets;

        public Builder(int id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder imageURL(String imageURL) {
            this.imageURL = imageURL;
            return this;
        }

        public Builder friends(List<User> friends) {
            this.friends = friends;
            return this;
        }

        public Builder followers(List<User> followers) {
            this.followers = followers;
            return this;
        }

        public Builder following(List<User> following) {
            this.following = following;
            return this;
        }

        public Builder posts(List<Post> posts) {
            this.posts = posts;
            return this;
        }

        public Builder pets(List<Pet> pets) {
            this.pets = pets;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
