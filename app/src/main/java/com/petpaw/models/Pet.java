package com.petpaw.models;

import java.util.List;

public class Pet {
    private  String name;
    private final int id;
    private String bio;
    private List<String> images;

    private Pet(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
        this.bio = builder.bio;
        this.images = builder.images;
    }

    public static class Builder {
        private String name;
        private final int id;
        private String bio;
        private List<String> images;

        public Builder(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder images(List<String> images) {
            this.images = images;
            return this;
        }

        public Pet build() {
            return new Pet(this);
        }
    }
}
