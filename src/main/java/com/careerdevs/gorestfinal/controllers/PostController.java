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


        @GetMapping("/{id}")
        public ResponseEntity<?> getOnePost (@PathVariable long postId) {
                //try{
                        Optional<Post> foundPost = postRepository.findById(postId);
                        if (foundPost.isPresent()) {
                                return new ResponseEntity<>(foundPost.get(), HttpStatus.OK);
                        }

                //} catch (Exception e) {
                       // return ApiErrorHandling.genericApiError(e);
                //}
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }



        @PostMapping("/")
        public ResponseEntity<?> create (@RequestBody Post createPost) {

                try {
                        ValidationError errors = PostValidation.validatePost(createPost, postRepository, Boolean.FALSE);
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



        @PutMapping("/{id}")
        public ResponseEntity<?>
        update(@PathVariable(value = "id") long postId,
                   @RequestBody Post updatePost)
        {
                //try {
                       Optional<Post> existingPost = postRepository.findById(postId);
                       if (existingPost.isPresent()) {
                               Post newPostData = existingPost.get();
                               newPostData.setTitle(updatePost.getTitle());
                               newPostData.setBody(updatePost.getBody());
                               return new ResponseEntity<>(newPostData, HttpStatus.OK);
                       }


                /*
                } catch (HttpClientErrorException e) {
                        return (ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());
                } catch (Exception e) {
                        return (ApiErrorHandling.genericApiError(e);
                }
                */
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        }



        @DeleteMapping("/{id}")
        void deletePost(@PathVariable Long postId) {
                postRepository.deleteById(postId);
        }

}
