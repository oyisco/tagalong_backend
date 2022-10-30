package com.tagalong;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@SpringBootApplication
public class TagalongApiApplication {
               //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
    public static void main(String[] args) {
           SpringApplication.run(TagalongApiApplication.class, args);

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

//        LatLng start = new LatLng(50.0011249, 8.2626402);
//        LatLng end = new LatLng(50.0003303, 8.2621764);
//        DirectionsLeg directionsSteps = queryDirections(start, end);
//        Long disImeters = directionsSteps.distance.inMeters;
//
//        float distKm = (float) (disImeters * 0.001);
//        DecimalFormat df = new DecimalFormat("#.0");
//        float number = Float.valueOf(df.format(distKm));
//
//        if (number < 3) {
//            System.out.println("distance Covert" + disImeters);
//            System.out.println("distance REAL " + directionsSteps.duration);
//            //}
//            // System.out.println("distance "+ queryDirections(start, end));
//        }


        ////int dt = distance;
        //  if(directionsSteps.distance=="27 mins") {
        //for(DirectionsStep directionsStep: directionsSteps){
        // System.out.println("directions" + directionsSteps.duration);


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
