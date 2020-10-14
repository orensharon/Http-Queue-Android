package com.orensharon.httpqueue.mock;

import com.orensharon.httpqueue.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Util {


    public static String getInvalidURL() {
        return String.format("%sc/%s", BuildConfig.API_URL, "posts/122");
    }

    public static String getValidURL() {
        return String.format("%s/%s", BuildConfig.API_URL, "posts/1");
    }

    public static String generatePayload() {
        int number = generateNumber();
        if (number == 0) return createEmptyBody();
        if (number == 1) return createNonEmptyBody();
        return createNonEmptyBody();
    }

    public static String createEmptyBody() {
        return new JSONObject().toString();
    }

    public static String createEmptyArrayBody() {
        return new JSONArray().toString();
    }

    public static String createNonEmptyBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("a", "b");
            jsonObject.put("c", 1);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int generateNumber() {
        Random rand = new Random();
        return rand.nextInt(3);
    }
}
