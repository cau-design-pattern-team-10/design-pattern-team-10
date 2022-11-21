package com.holub.tools;

/*******************************************************************
 * This class replaces the Multicaster class that's described in
 * <i>Taming Java Threads</i>. It's better in almost every way
 * (It's smaller, simpler, faster, etc.). The primary difference
 * between this class and the original is that I've based
 * it on a linked-list, and I've used a Strategy object to
 * define how to notify listeners, thereby makeing the interface
 * much more flexible.
 * <p>
 * The <code>Publisher</code> class provides an efficient thread-safe means of
 * notifying listeners of an event. The list of listeners can be
 * modified while notifications are in progress, but all listeners
 * that are registered at the time the event occurs are notified (and
 * only those listeners are notified). The ideas in this class are taken
 * from the Java's AWTEventMulticaster class, but I use an (iterative)
 * linked-list structure rather than a (recursive) tree-based structure
 * for my implementation.
 * <p>
 * Here's an example of how you might use a <code>Publisher</code>:
 * <PRE>
 *	class EventGenerator
 *  {	interface Listener
 *    {	notify( String why );
 *    }
 *
 *		private Publisher publisher = new Publisher();
 *
 *		public void addEventListener( Listener l )
 *    {	publisher.subscribe(l);
 *    }
 *
 *		public void removeEventListener ( Listener l )
 *    {	publisher.cancelSubscription(l);
 *    }
 *
 *		public void someEventHasHappend(final String reason)
 *    {	publisher.publish
 *			(	
 *				// Pass the publisher a Distributor that knows
 *				// how to notify EventGenerator listeners. The
 *				// Distributor's deliverTo method is called
 *				// multiple times, and is passed each listener
 *				// in turn.
 *
 *				new Publisher.Distributor()
 *        {	public void deliverTo( Object subscriber )
 *          {	((Listener)subscriber).notify(reason);
 *          }
 *        }
 *			);
 *    }
 *  }
 * </PRE>
 * Since you're specifying what a notification looks like
 * by defining a Listener interface, and then also defining
 * the message passing symantics (inside the Distributor implementation),
 * you have complete control over what the notification interface looks like.
 *
 * @include /etc/license.txt
 */

public class Publisher {

  public interface Distributor {

    void deliverTo(Object subscriber);  // the Visitor pattern's
  }                      // "visit" method.

  // The Node class is immutable. Once it's created, it can't
  // be modified. Immutable classes have the property that, in
  // a multithreaded system, access to the does not have to be
  // synchronized, because they're read only.
  //
  // This particular class is really a struct so I'm allowing direct
  // access to the fields. Since it's private, I can play
  // fast and loose with the encapsulation without significantly
  // impacting the maintainability of the code.

  private class Node {

    public final Object subscriber;
    public final Node next;

    private Node(Object subscriber, Node next) {
      this.subscriber = subscriber;
      this.next = next;
    }

    public Node remove(Object target) {
      if (target == subscriber) {
        return next;
      }

      if (next == null)            // target is not in list
      {
        throw new java.util.NoSuchElementException
            (target.toString());
      }

      return new Node(subscriber, next.remove(target));
    }

    public void accept(Distributor deliveryAgent) // deliveryAgent is
    {
      deliveryAgent.deliverTo(subscriber);     // a "visitor"
    }
  }

  private volatile Node subscribers = null;

  /**
   * Publish an event using the deliveryAgent. Note that this method isn't synchronized (and doesn't
   * have to be). Those subscribers that are on the list at the time the publish operation is
   * initiated will be notified. (So, in theory, it's possible for an object that cancels its
   * subsciption to nonetheless be notified.) There's no universally "good" solution to this
   * problem.
   */

  public void publish(Distributor deliveryAgent) {
    for (Node cursor = subscribers; cursor != null; cursor = cursor.next) {
      cursor.accept(deliveryAgent);
    }
  }

  synchronized public void subscribe(Object subscriber) {
    subscribers = new Node(subscriber, subscribers);
  }

  synchronized public void cancelSubscription(Object subscriber) {
    subscribers = subscribers.remove(subscriber);
  }

}
