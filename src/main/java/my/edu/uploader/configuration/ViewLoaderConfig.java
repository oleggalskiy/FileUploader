package my.edu.uploader.configuration;


import com.github.spring.boot.javafx.text.LocaleText;
import com.github.spring.boot.javafx.view.ViewLoader;
import com.github.spring.boot.javafx.view.ViewLoaderImpl;
import com.github.spring.boot.javafx.view.ViewManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


public class ViewLoaderConfig {

    @Bean
    public ViewLoader viewLoader(ApplicationContext applicationContext, ViewManager viewManager, LocaleText localeText){
        return new ViewLoaderImpl(applicationContext, viewManager, localeText);
    }
}
