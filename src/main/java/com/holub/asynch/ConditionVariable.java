package com.holub.asynch;

/**
 * This class is a simplified version of the com.asynch.Condition class
 * described in Taming Java Threads. It's really supplanted by classes in the
 * Java 1.5 java.util.concurrent package. Use it to wait for some condition to
 * become true. For example:
 * <PRE>
 * ConditionVariable hellFreezesOver = new ConditionVariable(false);
 * <p>
 * Thread 1: hellFreezesOver.waitForTrue();
 * <p>
 * Thread 2: hellFrezesOver.set(true);
 * </PRE>
 * Unlike <code>wait()</code> you will not be suspened at all if you wait on
 * a true condition variable. Call <code>set(false)</code>, to put the variable
 * back into a false condition (thereby forcing threads to wait for the
 * condition to become true, again).
 */

public class ConditionVariable {

  /**
   * isTure is a synchronized state.
   */
  private volatile boolean isTrue;

  /**
   * @param v
   */
  public ConditionVariable(final boolean v) {
    this.isTrue = v;
  }

  /**
   * @return current condition
   */
  public synchronized boolean isTrue() {
    return isTrue;
  }

  /**
   * @param v set state to v
   */
  public synchronized void set(final boolean v) {
    isTrue = v;
    if (isTrue) {
      notifyAll();
    }
  }

  /**
   * busy wait.
   * @throws InterruptedException
   */
  public final synchronized void waitForTrue() throws InterruptedException {
    while (!isTrue) {
      wait();
    }
  }
}
