package com.fittr.server.controller;


import com.fittr.server.model.Trip;
import com.fittr.server.model.UserDataAggregator;
import com.fittr.server.model.Users;
import com.fittr.server.repository.TripRepository;
import com.fittr.server.repository.UserDataAggregatorRepository;
import com.fittr.server.repository.UserRepo;
import com.fittr.server.utility.LevelUtility;
import com.fittr.server.utility.RewardsCalculatorUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ActivityController {

    @Autowired
    TripRepository tripRepository;

    @Autowired
    UserRepo userRepository;

    @Autowired
    UserDataAggregatorRepository userDataAggregatorRepository;


    //Working
    @RequestMapping(
            value = "/user/metrics/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculateCoinsAndDistance(@PathVariable("id") Integer userId) {
        Map<String, Double> metric = new HashMap<>();
        List<Trip> trips = (List<Trip>) tripRepository.getTripDetailsByUserId(userId);
        metric.put("COINS", 0.0);
        metric.put("DISTANCE", 0.0);
        trips.stream().forEach(trip -> {
            metric.put("COINS", (metric.get("COINS") + trip.getCoins()));
            metric.put("DISTANCE", (metric.get("DISTANCE") + trip.getDistanceTravelled()));
        });

        Map<String, Double> levels = LevelUtility.levelCalculator((metric.get("DISTANCE")));
        metric.put("CURRENT_LEVEL", levels.get("CURRENT_LEVEL"));
        metric.put("DISTANCE_FOR_NEXT_LEVEL", levels.get("DISTANCE_FOR_NEXT_LEVEL"));
        return new ResponseEntity<>(metric, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/leaderBoard/{mode}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLeaderBoardData(@PathVariable("mode") String mode) {
        List<UserDataAggregator> tripDetailsByMode = userDataAggregatorRepository.getTripDetailsByMode(mode);
        tripDetailsByMode.forEach(trip -> {
            Optional<Users> user = userRepository.findById(trip.getUserId());
            trip.setName(user.get().getFullName());
            // trip.setName(user.get().getFirstName()); set picture

        });
        return new ResponseEntity<>(tripDetailsByMode, HttpStatus.CREATED);
    }


    @PostMapping(path = "/user/saveTrip", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addUserRideDetails(@RequestBody Trip trip)
            throws Exception {
        trip.setCoins(RewardsCalculatorUtility.computeCoins(trip.getMode(), trip.getDistanceTravelled()));
        try {
            tripRepository.save(trip);
            List<Trip> tripDetailsByUserId = tripRepository.getTripDetailsByUserId(trip.getUserId());
            Double[] tripDistance = new Double[1];
            tripDistance[0] = 0.0;
            tripDetailsByUserId.forEach(trips -> {
                tripDistance[0] = tripDistance[0] + trips.getDistanceTravelled();
            });
            Map<String, Double> level = LevelUtility.levelCalculator(tripDistance[0]);
            Map<String, Integer> metric = new HashMap<>();
            metric.put("COINS", Integer.valueOf(String.valueOf(trip.getCoins())));
            metric.put("LEVEL", Integer.valueOf(String.valueOf(Math.round(level.get("CURRENT_LEVEL")))));


            //Logic to update aggregator table
            userDataAggregator(trip, level);
            return new ResponseEntity<>(metric, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>("Record Insertion Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void userDataAggregator(Trip trip, Map<String, Double> level) {
        List<UserDataAggregator> tripDetailsByUserIdMode = userDataAggregatorRepository.getTripDetailsByUserId(trip.getUserId(), trip.getMode());
        if (tripDetailsByUserIdMode.size() == 0) {
            UserDataAggregator userDataAggregator = new UserDataAggregator();
            userDataAggregator.setUserId(trip.getUserId());
            userDataAggregator.setMode(trip.getMode());
            userDataAggregator.setDistanceTravelled(trip.getDistanceTravelled());
            Map<String, Double> stringDoubleMap = LevelUtility.levelCalculator(trip.getDistanceTravelled());
            userDataAggregator.setLevel(Integer.valueOf(String.valueOf(Math.round(stringDoubleMap.get("CURRENT_LEVEL")))));
            userDataAggregatorRepository.save(userDataAggregator);
        } else {
            UserDataAggregator tripToUpdate = tripDetailsByUserIdMode.get(0);
            tripToUpdate.setDistanceTravelled(tripToUpdate.getDistanceTravelled() + trip.getDistanceTravelled());
            Map<String, Double> stringDoubleMap = LevelUtility.levelCalculator(tripToUpdate.getDistanceTravelled() + trip.getDistanceTravelled());
            tripToUpdate.setLevel(Integer.valueOf(String.valueOf(Math.round(level.get("CURRENT_LEVEL")))));
            userDataAggregatorRepository.save(tripToUpdate);
        }
    }

    @RequestMapping(
            value = "/tripDetails/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTripDetails(@PathVariable("id") Integer userId) {
        List<Trip> trips = tripRepository.getTripDetailsByUserId(userId);
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }
}
