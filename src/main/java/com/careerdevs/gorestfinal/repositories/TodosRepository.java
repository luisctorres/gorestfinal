package com.careerdevs.gorestfinal.repositories;

import com.careerdevs.gorestfinal.models.Todos;
import org.springframework.data.repository.CrudRepository;

public interface TodosRepository extends CrudRepository<Todos, Long> {
}
