package me.madmagic.tss.spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {

    public static final long serialVersionUID = 1L;

    public List<String> uris = new ArrayList<>();
    public String name;

    public void addAll(JSONObject pl) throws Exception {
        JSONArray arr = pl.getJSONObject("tracks").getJSONArray("items");
        for (int i=0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            uris.add(o.getJSONObject("track").getString("uri")); //local track
        }
    }
}
