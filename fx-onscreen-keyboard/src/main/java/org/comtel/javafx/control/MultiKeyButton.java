package org.comtel.javafx.control;

import java.util.Collection;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.TouchEvent;
import javafx.scene.transform.Scale;

public class MultiKeyButton extends KeyButton
{

  /**
   * There is only ever one keyboard with one popup active
   */
  private static MultiKeyPopup currentPopup = null;

  private ObservableList<KeyButton> extKeyCodes;
  private MultiKeyPopup context;//each multikey button has its own instance of the popup displaying different ext keys
  private final DoubleProperty scaleProperty;
  private final Collection<String> styles;

  public MultiKeyButton(KeyboardPane keyboard, DoubleProperty scaleProperty, Collection<String> styles)
  {
    super(keyboard);
    this.scaleProperty = scaleProperty;
    this.styles = styles;
  }

  public ObservableList<KeyButton> getExtKeyCodes()
  {
    if (extKeyCodes == null) {
      extKeyCodes = FXCollections.observableArrayList();
      extKeyCodes.addListener(new ListChangeListener<KeyButton>()
      {

        @Override
        public void onChanged(Change<? extends KeyButton> c)
        {
          while (c.next()) {
            for (KeyButton button : c.getAddedSubList()) {
              getContext().getButtons().add(button);
            }
          }
        }
      });
    }
    return extKeyCodes;
  }

  private MultiKeyPopup getContext()
  {
    if (context == null) {

      context = new MultiKeyPopup(this);
      context.getStylesheets().addAll(styles);
      context.getTransforms().setAll(new Scale(scaleProperty.get(), scaleProperty.get(), 1, 0, 0, 0));

      setOnLongPressed(new EventHandler<Event>()
      {

        @Override
        public void handle(Event event)
        {
          getParent().getParent().setDisable(true);
          context.show();
          currentPopup = context;
        }
      });

    }
    return context;
  }

  public void addExtKeyCode(int extKeyCode)
  {
    addExtKeyCode(extKeyCode, null, null);
  }

  public void addExtKeyCode(int extKeyCode, String label, ObservableList<String> styleClasses)
  {
    ShortPressKeyButton button = new ShortPressKeyButton(this, Character.toString((char) extKeyCode));

    if (styleClasses != null) {
      button.getStyleClass().addAll(styleClasses);
    }
    else {
      button.setId("key-context-button");
    }
    if (label != null) {
      button.setText(label);
    }
    button.setFocusTraversable(false);

    // TODO: add to css style
    button.setPrefWidth(this.getPrefWidth());
    button.setPrefHeight(this.getPrefHeight());

    button.setKeyCode(extKeyCode);
    button.setOnShortPressed(getOnShortPressed());

    getExtKeyCodes().add(button);
  }

  public boolean isContextAvailable()
  {
    return context != null && extKeyCodes != null;
  }

  @Override
  protected void touchReleased(TouchEvent event)
  {
    try {
      super.touchReleased(event);
    }
    finally {
      //We must always hide the popup
      if (currentPopup != null) {
        currentPopup.hide();
      }
      currentPopup = null;
    }
  }
}
