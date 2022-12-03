package com.holub.life.ui;

import com.holub.life.system.TickSystem;
import com.holub.tools.Observable;
import com.holub.tools.Observer;
import java.awt.Dimension;
import javax.swing.JLabel;

public class StatusBar extends JLabel implements Observer {
  public StatusBar() {
    super();
    super.setPreferredSize(new Dimension(100, 16));
    setMessage("Ready");
  }

  public void setMessage(String message) {
    setText(" "+message);
  }

  @Override
  public void detectUpdate(Observable o) {
    if (o instanceof TickSystem) {
      TickSystem tickSystem = (TickSystem) o;
      setMessage(tickSystem.getSpeed());
    }
  }
}
