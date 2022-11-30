package me.madmagic.tss;

import me.madmagic.tss.spotify.Playlist;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Config implements Serializable {
    static final long serialVersionUID = 1L;

    public static Config config;
    public static File configFile;

    public Map<String, Playlist> playlists = new HashMap<>();
    public String bearer;
    public String refresh;
    public LocalDateTime until;

    public static void init(File path) {
        configFile = new File(path, "TSS.mcfg");
        try {
            if (!configFile.exists()) configFile.createNewFile();
            byte[] data = Files.readAllBytes(configFile.toPath());
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            config = (Config) ois.readObject();
            bais.close();
            ois.close();
        } catch (Exception e) {
            Logger.doLog(e);
            config = new Config();
            update();
        }
    }

    public static void update() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);
             FileOutputStream fos = new FileOutputStream(configFile)) {
            oos.writeObject(config);
            fos.write(baos.toByteArray());
        } catch (Exception ignored) {}
    }
}
