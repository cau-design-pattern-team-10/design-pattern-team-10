package com.holub.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/***
 * All mementos created by the Cells are Storable.
 */

public interface Storable {

  /**
   *
   * @param in
   * @throws IOException
   */
  void load(InputStream in) throws IOException;

  /**
   *
   * @param out
   * @throws IOException
   */
  void flush(OutputStream out) throws IOException;
}
