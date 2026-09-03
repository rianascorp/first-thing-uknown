package com.rianascorp.utils;

import java.io.*;
import java.util.Properties;

    public class ConfigManager {
        private static final String CONFIG_FILE = "config.properties";
        private static final Properties props = new Properties();

        static {
            load();
        }

        public static void load() {
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                props.load(input);
            } catch (IOException e) {
                // Le fichier n'existe pas encore, c'est normal
                System.out.println("Aucun fichier de configuration trouvé, valeurs par défaut utilisées.");
            }
        }

        public static void save() {
            try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
                props.store(output, "Configuration SimpleRun");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String get(String key, String defaultValue) {
            return props.getProperty(key, defaultValue);
        }

        public static void set(String key, String value) {
            props.setProperty(key, value);
        }

        // --- Méthodes spécifiques ---

        public static String getServerIp() {
            return get("server.ip", "localhost");
        }

        public static void setServerIp(String ip) {
            set("server.ip", ip);
            save();
        }

}
