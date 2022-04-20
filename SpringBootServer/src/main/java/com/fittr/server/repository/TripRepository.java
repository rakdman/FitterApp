package com.fittr.server.repository;

import com.fittr.server.model.Trip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TripRepository <P> extends CrudRepository<Trip, Long> {

    @Query(value="select * from TRIP where USER_ID = :userId", nativeQuery=true)
    List<Trip> getTripDetailsByUserId(Integer userId);

    @Query(value="select * from TRIP where MODE_TYPE = :modeType", nativeQuery=true)
    List<Trip> getTripByMode(String modeType);


}
