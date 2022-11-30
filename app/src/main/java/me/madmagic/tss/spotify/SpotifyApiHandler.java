package me.madmagic.tss.spotify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import me.madmagic.tss.Config;
import me.madmagic.tss.Logger;
import me.madmagic.tss.Vars;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collections;

public class SpotifyApiHandler {

    private static final String baseUrl = "https://accounts.spotify.com/";
    private static final String fields = "?fields=uri,name,tracks(next,items.track(uri))";
    private static final String scopes = "user-read-playback-state%20user-modify-playback-state";

    public static void checkConfig(Context c) {
        try {
            if (Config.config.refresh == null || Config.config.refresh.isEmpty()) {
                Logger.doLog("something not ok");
                new ApiListener();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SpotifyApiHandler.refreshURL()));
                c.startActivity(browserIntent);
            }
        } catch (Exception e) {
            Logger.doLog(e);
        }
    }

    public static String refreshTokenIfNeededAndGet() throws Exception {
        Config c = Config.config;
        if (tokenExpired()) {
            RequestBody bodyRequestTokens = new FormBody.Builder()
                    .add("grant_type", "refresh_token")
                    .add("refresh_token", c.refresh).build();

            JSONObject response = new JSONObject(ApiSender.api(baseUrl + "api/token", bodyRequestTokens,
                    Collections.singletonList(new ApiSender.Headers("Authorization", getBasicAuth())), false).string());

            c.bearer = response.getString("access_token");
            c.until = LocalDateTime.now(ZoneId.of("CET")).plusSeconds(response.getInt("expires_in"));
            Config.update();
        }
        return c.bearer;
    }

    public static void handleRefreshCode(String code) throws Exception {
        RequestBody bodyRefresh = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("redirect_uri", Vars.redirect)
                .add("code", code).build();

        JSONObject response = new JSONObject(ApiSender.api(baseUrl + "api/token", bodyRefresh,
                Collections.singletonList(new ApiSender.Headers("Authorization", getBasicAuth())), false).string());

        Config c = Config.config;
        c.refresh = response.getString("refresh_token");
        c.bearer = response.getString("access_token");
        c.until = LocalDateTime.now(ZoneId.of("CET")).plusSeconds(response.getInt("expires_in"));
        Config.update();
        Logger.doLog("saved config");
    }

    public static JSONObject getCurrentPlayback() throws Exception {
        return ApiSender.get("https://api.spotify.com/v1/me/player/currently-playing");
    }

    public static JSONObject getPlaylistDetails(String href, boolean isFirst) throws Exception {
        if (isFirst) href += fields; //because the next only includes tracks anyways
        return ApiSender.get(href);
    }

    public static void addToQueue(String uri) throws Exception {
        ApiSender.postBlank("https://api.spotify.com/v1/me/player/queue?uri=" + URLEncoder.encode(uri, StandardCharsets.UTF_8.name()));
    }

    public static void forcePlay(String songUri, String contextUri) throws Exception {
        JSONObject put = new JSONObject()
            .put("offset", new JSONObject()
                .put("uri", songUri))
            .put("context_uri", contextUri)
                    .put("position_ms", 0);
        ApiSender.put("https://api.spotify.com/v1/me/player/play", put);
    }

    public static boolean tokenExpired() {
        Config c = Config.config;
        return c.until == null || c.until.isBefore(LocalDateTime.now());
    }

    public static String refreshURL() {
        return baseUrl + "authorize?&response_type=code&scope=" + scopes + "&redirect_uri=" + Vars.redirect + "&client_id=" + Vars.id;
    }

    private static String getBasicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString((Vars.id + ":" + Vars.secret).getBytes());
    }

    public static String getCurrentPlaylistHref() throws Exception {
        try {
            JSONObject o = SpotifyApiHandler.getCurrentPlayback();
            if (o.isNull("context")) {
                throw new Exception("nsc");
            }
            return o.getJSONObject("context").getString("href");
        } catch (JSONException ignored) {
            throw new Exception("npb");
        } catch (Exception e) {
            Logger.doLog(e);
        }
        return null;
    }
}
