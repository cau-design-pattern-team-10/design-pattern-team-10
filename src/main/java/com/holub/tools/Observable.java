package com.holub.tools;

public interface Observable {

  void update();

  void attach(Observer observer);

  void detach(Observer observer);
}
