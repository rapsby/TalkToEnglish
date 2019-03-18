package com.o2o.action.server.repo;

import com.o2o.action.server.db.ProductInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductInfoRepository extends CrudRepository<ProductInfo, Long> {
    List<ProductInfo> findByCategory(long category);
    List<ProductInfo> findByProductId(long prodId);
}
