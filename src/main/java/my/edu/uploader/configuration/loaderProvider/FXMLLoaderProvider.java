package my.edu.uploader.configuration.loaderProvider;


import com.github.spring.boot.javafx.view.ViewLoader;
import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FXMLLoaderProvider {

   private final ViewLoader viewLoader;

    public Node loadView(String path) {
        return viewLoader.load(path);
    }
}
