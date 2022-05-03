package com.careerdevs.gorestfinal.repositories;

import com.careerdevs.gorestfinal.models.Comments;
import com.careerdevs.gorestfinal.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface CommentsRepository extends CrudRepository<Comments, Long> {
}
