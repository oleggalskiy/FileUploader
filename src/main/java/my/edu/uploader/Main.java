package my.edu.uploader;

import com.github.spring.boot.javafx.SpringJavaFXApplication;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.edu.uploader.configuration.loaderProvider.FXMLLoaderProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class Main extends SpringJavaFXApplication {

    public static void main(String[] args) {
        launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoaderProvider provider = applicationContext.getBean(FXMLLoaderProvider.class);

        Node root = provider.loadView("main.fxml");

        Scene scene = new Scene((Parent) root);
        primaryStage.setScene(scene);
        super.start(primaryStage);
        primaryStage.setTitle("Load file...");
        primaryStage.setMinWidth(490);
        primaryStage.setMinHeight(480);
        primaryStage.show();

    }



}
