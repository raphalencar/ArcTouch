package com.rba.arctouch.service;

import org.json.JSONObject;

public interface ServiceRequestListener {
    void onResponseObject(JSONObject response);

    void onError(String error);
}
