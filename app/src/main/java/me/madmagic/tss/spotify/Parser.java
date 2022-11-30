package me.madmagic.tss.spotify;

import me.madmagic.tss.Config;
import org.json.JSONObject;

import java.util.function.Consumer;

public class Parser {

    public static void parse(String href, Consumer<String> feedback) {
        Config c = Config.config;
        Playlist pl;
        if (c.playlists.containsKey(href)) {
            pl = c.playlists.get(href);
            pl.uris.clear();
            feedback.accept("Reparsing existing playlist now...");
        } else {
            pl = new Playlist();
            feedback.accept("Parsing playlist now...");
        }

        try {
            JSONObject playlist = SpotifyApiHandler.getPlaylistDetails(href, true);
            pl.name = playlist.getString("name");

            pl.addAll(playlist);
            while (!playlist.getJSONObject("tracks").isNull("next")) {
                playlist = new JSONObject().put("tracks", SpotifyApiHandler.getPlaylistDetails(playlist.getJSONObject("tracks").getString("next"), false));
                pl.addAll(playlist);
            }
            feedback.accept("Finished parsing playlist");

            c.playlists.put(href, pl);
            Config.update();
        } catch (Exception e) {
            e.printStackTrace();
            feedback.accept("An error occurred");
        }
    }
}
