package my.edu.uploader.service;

import javafx.concurrent.Worker;
import javafx.event.EventTarget;

import java.io.File;

public interface Uploader extends Worker<Void>, EventTarget {
    void setSourceFile(File sourceFile);

    void setDestFile(File destFile);

    void reset();

    void start();
}
