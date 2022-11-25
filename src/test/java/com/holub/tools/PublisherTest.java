package com.holub.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PublisherTest {

  static final StringBuffer actualResults = new StringBuffer();
  static final StringBuffer expectedResults = new StringBuffer();

  interface Observer {

    void notify(String arg);
  }

  static class Notifier {

    private Publisher publisher = new Publisher();

    public void addObserver(Observer l) {
      publisher.subscribe(l);
    }

    public void removeObserver(Observer l) {
      publisher.cancelSubscription(l);
    }

    public void fire(final String arg) {
      publisher.publish(subscriber -> ((Observer) subscriber).notify(arg));
    }
  }

  @Test()
  void publisherTest() {
    Notifier source = new Notifier();
    int errors = 0;

    Observer listener1 = arg -> actualResults.append("1[" + arg + "]");
    Observer listener2 = arg -> actualResults.append("2[" + arg + "]");

    source.addObserver(listener1);
    source.addObserver(listener2);

    source.fire("a");
    source.fire("b");

    expectedResults.append("2[a]");
    expectedResults.append("1[a]");
    expectedResults.append("2[b]");
    expectedResults.append("1[b]");

    source.removeObserver(listener1);

    try {
      source.removeObserver(listener1);
      System.err.print("Removed nonexistent node!");
      ++errors;
    } catch (java.util.NoSuchElementException e) {
      // should throw an exception, which we'll catch
      // (and ignore) here.
    }

    expectedResults.append("2[c]");
    source.fire("c");

    if (!expectedResults.toString().equals(actualResults.toString())) {
      System.err.print("add/remove/fire failure.\n");
      System.err.print("Expected:[");
      System.err.print(expectedResults.toString());
      System.err.print("]\nActual:  [");
      System.err.print(actualResults.toString());
      System.err.print("]");
      ++errors;
    }

    source.removeObserver(listener2);
    source.fire("Hello World");
    try {
      source.removeObserver(listener2);
      System.err.println("Undetected illegal removal.");
      ++errors;
    } catch (Exception e) { /*everything's okay, do nothing*/ }

    Assertions.assertEquals(0, errors);
  }

}
