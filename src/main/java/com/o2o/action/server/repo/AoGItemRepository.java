package com.o2o.action.server.repo;

import com.o2o.action.server.db.AoGItem;
import com.o2o.action.server.db.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AoGItemRepository extends CrudRepository<AoGItem, Long> {
	List<AoGItem> findByCategory(Category category);
}
