package com.rianascorp.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IpFile {
    private static final Path FILE = Paths.get(
            System.getProperty("user.home"),
            ".peas", "ip-addresses.txt");   // hidden folder in home

    private IpFile() {}

    /** Load every non-empty line → List<String> */
    public static List<String> load() {
        if (!Files.exists(FILE)) return new ArrayList<>();
        try (var lines = Files.lines(FILE)) {
            return lines
                    .map(String::strip)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Overwrite the file with the current list */
    public static void save(Collection<String> ips) {
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, ips, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
