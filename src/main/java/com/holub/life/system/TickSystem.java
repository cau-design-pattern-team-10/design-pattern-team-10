package com.holub.life.system;

import com.holub.tools.Observable;
import com.holub.tools.Observer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TickSystem implements Observable {
  final Clock clock;
  String currentSpeed;
  Boolean isRunning = true;
  Map<String, Integer> tickMap;
  List<Observer> observers;
  /**
   *
   */
  private static final int AGONIZING_MS = 500;
  /**
   *
   */
  private static final int SLOW_MS = 150;
  /**
   *
   */
  private static final int MEDIUM_MS = 70;
  /**
   *
   */
  private static final int FAST_MS = 30;
  /**
   *
   */

  public TickSystem(final Clock c) {
    clock = c;
    tickMap = new HashMap<>();
    observers = new LinkedList<>();
    currentSpeed = "Halt";
    tickMap.put("Agonizing", AGONIZING_MS); // agonizing
    tickMap.put("Slow", SLOW_MS); // slow
    tickMap.put("Medium", MEDIUM_MS); // medium
    tickMap.put("Fast", FAST_MS); // fast
  }

  public List<String> getSupportedSpeeds() {
    return tickMap.keySet().stream()
        .sorted(Comparator.comparing(o -> tickMap.get(o)).reversed())
        .collect(Collectors.toList());
  }

  public String getSpeed() {
    return currentSpeed;
  }

  public Boolean isRunning() {
    return isRunning;
  }

  public void setSpeed(final String speedName) {
    currentSpeed = speedName;
    clock.startTicking(tickMap.get(speedName));
    isRunning = true;
    update();
  }

  public void tick() {
    clock.tick();
  }

  public void stop() {
    isRunning = false;
    clock.stop();
    update();
  }

  public void resume() {
    isRunning = true;
    clock.startTicking(tickMap.get(currentSpeed));
    update();
  }

  @Override
  public void update() {
    for (Observer observer : observers) {
      observer.detectUpdate(this);
    }
  }

  @Override
  public void attach(Observer observer) {
    observers.add(observer);
    update();
  }

  @Override
  public void detach(Observer observer) {
    observers.remove(observer);
  }
}
