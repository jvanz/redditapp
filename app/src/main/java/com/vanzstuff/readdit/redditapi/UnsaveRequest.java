package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request used to unsave a link or comment
 * http://www.reddit.com/dev/api#POST_api_unsave
 * Created by vanz on 21/11/14.
 */
public class UnsaveRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public static UnsaveRequest newInstance(String id, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<>();
        if(Utils.stringNotNullOrEmpty(id))
            params.put(PARAM_ID, id);
        return new UnsaveRequest(listener, errorListener, params, accessToken);
    }

    public UnsaveRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/unsave" , null, listener, errorListener, params, accessToken);
    }

}
