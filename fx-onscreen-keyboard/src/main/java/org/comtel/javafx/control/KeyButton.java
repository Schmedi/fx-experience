package org.comtel.javafx.control;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.animation.Animation.Status;
import javafx.scene.input.TouchEvent;
import org.comtel.javafx.event.KeyButtonEvent;

public class KeyButton extends Button implements LongPressable
{

  /**
   * There is only ever one keyboard with one hold key
   */
  private static KeyButton buttonUnderTouchHold = null;

  private final static long DEFAULT_DELAY = 200;
  private final Timeline timer;
  private final KeyboardPane keyboard;

  private int keyCode;

  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressed;
  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressed;

  public KeyButton(KeyboardPane keyboard)
  {
    this(keyboard, null, DEFAULT_DELAY);
  }

  public KeyButton(KeyboardPane keyboard, String label)
  {
    this(keyboard, label, DEFAULT_DELAY);
  }

  public KeyButton(KeyboardPane keyboard, String label, long delay)
  {
    super(label);
    this.keyboard = keyboard;
    setId("key-button");

    if (delay == 0) {
      timer = null;
    }
    else {
      timer = createTimer(delay);
    }
    setOnTouchPressed((TouchEvent event) -> {
      touchPressed(event);
    });
    setOnTouchMoved((TouchEvent event) -> {
      touchMoved(event);
    });
    setOnTouchReleased((TouchEvent event) -> {
      touchReleased(event);
    });
  }

  public Duration getDelay()
  {
    return timer.getDelay();
  }

  public void setDelay(Duration delay)
  {
    timer.setDelay(delay);
  }

  protected void fireLongPressed()
  {
    fireEvent(new KeyButtonEvent(this, KeyButtonEvent.LONG_PRESSED));
  }

  protected void fireShortPressed()
  {
    fireEvent(new KeyButtonEvent(this, KeyButtonEvent.SHORT_PRESSED));
  }

  @Override
  public final void setOnLongPressed(EventHandler<? super KeyButtonEvent> eventhandler)
  {
    onLongPressedProperty().set(eventhandler);
  }

  @Override
  public final EventHandler<? super KeyButtonEvent> getOnLongPressed()
  {
    return onLongPressedProperty().get();
  }

  @Override
  public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressedProperty()
  {
    if (onLongPressed == null) {
      onLongPressed = new SimpleObjectProperty<EventHandler<? super KeyButtonEvent>>()
      {

        @SuppressWarnings("unchecked")
        @Override
        protected void invalidated()
        {
          setEventHandler(KeyButtonEvent.LONG_PRESSED, (EventHandler<? super Event>) get());
        }
      };
    }
    return onLongPressed;
  }

  @Override
  public final void setOnShortPressed(EventHandler<? super KeyButtonEvent> eventhandler)
  {
    onShortPressedProperty().set(eventhandler);
  }

  @Override
  public final EventHandler<? super KeyButtonEvent> getOnShortPressed()
  {
    return onShortPressedProperty().get();
  }

  @Override
  public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressedProperty()
  {
    if (onShortPressed == null) {
      onShortPressed = new SimpleObjectProperty<EventHandler<? super KeyButtonEvent>>()
      {

        @SuppressWarnings("unchecked")
        @Override
        protected void invalidated()
        {
          setEventHandler(KeyButtonEvent.SHORT_PRESSED, (EventHandler<? super Event>) get());
        }
      };
    }
    return onShortPressed;

  }

  protected KeyboardPane getKeyboard()
  {
    return keyboard;
  }

  public int getKeyCode()
  {
    return keyCode;
  }

  public void setKeyCode(int keyCode)
  {
    this.keyCode = keyCode;
  }

  private Timeline createTimer(long delay)
  {
    Timeline timeLine = new Timeline(new KeyFrame(new Duration(20), new KeyValue[0]));
    timeLine.setDelay(new Duration(delay));
    timeLine.setOnFinished(new EventHandler<ActionEvent>()
    {

      @Override
      public void handle(ActionEvent event)
      {
        buttonUnderTouchHold.fireLongPressed();
      }

    });
    return timeLine;
  }

  protected void touchPressed(TouchEvent event)
  {
    //We are only interested in single finger touch events
    if (event.getTouchCount() == 1 && event.getEventSetId() == 1) {
      //This is the first "touch" of a complex gesture, we must start the timer
      if (timer.getStatus().equals(Status.STOPPED)) {
        timer.playFromStart();
      }
    }
    KeyButton oldButton = buttonUnderTouchHold;
    Node intersectedNode = event.getTouchPoint().getPickResult().getIntersectedNode();
    if (intersectedNode instanceof KeyButton) {
      buttonUnderTouchHold = (KeyButton) intersectedNode;
    }
    else if (intersectedNode instanceof LabeledText && intersectedNode.getParent() instanceof KeyButton) {
      LabeledText lt = (LabeledText) intersectedNode;
      buttonUnderTouchHold = (KeyButton) lt.getParent();
    }
    if (oldButton != null) {
      oldButton.setStyle("-fx-background-color: rgb(60, 66, 80)");
    }
    if (buttonUnderTouchHold != null) {
      buttonUnderTouchHold.setStyle("-fx-background-color: rgb(100, 106, 120)");
    }
    event.consume();
  }

  protected void touchMoved(TouchEvent event)
  {
    KeyButton oldButton = buttonUnderTouchHold;
    Node intersectedNode = event.getTouchPoint().getPickResult().getIntersectedNode();
    if (intersectedNode instanceof KeyButton) {
      buttonUnderTouchHold = (KeyButton) intersectedNode;
    }
    else if (intersectedNode instanceof LabeledText && intersectedNode.getParent() instanceof KeyButton) {
      LabeledText lt = (LabeledText) intersectedNode;
      buttonUnderTouchHold = (KeyButton) lt.getParent();
    }
    //If the button we have moved over, changed, we reset the old buttons CSS to "normal" and switch the new button to "hold"
    if (oldButton != buttonUnderTouchHold) {
      timer.playFromStart();
      if (oldButton != null) {
        oldButton.setStyle("-fx-background-color: rgb(60, 66, 80)");
      }
      if (buttonUnderTouchHold != null) {
        buttonUnderTouchHold.setStyle("-fx-background-color: rgb(100, 106, 120)");
      }
    }
  }

  protected void touchReleased(TouchEvent event)
  {
    if (timer.getStatus() == Status.RUNNING) {
      timer.stop();
    }
    KeyButton oldButton = buttonUnderTouchHold;
    Node intersectedNode = event.getTouchPoint().getPickResult().getIntersectedNode();
    if (intersectedNode instanceof KeyButton) {
      buttonUnderTouchHold = (KeyButton) intersectedNode;
    }
    else if (intersectedNode instanceof LabeledText && intersectedNode.getParent() instanceof KeyButton) {
      LabeledText lt = (LabeledText) intersectedNode;
      buttonUnderTouchHold = (KeyButton) lt.getParent();
    }
    else {
      buttonUnderTouchHold = null;
    }
    if (oldButton != null) {
      oldButton.setStyle("-fx-background-color: rgb(60, 66, 80)");
    }
    if (buttonUnderTouchHold != null) {
      buttonUnderTouchHold.setStyle("-fx-background-color: rgb(60, 66, 80)");
      buttonUnderTouchHold.fireShortPressed();
    }
  }
}
