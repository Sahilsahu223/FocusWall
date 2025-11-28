package com.sahil.focuswall;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class AppKillerService {

    // Apps to kill (Case insensitive)
    private static final List<String> FORBIDDEN_APPS = List.of("steam.exe", "discord.exe", "spotify.exe");

    public void killForbiddenApps() {
        try {
            // 1. Get list of currently running tasks
            Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                // 2. Check if any forbidden app is running
                for (String app : FORBIDDEN_APPS) {
                    if (line.toLowerCase().contains(app)) {
                        forceKill(app);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forceKill(String appName) {
        try {
            // 3. Execute 'taskkill /F /IM appname.exe'
            new ProcessBuilder("taskkill", "/F", "/IM", appName).start();
            System.out.println("KILLED: " + appName);
        } catch (Exception e) {
            System.err.println("Failed to kill " + appName);
        }
    }
}