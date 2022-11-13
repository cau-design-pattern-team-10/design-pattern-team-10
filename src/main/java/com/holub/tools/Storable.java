package com.holub.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/***
 * All mementos created by the Cells are Storable.
 *
 * @include /etc/license.txt
 */

public interface Storable {

  void load(InputStream in) throws IOException;

  void flush(OutputStream out) throws IOException;
}
