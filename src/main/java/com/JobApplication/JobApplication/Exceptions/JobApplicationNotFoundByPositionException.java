package com.JobApplication.JobApplication.Exceptions;

public class JobApplicationNotFoundByPositionException extends ResourceNotFound {
    public JobApplicationNotFoundByPositionException(String message) {
        super(message);
    }
}
