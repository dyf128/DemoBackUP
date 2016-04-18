package com.dmeos.test.androidart.net;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.dmeos.test.androidart.module.Result;
import com.dmeos.test.androidart.utils.NetUtil;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

public class GsonRequest<T extends Result> extends AbstractRequest<T> {
    private final Gson gson = new Gson();
    private Type type;
    private ResponseCallback responseCallback;

    public GsonRequest(String url, Type type, ResponseCallback callback) {
        super(url);
        this.type = type;
        this.responseCallback = callback;
        setListener(listener);
        setErrorListener(errorListener);
    }

    public Type getType() {
        return type;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String json = null;
        try {
            json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Response.error(new ParseError());
        }
        return (Response<T>) Response.success(
                (T) (gson.fromJson(json, getType())),
                HttpHeaderParser.parseCacheHeaders(response));
    }

    private Response.Listener listener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            if (response instanceof Result) {
                responseCallback.onSuccess(((Result) response).code, ((Result) response).message, response);
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            if (response != null) {
                responseCallback.onFailure(response.statusCode, NetUtil.httpRequestErrorMsg(response.statusCode));
            }
        }
    };


}
