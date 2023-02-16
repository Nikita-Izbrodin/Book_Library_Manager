package CellRenderers;

import Entities.Book;
import Forms.BookListItemForm;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

/**
 * A class for setting data in a cell from a JList displaying books.
 * Changes the colour of a cell depending on if the cell has been selected.
 */
public class BookCellRenderer extends BookListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Book entry = (Book) value;
        setData(entry);
        setBackground(isSelected);
        return this.getPanel();
    }
}
