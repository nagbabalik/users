package com.tamj.secure.thread;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExpireDate extends AsyncTask<String, Void, String> {

    private static final String TAG = "ExpireDate";

    // 🔐 MUST MATCH PANEL
    private static final String APP_TOKEN = "TAMJ_APP_2026_SECRET";

    private final String URL_JSON;
    private final ExpireDateListener listener;

    public interface ExpireDateListener {
        void onExpireDate(String expire_date);
        void onDeviceNotMatch();
        void onAuthFailed();
        void onError();
    }

    public ExpireDate(String URL_JSON, ExpireDateListener listener) {
        this.URL_JSON = URL_JSON;
        this.listener = listener;
    }

    public void start() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, URL_JSON);
    }

    @Override
protected String doInBackground(String... params) {
    HttpURLConnection connection = null;
    try {
        URL url = new URL(params[0]);
        connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("GET");

        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "TamJVPN");
        connection.setRequestProperty("X-APP-TOKEN", APP_TOKEN);

        int code = connection.getResponseCode();

        InputStream input;
        if (code >= 200 && code < 300) {
            input = connection.getInputStream();
        } else {
            input = connection.getErrorStream();
        }

        if (input == null) {
            Log.e(TAG, "HTTP " + code + " no response body");
            return null;
        }

        Reader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int read;
        while ((read = reader.read(buf)) != -1) {
            sb.append(buf, 0, read);
        }

        String raw = sb.toString();
        Log.d(TAG, "HTTP " + code + " RESPONSE: " + raw);
        return raw;

    } catch (Exception e) {
        Log.e(TAG, "Network error", e);
        return null;
    } finally {
        if (connection != null) {
            connection.disconnect();
        }
    }
}

    @Override
    protected void onPostExecute(String result) {

        if (listener == null) {
            Log.e(TAG, "Listener is NULL");
            return;
        }

        if (result == null || result.trim().isEmpty()) {
            listener.onError();
            return;
        }

        try {
            JSONObject js = new JSONObject(result);
            String status = js.optString("status", "");

            // 🔥 FIXED STATUS HANDLING
            switch (status) {

                case "active":
    String expiry = js.optString("expiry", "");
    if (expiry.isEmpty()) expiry = js.optString("expire_date", "");
    if (expiry.isEmpty()) expiry = js.optString("exp", "");

    if (expiry.isEmpty() && js.has("data")) {
        JSONObject data = js.optJSONObject("data");
        if (data != null) {
            expiry = data.optString("expiry", "");
            if (expiry.isEmpty()) expiry = data.optString("expire_date", "");
            if (expiry.isEmpty()) expiry = data.optString("exp", "");
        }
    }

    if (expiry.isEmpty()) expiry = "none";
    listener.onExpireDate(expiry);
    break;

                case "expired":
                    listener.onExpireDate("none");
                    break;

                case "invalid":
                case "auth_failed":
                case "forbidden":
                    listener.onAuthFailed();
                    break;

                case "device_not_match":
                    listener.onDeviceNotMatch();
                    break;

                default:
                    Log.e(TAG, "Unknown response: " + result);
                    listener.onError();
                    break;
            }

        } catch (Exception e) {
            Log.e(TAG, "JSON parse error: " + result, e);
            listener.onError();
        }
    }
}