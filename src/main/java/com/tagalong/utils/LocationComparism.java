package com.tagalong.utils;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;

public class LocationComparism {
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
}
