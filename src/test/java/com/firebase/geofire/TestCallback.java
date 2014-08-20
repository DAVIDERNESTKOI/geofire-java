package com.firebase.geofire;

import com.firebase.client.FirebaseError;
import com.firebase.geofire.core.SimpleFuture;
import org.junit.Assert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestCallback implements LocationCallback {

    private final SimpleFuture<String> future = new SimpleFuture<String>();

    public static String location(String key, double latitude, double longitude) {
        return String.format("LOCATION(%s,%f,%f)", key, latitude, longitude);
    }

    public static String noLocation(String key) {
        return String.format("NO_LOCATION(%s)", key);
    }

    public String getCallbackValue() throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(TestHelpers.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void onLocationResult(String key, boolean hasLocation, double latitude, double longitude) {
        if (future.isDone()) {
            throw new IllegalStateException("Already received callback");
        }
        if (hasLocation) {
            future.put(location(key, latitude, longitude));
        } else {
            future.put(noLocation(key));
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Assert.fail("Firebase synchronization failed: " + firebaseError);
    }
}
