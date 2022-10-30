package com.tagalong.controller;


import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.tagalong.dto.GeolocationDto;
import com.tagalong.dto.MatcherDto;
import com.tagalong.dto.OnlineStatus;
import com.tagalong.model.Driver;
import com.tagalong.model.Request;
import com.tagalong.model.repository.DriverRepository;
import com.tagalong.model.repository.RequestRepository;
import com.tagalong.model.repository.UserRepository;
import com.tagalong.model.user.User;

import com.tagalong.utils.LocationComparism;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchDriverPassenger {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private static final Logger logger = LoggerFactory.getLogger(MatchDriverPassenger.class);

    @PostMapping
    public MatcherDto match(@RequestBody GeolocationDto geolocationDto, @RequestHeader("Authorization") String Authorization) throws Exception {
        MatcherDto map = new MatcherDto();

        Optional<User> passengers = this.userRepository.findByEmail(geolocationDto.getEmail());
        User passenger = null;
        if (passengers.isPresent()) {
            passenger = passengers.get();
        }
        System.out.println("here 1");
        List<Driver> driverList = driverRepository.findByOnlineStatusAndIsAvailable(OnlineStatus.ONLINE, Boolean.FALSE);

//        GeoApiContext apiContext = new GeoApiContext();
////AIzaSyBEkQSmMLZV7EmRvQNy2yw9AJmWsVFOcyIdouble
//        apiContext.setApiKey("AIzaSyBEkQSmMLZV7EmRvQNy2yw9AJmWsVFOcyI");

        boolean matchFound = false;

        for (Driver driver : driverList) {


            if (driver.getVehicleSeat() <= 3) {

                LatLng passengerOrig = new LatLng(geolocationDto.getLatitudePassengerFrom(), geolocationDto.getLongitudePassengerFrom());
                LatLng passengerDest = new LatLng(geolocationDto.getLatitudePassengerTo(), geolocationDto.getLongitudePassengerTo());


                LatLng driverOrigin = new LatLng(driver.getLatitudeDriverFrom(), driver.getLongitudeDriverFrom());
                LatLng driverDest = new LatLng(driver.getLatitudeDriverTo(), driver.getLongitudeDriverTo());


                // DirectionsResult directionsResult = DirectionsApi.newRequest(apiContext).mode(TravelMode.DRIVING).origin(driverOrigin).destination(driverDest).await();

                //System.out.println("Route" + directionsResult.routes[0].legs[0].steps[0].duration);
                DirectionsLeg directionsStep = LocationComparism.queryDirections(passengerOrig, driverOrigin);

                long distanceInMeters = directionsStep.distance.inMeters;

                float distanceInKm = (float) (distanceInMeters * 0.001);
                //   Driver driver = null;
                DecimalFormat df = new DecimalFormat("#.0");
                float roundDistance = Float.parseFloat(df.format(distanceInKm));

                if (roundDistance < 3) {

                    //   List<Driver> driverList1 = this.driverRepository.findStoresWithInDistance(geolocationDto.getLatitudePassengerFrom(), geolocationDto.getLongitudePassengerFrom(), roundDistance);
                    // System.out.println("driverList1 " + driverList1.toString());
                    //if (driverList1.isEmpty()) {
                    matchFound = true;
                    map.setMessage("matchFound");
                    map.setDriver(driver);
                    map.setPassenger(passengers);
                    map.setDuration(directionsStep.duration.toString());
                    map.setDistance(directionsStep.distance.toString());
                    // driver.setIsAvailable(Boolean.TRUE);
                    //trip.setStatus("Booked");
                    this.driverRepository.save(driver);
                    Request request = new Request();
                    request.setDriverId(driver.getId());
                    request.setUserId(Objects.requireNonNull(passenger).getId());
                    request.setEmail(passenger.getEmail());
                    request.setLatitudePassengerFrom(geolocationDto.getLatitudePassengerFrom());
                    request.setLatitudePassengerFrom(geolocationDto.getLongitudePassengerFrom());
                    request.setLatitudePassengerTo(geolocationDto.getLatitudePassengerTo());
                    request.setLongitudePassengerFrom(geolocationDto.getLongitudePassengerFrom());
                    request.setStatus("matchFound");
                    this.requestRepository.save(request);
                    // }
                }

                //  Duration duration = directionsStep.duration.;

                /**
                 * check the pickup location of package lies on the drivers route or not
                 */
//                int i = 0;
//
//                double distanceFromStartPoint = 0, distanceFromEndPoint;
//                for (DirectionsStep directionsStep : directionsResult.routes[0].legs[0].steps) {
//
//                    distanceFromStartPoint = distance(passengerOrig.lat, passengerOrig.lng, directionsStep.startLocation.lat, directionsStep.startLocation.lng, "M");
//                    distanceFromEndPoint = distance(passengerOrig.lat, passengerOrig.lng, directionsStep.endLocation.lat, directionsStep.endLocation.lng, "M");
//                    i++;
//                    if (distanceFromStartPoint <= 5 || distanceFromEndPoint <= 5) {
//
//                        matchFound = isMatchFound(passengerDest, directionsResult, i, matchFound);
//
//                    }
//
//                    if (matchFound) {
//                        break;
//                    }
//
//                }

//                if (matchFound) {
//                    map.put("message", "matchFound");
//                    map.put("driver", driver);
//                    map.put("userDetails", passengers);
//                    map.put("duration", directionsStep.duration);
//                    map.put("distance", directionsStep.distance);
//                    trip.setIsAvailable(Boolean.TRUE);
//                    //trip.setStatus("Booked");
//                    this.driverRepository.save(trip);
//                    Request request = new Request();
//                    request.setDriverId(trip.getId());
//                    request.setUserId(Objects.requireNonNull(passenger).getId());
//                    request.setEmail(passenger.getEmail());
//                    request.setLatitudePassengerFrom(geolocationDto.getLatitudePassengerFrom());
//                    request.setLatitudePassengerFrom(geolocationDto.getLongitudePassengerFrom());
//                    request.setLatitudePassengerTo(geolocationDto.getLatitudePassengerTo());
//                    request.setLongitudePassengerFrom(geolocationDto.getLongitudePassengerFrom());
//                    request.setStatus("matchFound");
//                    this.requestRepository.save(request);
//                    break;
//                }
            }


            if (!matchFound) {

                map.setMessage("we'll get back to you when we have a driver");


            }


            {
                logger.error("Exception while calling google API");

            }
        }

        return map;
    }


    /**
     * THis  method returns if the match is found or not
     *
     * @param packagedest
     * @param directionsResult
     * @param i
     * @param matchFound
     * @return
     */
    private boolean isMatchFound(LatLng packagedest, DirectionsResult directionsResult, int i, boolean matchFound) {
        int len = directionsResult.routes[0].legs[0].steps.length;
        for (int j = i; j < len; j++) {
            DirectionsStep remainingSteps = directionsResult.routes[0].legs[0].steps[j];

            double distanceToPackDest = distance(packagedest.lat, packagedest.lng, remainingSteps.startLocation.lat, remainingSteps.startLocation.lng, "M");
            double distanceToPackDest2 = distance(packagedest.lat, packagedest.lng, remainingSteps.endLocation.lat, remainingSteps.endLocation.lng, "M");

            if (distanceToPackDest <= 5 || distanceToPackDest2 <= 5) {
                matchFound = true;
                break;
            }
        }
        return matchFound;
    }


    /**
     * Returns the distance between tow points with latitude and longitude
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /**
     * Converts decimal degrees to radians
     *
     * @param deg
     * @return
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Converts radians to decimal degress
     *
     * @param rad
     * @return
     */
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}


