package org.comtel.javafx.control;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

public class MultiKeyPopup extends Region
{

  public static final String DEFAULT_STYLE_CLASS = "key-context-background";//NOI18N
  private static final int MAX_COLUMNS = 3;

  private final ObservableList<KeyButton> buttons;
  private final TilePane buttonPane;
  private final MultiKeyButton multiKeyButton;

  public MultiKeyPopup(MultiKeyButton multiKeyButton)
  {
    this.multiKeyButton = multiKeyButton;
    setManaged(false);

    buttonPane = new TilePane();
    buttonPane.setId(DEFAULT_STYLE_CLASS);
    buttonPane.setPrefColumns(0);
    buttonPane.setFocusTraversable(false);
    //The vgap property is initialized when the css values are applied. This happens after the call of the first show() method.
    //Therefore we must listen to the change of this property to relayout the location of the popup
    buttonPane.vgapProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
      layoutPosition();
    });
    buttons = FXCollections.observableArrayList();
    buttons.addListener(new ListChangeListener<KeyButton>()
    {

      @Override
      public void onChanged(Change<? extends KeyButton> c)
      {
        while (c.next()) {
          for (ButtonBase button : c.getAddedSubList()) {
            buttonPane.setPrefColumns(Math.min(MAX_COLUMNS, buttonPane.getPrefColumns() + 1));
            button.setFocusTraversable(false);
            buttonPane.getChildren().add(button);
          }
        }
      }
    });
    getChildren().add(buttonPane);
  }

  public ObservableList<KeyButton> getButtons()
  {
    return buttons;
  }

  public void show()
  {
    StackPane stackPane = getStackPane(multiKeyButton.getScene());
    stackPane.getChildren().add(this);
    layoutPosition();
  }

  public void hide()
  {
    Scene scene = multiKeyButton.getScene();
    getStackPane(scene).getChildren().remove(this);
    multiKeyButton.getParent().getParent().setDisable(false);
  }

  private void layoutPosition()
  {
    StackPane stackPane = getStackPane(multiKeyButton.getScene());
    Bounds boundsOnPane = stackPane.sceneToLocal(multiKeyButton.localToScene(multiKeyButton.getBoundsInLocal()));
    double x = boundsOnPane.getMinX();
    double y = boundsOnPane.getMinY() - calcPreferredHeight();
    setLayoutX(x);
    setLayoutY(y);
  }

  private double calcPreferredHeight()
  {
    double rowCount = Math.ceil(((double) buttons.size()) / buttonPane.getPrefColumns());
    double height = rowCount * multiKeyButton.getLayoutBounds().getHeight();
    height += (rowCount + 1) * buttonPane.getVgap();
    height += buttonPane.getPadding().getTop();
    height += buttonPane.getPadding().getBottom();
    //if we use a scale for the keyboard, we must also scale up the preferred height.
    if (multiKeyButton.getKeyboard().getTransforms().size() == 1) {
      Scale scale = (Scale) multiKeyButton.getKeyboard().getTransforms().get(0);
      height = height * scale.getY();
    }
    return height;
  }

  private StackPane getStackPane(Scene scene)
  {
    for (Node node : scene.getRoot().getChildrenUnmodifiable()) {
      if (node instanceof StackPane) {
        return (StackPane) node;
      }
    }
    throw new RuntimeException("The rootpane of the FT1 must have a stackpane as the content pane");//NOI18N
  }
}
