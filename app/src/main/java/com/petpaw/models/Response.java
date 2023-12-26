package com.petpaw.models;

public abstract class Response<T> {
    private Response() {}

    public static final class Success<T> extends  Response<T> {
        private final T value;
        public Success(T value) {
            this.value = value;
        }
        public T getValue() {
            return value;
        }
    }

    public static final class Error<T> extends Response<T> {
        private final Exception exception;
        public Error(Exception exception) {
            this.exception = exception;
        }
        public Exception getException() {
            return exception;
        }
    }

}
