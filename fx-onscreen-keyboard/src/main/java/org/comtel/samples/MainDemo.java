package org.comtel.samples;

import java.util.Locale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.comtel.javafx.control.DefaultLayers;
import org.comtel.javafx.control.KeyBoardBuilder;
import org.comtel.javafx.control.KeyboardPane;
import org.comtel.javafx.robot.RobotFactory;

public class MainDemo extends Application
{

  private KeyboardPane keyboard;

  @Override
  public void start(Stage stage)
  {

    stage.setTitle("FX Keyboard (" + System.getProperty("javafx.runtime.version") + ")");
    stage.setResizable(true);
    //NUMBLOCK examples
    keyboard = KeyBoardBuilder.create().layer(DefaultLayers.DEFAULT).initScale(1).initLocale(Locale.forLanguageTag("fr")).addIRobot(RobotFactory.createFXRobot()).build();

    StackPane outerStackPane = new StackPane();
    StackPane innerStackPane = new StackPane();
    VBox pane = new VBox(20);

    TextField initialFocusTF = new TextField("");
    pane.getChildren().add(new Label(""));
    pane.getChildren().add(new Label("Text Field"));
    pane.getChildren().add(initialFocusTF);
    pane.getChildren().add(new Label("Text Area"));
    pane.getChildren().add(new TextArea(""));

    HBox hPane = new HBox();

    hPane.getChildren().add(keyboard);
    pane.getChildren().add(hPane);
    outerStackPane.getChildren().add(innerStackPane);
    innerStackPane.getChildren().add(pane);
    Scene scene = new Scene(outerStackPane, 740, 590);

    stage.setOnCloseRequest((event) -> System.exit(0));
    stage.setScene(scene);
    stage.show();
    initialFocusTF.requestFocus();
  }

  public static void main(String[] args)
  {
    Application.launch(args);
  }
}
