package my.edu.uploader.controllers;

import com.github.spring.boot.javafx.stereotype.ViewController;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.edu.uploader.service.MultiThreadedFileCopyService;
import my.edu.uploader.service.Uploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@ViewController
@RequiredArgsConstructor
public class ControlController implements Initializable {

    @Value("${file.source}")
    private String sourcePath;

    @Value("${file.destination}")
    private String destinationPath;

    private final Uploader fileCopyService;

    private final MessageSource messageSource;

    @FXML
    private Button uploadButton;

    @FXML
    Label statusLabel = new Label("File copy status: ");

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fileCopyService.stateProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText(messageSource.getMessage("label.status", null, LocaleContextHolder.getLocale()) + newValue.toString());
            if (newValue == Service.State.SUCCEEDED || newValue == Service.State.FAILED || newValue == Service.State.CANCELLED) {
                uploadButton.setText(messageSource.getMessage("button.upload", null, LocaleContextHolder.getLocale()));
            } else if (newValue == Service.State.RUNNING) {
                uploadButton.setText(messageSource.getMessage("button.cancel", null, LocaleContextHolder.getLocale()));
            }
        });

        File sourceFile = new File(sourcePath);

        uploadButton.setOnAction(event -> {
            if (fileCopyService.isRunning()) {
                cancelUpload();
            } else {
                startUpload(sourceFile);
            }
        });
    }

    private void startUpload(File sourceFile) {
        if (sourceFile.exists()){
            File destFile = new File(destinationPath, "copy_" + sourceFile.getName());
            fileCopyService.setSourceFile(sourceFile);
            fileCopyService.setDestFile(destFile);
            if (fileCopyService.getState() == Service.State.READY || fileCopyService.getState() == Service.State.SUCCEEDED || fileCopyService.getState() == Service.State.CANCELLED) {
                fileCopyService.reset();
                fileCopyService.start();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("File Not Found");
            alert.setHeaderText(null);
            alert.setContentText("File does not exist at the specified path: " + sourceFile.getAbsolutePath());
            alert.showAndWait();
        }

    }

    private void cancelUpload(){
        fileCopyService.cancel();
        fileCopyService.reset();
    }


}
