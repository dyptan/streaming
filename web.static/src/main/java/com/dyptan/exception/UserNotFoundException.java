package com.dyptan.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String name){
        super("No such user: "+name);
    }
}
