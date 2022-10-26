package com.tagalong;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ObjectInputFilter;
import java.util.Arrays;

@SpringBootApplication
public class UberApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UberApiApplication.class, args);

//        double lat1 = 	9.1628777;
//        double lat2 = 9.1424061;
//        double lon1 = 7.3135954;
//        double lon2 = 7.3540112;
//        System.out.println(calculateDistanceBetweenPoints(lat1, lat2,
//                lon1, lon2) + " K.M");
//
//        LatLng passengerOrig = new LatLng(geolocationDto.getLatitudePassengerFrom(), geolocationDto.getLongitudePassengerFrom());
//        LatLng passengerDest = new LatLng(geolocationDto.getLatitudePassengerTo(), geolocationDto.getLongitudePassengerTo());

//current location

        //LatLng start = new LatLng(9.0570752, 7.471104);
      //  LatLng end = new LatLng(9.1424061, 7.3518225);
       // DirectionsLeg directionsSteps = queryDirections(start, end);
      //  Distance distance = directionsSteps.distance;
       // double distanceInMeters = directionsSteps.distance.inMeters;
       // double distance1 = distanceInMeters/101000.0;
        // int dt = distance;
        //if(directionsSteps.distance=="27 mins") {
        // for(DirectionsStep directionsStep: directionsSteps){
      //  System.out.println("directions" + directionsSteps.duration);
      //  System.out.println("distance" + directionsSteps.distance);
        // }
        //   System.out.println("distance "+ queryDirections(start, end));
        //}
    }

    public static DirectionsLeg queryDirections(LatLng start, LatLng end) {
        DirectionsLeg stepsToTake = null;
        GeoApiContext ctx = new GeoApiContext().setApiKey("AIzaSyBEkQSmMLZV7EmRvQNy2yw9AJmWsVFOcyI");
        DirectionsApiRequest request = DirectionsApi.newRequest(ctx).origin(start).destination(end).mode(TravelMode.DRIVING);
        try {
            DirectionsResult result = request.await();
            if (result.routes.length > 0) {
                DirectionsRoute directionsRoute = result.routes[0];
                if (directionsRoute.legs.length > 0) {
                    DirectionsLeg legs = directionsRoute.legs[0];
                    if (legs.steps.length > 0) {
                        stepsToTake = legs;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error("Remote Server Exception", e);
        }
        return stepsToTake;
    }

    //    public static DirectionsStep[] queryDirections(LatLng start, LatLng end) {
//        DirectionsStep[] stepsToTake = null;
//        GeoApiContext ctx = new GeoApiContext().setApiKey("AIzaSyBEkQSmMLZV7EmRvQNy2yw9AJmWsVFOcyI");
//        DirectionsApiRequest request = DirectionsApi.newRequest(ctx).origin(start).destination(end).mode(TravelMode.DRIVING);
//        try {
//            DirectionsResult result = request.await();
//            if (result.routes.length > 0) {
//                DirectionsRoute directionsRoute = result.routes[0];
//                if (directionsRoute.legs.length > 0) {
//                    DirectionsLeg legs = directionsRoute.legs[0];
//                    if (legs.steps.length > 0) {
//                        stepsToTake = legs.steps;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//           // logger.error("Remote Server Exception", e);
//        }
//        return stepsToTake;
//    }
//
    public static double calculateDistanceBetweenPoints(
            double x1,
            double y1,
            double x2,
            double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }


}
