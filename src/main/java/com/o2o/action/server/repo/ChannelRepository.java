package com.o2o.action.server.repo;

import com.o2o.action.server.db.Category;
import com.o2o.action.server.db.Channel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChannelRepository extends CrudRepository<Channel, Long> {
    List<Channel> findByChCode(int code);
    List<Channel> findByChName(String name);
}
