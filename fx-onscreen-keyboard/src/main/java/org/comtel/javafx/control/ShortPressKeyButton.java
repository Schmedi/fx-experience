package org.comtel.javafx.control;

public class ShortPressKeyButton extends KeyButton
{

  private final MultiKeyButton parentButton;

  public ShortPressKeyButton(MultiKeyButton parentButton, String key)
  {
    super(parentButton.getKeyboard(), key, 0);
    this.parentButton = parentButton;
  }

  public MultiKeyButton getParentButton()
  {
    return parentButton;
  }
}
