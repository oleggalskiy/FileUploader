JavaFX File Uploader

Description

This is a JavaFX application for multithreaded file copying using Spring Boot for dependency management and configuration. The application supports localization of the interface and displays the status of the file copying process.
Requirements

    Java 11 or higher
    JavaFX SDK (e.g., javafx-sdk-21.0.3)

Installation and Running
Step 1: Clone the Repository

Clone the repository to your local machine:

bash

git clone https://github.com/oleggalskiy/javafx-file-uploader.git

Step 2: Configure Properties File

Create a file named application.properties in the src/main/resources directory and add the following lines:

properties

file.source=path/to/source/file
file.destination=path/to/destination/directory

Replace the paths with actual paths suitable for your environment.

Step 3: Set Up JavaFX SDK

Download and set up the JavaFX SDK. Make sure you add the following VM options to your run configuration:

text

--module-path path/to/JavaFX/lib --add-modules javafx.controls,javafx.fxml
