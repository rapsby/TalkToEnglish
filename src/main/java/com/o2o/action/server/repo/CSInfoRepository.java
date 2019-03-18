package com.o2o.action.server.repo;

import com.o2o.action.server.db.CustomerServiceInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CSInfoRepository extends CrudRepository<CustomerServiceInfo, Long> {
	List<CustomerServiceInfo> findByKeyword(String keyword);
}
