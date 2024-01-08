package com.petpaw.models;

import com.petpaw.interfaces.FirebaseDoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pet implements FirebaseDoc {
    private String name;
    private int age;
    private String breed;
    private int weight;
    private String imageURL;
    private List<String> images;

    public Pet() {
    }

    public Pet(String name, int age, String breed, int weight, String imageURL, List<String> images) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.imageURL = imageURL;
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public Map<String, Object> toDoc() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", name);
        doc.put("age", age);
        doc.put("breed", breed);
        doc.put("weight", weight);
        doc.put("images", images);
        doc.put("imageURL", imageURL);
        return doc;
    }

    //    private Pet(Builder builder) {
//        this.name = builder.name;
//        this.id = builder.id;
//        this.bio = builder.bio;
//        this.images = builder.images;
//    }
//
//    public static class Builder {
//        private String name;
//        private final int id;
//        private String bio;
//        private List<String> images;
//
//        public Builder(String name, int id) {
//            this.name = name;
//            this.id = id;
//        }
//
//        public Builder bio(String bio) {
//            this.bio = bio;
//            return this;
//        }
//
//        public Builder images(List<String> images) {
//            this.images = images;
//            return this;
//        }
//
//        public Pet build() {
//            return new Pet(this);
//        }
//    }
}
