package me.madmagic.tss.spotify;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ApiSender {

    private static OkHttpClient c = new OkHttpClient();

    public static JSONObject get(String url) throws Exception {
        return new JSONObject(api(url, null, Arrays.asList(new Headers("Authorization", "Bearer " + SpotifyApiHandler.refreshTokenIfNeededAndGet())), false).string());
    }

    public static void postBlank(String url) throws Exception {
        api(url, RequestBody.create("", null), Arrays.asList(new Headers("Authorization", "Bearer " + SpotifyApiHandler.refreshTokenIfNeededAndGet())), false);
    }

    public static void put(String url, JSONObject put) throws Exception {
        api(url, RequestBody.create(put.toString(), MediaType.get("application/json")), Arrays.asList(new Headers("Authorization", "Bearer " + SpotifyApiHandler.refreshTokenIfNeededAndGet())), true);
    }

    public static ResponseBody api(String url, RequestBody body, List<Headers> headers, boolean isPut) throws IOException {
        Request.Builder request = new Request.Builder().url(url);

        if (body != null) {
            if (isPut) request.put(body);
            else request.post(body);
        }
        for (Headers header:headers) request.addHeader(header.header, header.value);

        return c.newCall(request.build()).execute().body();
    }

    public static class Headers {
        public String header, value;
        public Headers(String header, String value) {
            this.header = header;
            this.value = value;
        }
    }
}
