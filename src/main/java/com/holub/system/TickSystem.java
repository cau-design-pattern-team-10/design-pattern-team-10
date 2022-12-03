package com.holub.system;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TickSystem {
  final Clock clock;
  Map<String, Integer> tickMap;
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

  public void setSpeed(final String speedName) {
    clock.startTicking(tickMap.get(speedName));
  }

  public void tick() {
    clock.tick();
  }

  public void stop() {
    clock.stop();
  }
}
