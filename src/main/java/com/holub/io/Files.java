package com.holub.io;

import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/*** A utility that handles various File-related operations.
 */

public final class Files {

  /**
   * Files is a singleton class.
   */
  private Files() {
  }

  /**
   * Throw up a file choser and return the file that the user selects.
   *
   * @param extension File extension (text following the rightmost dot in the
   * name) that you're looking for. Use null if any will do.
   * @param description the description of what the extension means. Not used
   * if "extension" is null.
   * @param selectButtonText Replaces the "Open" on the chooser button.
   * @param startHere Name of initial directory in which to look.
   * @return a {@link File} representing the selected file.
   * @throws FileNotFoundException if the user didn't select a file.
   * An exception toss (as compared to a null return value) makes it easy to
   * do the following:
   * <PRE>
   * FileInputStream in = new FileInputStream(
   *   Files.userSelected(".",".txt","Text File","Open"));
   * </PRE>
   */

  public static File userSelected(final String startHere,
      final String extension,
      final String description,
      final String selectButtonText)
      throws FileNotFoundException {
    FileFilter filter =
        new FileFilter() {
          public boolean accept(final File f) {
            return f.isDirectory()
                || (extension != null
                && f.getName().endsWith(extension));
          }

          public String getDescription() {
            return description;
          }
        };

    JFileChooser chooser = new JFileChooser(startHere);
    chooser.setFileFilter(filter);

    int result = chooser.showDialog(null, selectButtonText);
    if (result == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile();
    }

    throw new FileNotFoundException("No file selected by user");
  }
}
