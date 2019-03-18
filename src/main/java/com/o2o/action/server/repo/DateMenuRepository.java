package com.o2o.action.server.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.o2o.action.server.db.MealMenu;

public interface DateMenuRepository extends CrudRepository<MealMenu, Long> {
	List<MealMenu> findByServiceDate(Date date);
	List<MealMenu> findByServiceDateBetweenOrderByServiceDateAsc(Date start, Date end);
}
