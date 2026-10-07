package com.JobApplication.JobApplication.Exceptions;

public class UserNotFoundException extends ResourceNotFound {
    public UserNotFoundException(String message) {
        super(message);
    }
}
