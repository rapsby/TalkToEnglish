package com.o2o.action.server;


import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DBInit {
    @Autowired
    CategoryRepository categoryRepository;

    @PostConstruct
    public void initDB() {
        List<Category> categories = categoryRepository.findByParent(null);
        if (categories.size() == 0) {
            Category category = new Category();
            category.setName("고객센터");
            category.setSynonyms("고객센터");

            categoryRepository.save(category);

            category = new Category();
            category.setName("K쇼핑");

            categoryRepository.save(category);
        }
    }
}