package com.petpaw.models;


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
    private String id;
    private String bio;
    private String email;
    private String phone;
    private String address;
    private String imageURL;
    private List<String> friends;
    private List<String> followers;
    private List<String> following;
    private List<String> posts;
    private List<String> pets;

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

    public User() {

    }

    public User(String id, String username, String email, String phoneNumber, String address) {
        this.id = id;
        this.name = username;
        this.email = email;
        this.phone = phoneNumber;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    public List<String> getPets() {
        return pets;
    }

    public void setPets(List<String> pets) {
        this.pets = pets;
    }
}
