package com.sahil.focuswall;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockerService {

    private static final String HOSTS_FILE_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    // The sites we want to block (redirecting them to localhost)
    private static final String BLOCK_ENTRY = "127.0.0.1 www.facebook.com www.instagram.com www.reddit.com";
    private static final String MARKER = "# FOCUSWALL BLOCK START";

    public void blockWebsites() {
        try {
            Path path = Paths.get(HOSTS_FILE_PATH);
            List<String> lines = Files.readAllLines(path);

            // Avoid double blocking
            if (lines.stream().anyMatch(line -> line.contains(MARKER))) {
                System.out.println("Already blocked.");
                return;
            }

            // Append our block
            String contentToAppend = "\n" + MARKER + "\n" + BLOCK_ENTRY + "\n";
            Files.write(path, contentToAppend.getBytes(), StandardOpenOption.APPEND);
            System.out.println("BLOCKER ACTIVATED: Websites blocked.");

        } catch (IOException e) {
            System.err.println("ERROR: Could not edit hosts file. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void unblockWebsites() {
        try {
            Path path = Paths.get(HOSTS_FILE_PATH);
            List<String> lines = Files.readAllLines(path);

            // Filter out our lines
            List<String> cleanLines = lines.stream()
                    .filter(line -> !line.contains(MARKER) && !line.contains(BLOCK_ENTRY))
                    .collect(Collectors.toList());

            Files.write(path, cleanLines);
            System.out.println("BLOCKER DEACTIVATED: Websites restored.");

        } catch (IOException e) {
            System.err.println("ERROR: Could not clean hosts file. " + e.getMessage());
        }
    }
}