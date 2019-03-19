package com.o2o.action.server;


import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DBInit {
    public static final String KEYCODE_CUSTOMER_SERVICE_ROOT = "cserviceroot";
    public static final String KEYCODE_K_SHOPPINT_ROOT = "kshoppingroot";
    public static final String KEYCODE_XIAOMI_SHOP_ROOT = "xaiomishop";

    @Autowired
    CategoryRepository categoryRepository;

    @PostConstruct
    public void initDB() {
        List<Category> categories = categoryRepository.findByParentOrderByDispOrderAsc(null);
        if (categories.size() == 0) {
            Category category = new Category();
            category.setKeycode(KEYCODE_CUSTOMER_SERVICE_ROOT);
            category.setTitle("고객센터");
            category = categoryRepository.save(category);

            category = new Category();
            category.setKeycode(KEYCODE_K_SHOPPINT_ROOT);
            category.setTitle("K 쇼핑");
            categoryRepository.save(category);

            category = new Category();
            category.setKeycode(KEYCODE_XIAOMI_SHOP_ROOT);
            category.setTitle("샤오미 미니샵");
            categoryRepository.save(category);
        }
    }
}