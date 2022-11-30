package me.madmagic.tss;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import me.madmagic.tss.spotify.Parser;
import me.madmagic.tss.spotify.SpotifyApiHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.listPl);

        Config.init(getFilesDir());
        doList();
        SpotifyApiHandler.checkConfig(this);

        findViewById(R.id.btnAdd).setOnClickListener(l -> {
            new Thread(() -> {
                try {
                    String href = SpotifyApiHandler.getCurrentPlaylistHref();
                    if (href == null) {
                        setFeedback("An error occurred");
                        return;
                    }
                    Logger.doLog(href);

                    if (Config.config.playlists.containsKey(href)) {
                        setFeedback("Playlist already added.\nConsider reparsing instead.");
                        return;
                    }
                    setFeedback("Got playlist, parsing now...");
                    Parser.parse(href, this::setFeedback);
                    doList();

                } catch (Exception e) {
                    Logger.doLog(e);
                    switch (Objects.requireNonNull(e.getMessage())) {
                        case "nsc":
                            setFeedback("Current playback is from a non shuffle-able context");
                            break;
                        case "npb":
                            setFeedback("No spotify playback detected");
                            break;
                    }
                }
            }).start();
        });

        findViewById(R.id.btnReparse).setOnClickListener(l -> {
            if (spinner.getSelectedItem() == null) {
                setFeedback("No playlist selected.\nConsider adding current first.");
                return;
            }

            String[] at = new String[1];
            Config.config.playlists.forEach((str, pl) -> {
                if (!pl.name.equals(spinner.getSelectedItem())) return;
                at[0] = str;
            });

            String href = at[0];
            if (href == null) {
                setFeedback("Selected playlist not found somehow");
                return;
            }

            new Thread(() -> {
                Parser.parse(href, this::setFeedback);
                doList();
            }).start();
        });

        findViewById(R.id.btnRemove).setOnClickListener(l -> {
            int index = spinner.getSelectedItemPosition();
            String href = new ArrayList<>(Config.config.playlists.keySet()).get(index);
            Config.config.playlists.remove(href);
            Config.update();
            doList();
        });

        findViewById(R.id.btnShuffle).setOnClickListener(l -> {
            int index = spinner.getSelectedItemPosition();
            String href = new ArrayList<>(Config.config.playlists.keySet()).get(index);

            Intent i = new Intent(this, ShufflerService.class);
            i.putExtra("href", href);
            startForegroundService(i);
        });
    }

    private void doList() {
        runOnUiThread(() -> {
            List<String> list = new ArrayList<>();
            Config.config.playlists.values().forEach(pl -> list.add(pl.name));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        });
    }

    private void setFeedback(String text) {
        runOnUiThread(() -> {
            TextView tv = findViewById(R.id.txtFeedback);
            tv.setText(text);
        });
    }
}