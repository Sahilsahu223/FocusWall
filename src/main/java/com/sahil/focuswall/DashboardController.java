package com.sahil.focuswall;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class DashboardController {

    private final BlockerService blockerService;
    private final AppKillerService appKillerService; // <--- NEW FIELD

    // UPDATED CONSTRUCTOR: Now accepts BOTH services
    public DashboardController(BlockerService blockerService, AppKillerService appKillerService) {
        this.blockerService = blockerService;
        this.appKillerService = appKillerService;
    }

    @FXML private Label timerLabel;
    @FXML private Label statusLabel;
    @FXML private Button toggleButton;
    @FXML private ListView<String> blockedList;

    private Timeline timeline;
    private static final int START_TIME = 25 * 60;
    private int timeSeconds = START_TIME;
    private boolean isStrict = false;

    @FXML
    public void initialize() {
        // Show what we are blocking
        blockedList.getItems().clear();
        blockedList.getItems().addAll("Facebook.com", "Steam.exe", "Discord.exe", "Spotify.exe");
        updateTimerLabel();
    }

    @FXML
    public void handleToggle() {
        if (isStrict) {
            // STOPPING
            stopTimer();
            blockerService.unblockWebsites();

            isStrict = false;
            statusLabel.setText("Status: RELAXED");
            statusLabel.setStyle("-fx-text-fill: #98C379; -fx-font-size: 14px;");
            toggleButton.setText("START FOCUS");
            toggleButton.setStyle("-fx-background-color: #E06C75; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 30;");
        } else {
            // STARTING
            startTimer();
            blockerService.blockWebsites();

            isStrict = true;
            statusLabel.setText("Status: STRICT MODE ACTIVE");
            statusLabel.setStyle("-fx-text-fill: #E06C75; -fx-font-size: 14px;");
            toggleButton.setText("STOP");
            toggleButton.setStyle("-fx-background-color: #98C379; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 30;");
        }
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        timeSeconds = START_TIME;
        updateTimerLabel();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            updateTimerLabel();

            // --- CRITICAL: KILL APPS EVERY SECOND ---
            appKillerService.killForbiddenApps();
            // ----------------------------------------

            if (timeSeconds <= 0) {
                timeline.stop();
                handleToggle();
            }
        }));
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) timeline.stop();
        timeSeconds = START_TIME;
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
}