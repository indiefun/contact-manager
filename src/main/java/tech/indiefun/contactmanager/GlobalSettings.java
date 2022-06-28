package tech.indiefun.contactmanager;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;

public class GlobalSettings {

    private static GlobalSettings instance = null;

    public static synchronized GlobalSettings getInstance() {
        if (instance == null) {
            instance = new GlobalSettings();
        }
        return instance;
    }

    public static class Configurations {
        private String defaultRegionCode = "CN";

        private String defaultTimeZoneID = ZoneId.systemDefault().getId();

        public Configurations() {
        }

        public Configurations(Configurations other) {
            this.defaultRegionCode = other.defaultRegionCode;
        }

        public String getDefaultRegionCode() {
            return defaultRegionCode;
        }

        public void setDefaultRegionCode(String defaultRegionCode) {
            this.defaultRegionCode = defaultRegionCode;
        }

        public String getDefaultTimeZoneID() {
            return defaultTimeZoneID;
        }

        public void setDefaultTimeZoneID(String defaultTimeZoneID) {
            this.defaultTimeZoneID = defaultTimeZoneID;
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File configurationsFile = Path.of(System.getProperty("user.home"), ".contact-manager.json").toFile();
    private Configurations configurations = new Configurations();

    public synchronized void initialize() throws IOException {
        if (configurationsFile.exists()) {
            configurations = objectMapper.readValue(configurationsFile, Configurations.class);
        } else {
            objectMapper.writeValue(configurationsFile, configurations);
        }
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    public synchronized void setConfigurations(Configurations configurations) throws IOException {
        this.configurations = configurations;
        objectMapper.writeValue(configurationsFile, configurations);
    }
}
