package com.fittr.server.repository;

import com.fittr.server.model.Trip;
import com.fittr.server.model.UserDataAggregator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDataAggregatorRepository <P> extends CrudRepository<UserDataAggregator, Long> {

    @Query(value="select * from USERDATA_AGGREGATOR  where USER_ID = :userId AND MODE_TYPE = :modeType", nativeQuery=true)
    List<UserDataAggregator> getTripDetailsByUserId(Integer userId,String modeType);

    @Query(value="select * from USERDATA_AGGREGATOR  where  MODE_TYPE = :modeType ORDER BY DISTANCE_TRAVELLED DESC", nativeQuery=true)
    List<UserDataAggregator> getTripDetailsByMode(String modeType);
}
