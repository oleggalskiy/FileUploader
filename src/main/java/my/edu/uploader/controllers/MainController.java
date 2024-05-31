package my.edu.uploader.controllers;


import com.github.spring.boot.javafx.stereotype.ViewController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

@ViewController
@RequiredArgsConstructor
public class MainController {

    @FXML
    private VBox root;


    private final HeaderController headerController;


    private final ControlController controlController;

    @FXML
    public void initialize() {
    }

}
