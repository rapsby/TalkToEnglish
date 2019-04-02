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
    public static final String KEYCODE_SHOPPING_ROOT = "shoppingroot";
    public static final String KEYCODE_BRODCAST_CHANNEL = "channelroot";
   //public static final String KEYCODE_XIAOMI_SHOP_ROOT = "xaiomishop";

    @Autowired
    CategoryRepository categoryRepository;

    @PostConstruct
    public void initDB() {

    }
}