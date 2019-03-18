package com.o2o.action.server.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.o2o.action.server.db.MealDetail;

public interface MealDetailRepository extends CrudRepository<MealDetail, Long> {
}
