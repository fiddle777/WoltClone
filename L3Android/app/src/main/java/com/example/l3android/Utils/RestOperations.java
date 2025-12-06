package com.example.l3android.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RestOperations {

    public static String sendGet(String urlGet) throws IOException {
        URL url = new URL(urlGet);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        int code = httpURLConnection.getResponseCode();
        System.out.println("Resonse code get " + code);

        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        } else {
            return "Error";
        }
    }

    public static String sendDelete(String urlDelete) throws IOException {
        URL url = new URL(urlDelete);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("DELETE");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        int code = httpURLConnection.getResponseCode();
        System.out.println("Resonse code get " + code);

        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        } else {
            return "Error";
        }
    }

    public static String sendPost(String urlString, String jsonPayload) throws IOException {
        System.out.println("POST URL: " + urlString);
        System.out.println("Payload: " + jsonPayload);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            byte[] out = jsonPayload.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(out.length);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            int status = conn.getResponseCode();
            System.out.println("Response code get " + status);

            InputStream is = (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream();
            String body = readStream(is);
            System.out.println("Response body: " + body);

            if (status >= 200 && status < 300) {
                return body == null ? "" : body;
            } else {
                return "Error";
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    private static String readStream(InputStream is) throws IOException {
        if (is == null) return null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public static String sendPut(String urlPut, String postDataParams) throws IOException {
        URL url = new URL(urlPut);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);

        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        bufferedWriter.write(postDataParams);
        bufferedWriter.close();
        outputStream.close();

        int code = httpURLConnection.getResponseCode();
        System.out.println("Resonse code get " + code);

        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        } else {
            return "Error";
        }
    }
}
