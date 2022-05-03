package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;
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

        @Autowired
        UserRepository userRepository;


        @GetMapping("/all")
        public ResponseEntity<?> getAllPosts () {

                try{
                Iterable<Post> allPosts = postRepository.findAll();
                return new ResponseEntity<>(allPosts, HttpStatus.OK);

                } catch (Exception e) {
                      return ApiErrorHandling.genericApiError(e);
                }
        }


        @GetMapping("/{id}")
        public ResponseEntity<?> getOnePost (@PathVariable long postId) {
                try{
                        Optional<Post> foundPost = postRepository.findById(postId);
                        if (foundPost.isPresent()) {
                               return new ResponseEntity<>(foundPost.get(), HttpStatus.OK);
                        } else {
                                return ApiErrorHandling.customApiError("Post now with found with id " + postId, HttpStatus.NOT_FOUND);
                        }


                } catch (Exception e) {
                        return ApiErrorHandling.genericApiError(e);
                }

        }



        @PostMapping("/")
        public ResponseEntity<?> create (@RequestBody Post createPost) {

                try {
                        ValidationError errors = PostValidation.validatePost(createPost, postRepository, userRepository, false);
                        if (errors.hasError()) {
                                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());
                        }
                        Post createdPost = postRepository.save(createPost);
                        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);

                } catch (HttpClientErrorException e) {
                        return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
                } catch (Exception e) {
                        return ApiErrorHandling.genericApiError(e);
                }

        }



        @PutMapping("/")
        public ResponseEntity<?>
        update(@RequestBody Post updatePost)
        {
                try {
                        ValidationError errors = PostValidation.validatePost(updatePost, postRepository, userRepository,true);
                        if (errors.hasError()) {
                                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());
                        }


                       Optional<Post> existingPost = postRepository.findById(updatePost.getId());
                       if (existingPost.isPresent()) {
                               Post newPostData = existingPost.get();
                               newPostData.setUser_id(updatePost.getUser_id());
                               newPostData.setTitle(updatePost.getTitle());
                               newPostData.setBody(updatePost.getBody());
                               postRepository.save(newPostData);
                               return new ResponseEntity<>(newPostData, HttpStatus.OK);
                       } else {
                               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                       }


                } catch (HttpClientErrorException e) {
                        return (ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode()));
                } catch (Exception e) {
                        return (ApiErrorHandling.genericApiError(e));
                }




        }


        //delete should return the resource that was deleted
        //get the post and store it in variable the delete it from the repository
        @DeleteMapping("/{id}")
        ResponseEntity<?> deletePost(@PathVariable Long postId) {

                try{
                        Optional<Post> foundPost = postRepository.findById(postId);
                        if (foundPost.isPresent()) {
                                postRepository.deleteById(postId);
                                return new ResponseEntity<>(foundPost.get(), HttpStatus.OK);
                        } else {
                                return ApiErrorHandling.customApiError("Post now with found with id " + postId, HttpStatus.NOT_FOUND);
                        }


                } catch (Exception e) {
                        return ApiErrorHandling.genericApiError(e);
                }



        }

}
