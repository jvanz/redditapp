package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by vanz on 21/11/14.
 */
public class VoteRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_DIR = "dir";
    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public VoteRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "api/vote" , null, listener, errorListener, params);
    }
}