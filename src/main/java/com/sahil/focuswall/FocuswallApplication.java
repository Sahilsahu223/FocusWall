package com.sahil.focuswall;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

// START: IMPORTANT LINE - DO NOT DELETE
@org.springframework.boot.autoconfigure.SpringBootApplication
public class FocuswallApplication extends Application {
// END: IMPORTANT LINE

	private ConfigurableApplicationContext applicationContext;

	@Override
	public void init() {
		applicationContext = new SpringApplicationBuilder(FocuswallApplication.class).run();
	}

	// THE BEAN THAT CONNECTS EVERYTHING
	@Bean
	public DashboardController dashboardController(BlockerService blockerService, AppKillerService appKillerService) {
		return new DashboardController(blockerService, appKillerService);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
		loader.setControllerFactory(applicationContext::getBean);

		Parent root = loader.load();
		Scene scene = new Scene(root, 400, 500);

		stage.setTitle("FocusWall");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		applicationContext.close();
		Platform.exit();
	}

	public static void main(String[] args) {
		Application.launch(FocuswallApplication.class, args);
	}
} // <--- MAKE SURE THIS LAST CURLY BRACE EXISTS