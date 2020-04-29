package com.example.springboot.content.guidance;

import org.json.JSONObject;

public class StateMachineUtils {

    public static String getToken(String incomingMessage) {
        JSONObject obj = new JSONObject(incomingMessage);
        return obj.getString("token");
    }
}
