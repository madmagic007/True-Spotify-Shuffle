package me.madmagic.tss.spotify;

import fi.iki.elonen.NanoHTTPD;
import me.madmagic.tss.Logger;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ApiListener extends NanoHTTPD {

    public ApiListener() throws Exception {
        super(Logger.port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, List<String>> params = session.getParameters();

        if (params.keySet().contains("code")) {
            try {
                SpotifyApiHandler.handleRefreshCode(params.get("code").get(0));
            } catch (Exception e) {
                return newFixedLengthResponse("Invalid request");
            }
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    stop();
                }
            }, 1000);
            return newFixedLengthResponse("Spotify account validated, you may close this page");
        }
        return newFixedLengthResponse("Invalid request");
    }
}
