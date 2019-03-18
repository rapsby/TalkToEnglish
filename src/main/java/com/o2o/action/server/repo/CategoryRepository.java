package com.o2o.action.server.repo;

import com.o2o.action.server.db.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByParent(Long category);
}
