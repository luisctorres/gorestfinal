package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.PostValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;

public class UserController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;




    @PostMapping("/")
    public ResponseEntity<?> create (@RequestBody User createUser) {

        try {
            ValidationError errors = PostValidation.validatePost(createUser, postRepository, userRepository, false);
            if (errors.hasError()) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());
            }
            User createdUser = userRepository.save(createUser);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }




}
