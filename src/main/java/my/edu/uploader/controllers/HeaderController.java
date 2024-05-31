package my.edu.uploader.controllers;

import com.github.spring.boot.javafx.stereotype.ViewController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;
import my.edu.uploader.service.Uploader;

import java.net.URL;
import java.util.ResourceBundle;

@ViewController
@RequiredArgsConstructor
public class HeaderController implements Initializable {


    @FXML
    private ProgressBar progressBar;

    private final Uploader fileCopyService;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setStyle("-fx-accent: #00FF00;");
        progressBar.progressProperty().bind(fileCopyService.progressProperty());
    }


}
