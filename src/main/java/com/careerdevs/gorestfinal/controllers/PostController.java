package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.validation.PostValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;


@RestController
@RequestMapping("/api/posts")
public class PostController {

        @Autowired
        PostRepository postRepository;

        @GetMapping("/all")
        public ResponseEntity<?> getAllPosts () {
                try{
                Iterable<Post> allPosts = postRepository.findAll();
                return new ResponseEntity<>(allPosts, HttpStatus.OK);

                } catch (Exception e) {
                      return ApiErrorHandling.genericApiError(e);
                }
        }

        @PostMapping("/")
        public ResponseEntity<?> createPost (@RequestBody Post newPost) {

                try {
                        ValidationError errors = PostValidation.validatePost(newPost, postRepository, Boolean.FALSE);
                        if (errors.hasError()) {
                                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());
                        }
                        Post createdPost = postRepository.save(newPost);
                        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);

                } catch (HttpClientErrorException e) {
                        return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
                } catch (Exception e) {
                        return ApiErrorHandling.genericApiError(e);
                }

        }

        @PutMapping("/{id}")
        public Optional<Post>
        updatePost(@RequestBody Post updatePost,
                         @PathVariable(value = "id") long postId)
        {
                //try {
                       return postRepository.findById(postId)
                               .map(post -> {
                               post.setTitle(updatePost.getTitle());
                               post.setBody(updatePost.getBody());
                               return postRepository.save(post);
                               });



                       //return new ResponseEntity<Post>(postRepository.save(updatePost), HttpStatus.OK);
                /*
                } catch (HttpClientErrorException e) {
                        return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
                } catch (Exception e) {
                        return ApiErrorHandling.genericApiError(e);
                }
                */



        }



}
