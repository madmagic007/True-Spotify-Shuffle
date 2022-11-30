package me.madmagic.tss.spotify;

import android.util.Log;
import me.madmagic.tss.Config;
import me.madmagic.tss.Logger;
import org.json.JSONObject;

import java.util.*;

public class Shuffler {

    private static Playlist pl;
    private static Timer timer;
    private static Boolean added = false;
    private static String lastUri = "";
    private static List<String> alreadyPlayed = new ArrayList<>();

    public static void doShuffle(String href) {
        stopAll();
        Playlist cPl = Config.config.playlists.get(href);
        if (cPl == null) return;
        Logger.doLog("after");
        pl = cPl;

        new Thread(() -> {
            //play a random one forcefully
            try {
                JSONObject plDetails = SpotifyApiHandler.getPlaylistDetails(href, true);
                String uri = plDetails.getString("uri");
                lastUri = getNext();
                SpotifyApiHandler.forcePlay(lastUri, uri);
            } catch (Exception e) {
                Logger.doLog(e);
            }
        }).start();

        timer = new Timer("SongTimer");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    JSONObject curTrack = SpotifyApiHandler.getCurrentPlayback();
                    if (!curTrack.getBoolean("is_playing")) return;

                    JSONObject item = curTrack.getJSONObject("item");

                    if (!item.getString("uri").equals(lastUri)) {
                        added = false;
                        lastUri = item.getString("uri");
                    }

                    int curLength = item.getInt("duration_ms");
                    int curProgress = curTrack.getInt("progress_ms");

                    if (curLength - curProgress < 20000 && !added) { // if less than 20 seconds, add new track
                        added = true;
                        String nextUri = getNext();
                        SpotifyApiHandler.addToQueue(nextUri);
                    }
                } catch (Exception e) {
                    Logger.doLog(e);
                }
            }
        }, 1000, 1000);
    }

    public static void stopAll() {
        pl = null;
        alreadyPlayed.clear();
        if (timer != null) {
            try {
                timer.cancel();
                timer = null;
            } catch (Exception ignored) {}
        }
    }

    private static String getNext() {
        Random r = new Random();
        String id = pl.uris.get(r.nextInt(pl.uris.size()));

        while (alreadyPlayed.contains(id) && id.isEmpty()) id = pl.uris.get(r.nextInt(pl.uris.size()));
        alreadyPlayed.add(id);
        return id;
    }
}
