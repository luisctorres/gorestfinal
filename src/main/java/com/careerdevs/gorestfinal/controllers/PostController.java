package com.careerdevs.gorestfinal.controllers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.utils.BasicUtils;
import com.careerdevs.gorestfinal.validation.PostValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository primaryRepository;

    @Autowired
    private UserRepository secondaryRepository;

    private final String PRIMARY_RESOURCE = "post";
    private final String GO_REST_URI = "https://gorest.co.in/public/v2/"+PRIMARY_RESOURCE+"s";


    @GetMapping("/{id}")
    public ResponseEntity<?> getById (@PathVariable("id") String id) {
        try {

            if (BasicUtils.isStrNaN(id)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");
            }

            long resourceId = Long.parseLong(id);
            Optional<Post> foundResource = primaryRepository.findById(resourceId);

            if (foundResource.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, PRIMARY_RESOURCE +" Not found with ID: " + id);
            }

            return new ResponseEntity<>(foundResource, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById (@PathVariable ("id") String id) {
        try {

            if (BasicUtils.isStrNaN(id)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");
            }

            long resourceId = Long.parseLong(id);
            Optional<Post> foundResource = primaryRepository.findById(resourceId);


            if (foundResource.isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, PRIMARY_RESOURCE + " Not found with ID: " + id);
            }

            primaryRepository.deleteById(resourceId);

            return new ResponseEntity<>(foundResource, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

    @DeleteMapping("deleteall")
    public ResponseEntity<?> deleteAll () {
        try {

            long totalResources = primaryRepository.count();
            primaryRepository.deleteAll();

            return new ResponseEntity<>(PRIMARY_RESOURCE+"s Deleted: " + totalResources, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

    @PostMapping ("upload/{id)")
    public ResponseEntity<?> uploadById (
            @PathVariable ("id") String id,
            RestTemplate restTemplate
    ) {

        try {

            if (BasicUtils.isStrNaN(id)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");
            }

            int resourceId = Integer.parseInt(id);

            String url = GO_REST_URI + "/" + resourceId;

            Post foundResource = restTemplate.getForObject(url, Post.class);

            if (foundResource == null) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, PRIMARY_RESOURCE+" with ID: " + resourceId + "not found");
            }

            Iterable<User> allUser = secondaryRepository.findAll();
            List<User> result = new ArrayList<>();
            allUser.forEach(result::add);
            long randomId = result.get((int) (result.size() * Math.random())).getId();
            foundResource.setUser_id(randomId);

            Post savedResource = primaryRepository.save(foundResource);

            return new ResponseEntity<>(savedResource, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

    @PostMapping ("/")
    public ResponseEntity<?> create ( @RequestBody Post newResourceData) {
        try {

            ValidationError newResourceErrors = PostValidation.validatePost(newResourceData, primaryRepository, secondaryRepository, false);

            if (newResourceErrors.hasError()) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, newResourceErrors.toJSONString());
            }

            Post savedResource = primaryRepository.save(newResourceData);

            return ResponseEntity.ok(savedResource);
        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }


    @PutMapping("/")
    public ResponseEntity<?> update ( @RequestBody Post updateResourceData) {
        try {

            ValidationError updateResourceErrors = PostValidation.validatePost(updateResourceData, primaryRepository, secondaryRepository, true);

            if (updateResourceErrors.hasError()) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, updateResourceErrors.toString());
            }

            Post savedResource = primaryRepository.save(updateResourceData);

            return new ResponseEntity<>(savedResource, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }

    }

    @PostMapping("uploadall")
    public ResponseEntity<?> uploadAll (
            RestTemplate restTemplate
    ) {

        try {


            ResponseEntity<Post[]> response = restTemplate.getForEntity(GO_REST_URI, Post[].class);

            Post[] firstPageData = response.getBody();

            if (firstPageData == null) {
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to GET first page of " + PRIMARY_RESOURCE+"s from GoRest");
            }

            ArrayList<Post> allResourceData = new ArrayList<>(Arrays.asList(firstPageData));

            HttpHeaders responseHeaders = response.getHeaders();

            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-Pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            for (int i = 2; i <= totalPgNum; i++) {
                String pageUrl = GO_REST_URI + "?page=" + i;
                Post[] pageData = restTemplate.getForObject(pageUrl, Post[].class);
                if (pageData == null) {
                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to GET page " + i + " of " +PRIMARY_RESOURCE+"s from GoRest");
                }

                allResourceData.addAll(Arrays.asList(firstPageData));
            }

            Iterable<User> allUser = secondaryRepository.findAll();
            List<User> result = new ArrayList<>();
            allUser.forEach(result::add);
            for (Post resource : allResourceData) {
                long randomId = result.get((int) (result.size() * Math.random())).getId();
                resource.setUser_id(randomId);
            }


            primaryRepository.saveAll(allResourceData);

            return new ResponseEntity<>(PRIMARY_RESOURCE+"s Created: " + allResourceData.size(), HttpStatus.OK);


        } catch (HttpClientErrorException e) {
            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }



    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllData () {
        try {
            Iterable<Post> allDatabaseData = primaryRepository.findAll();
            return new ResponseEntity<>(allDatabaseData, HttpStatus.OK);
        } catch (Exception e) {
            return ApiErrorHandling.genericApiError(e);
        }
    }

}
