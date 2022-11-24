package com.holub.tools;

public interface Observable {

  /**
   *
   */
  void update();

  /**
   *
   * @param observer
   */
  void attach(Observer observer);

  /**
   *
   * @param observer
   */
  void detach(Observer observer);
}
