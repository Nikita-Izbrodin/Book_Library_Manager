package CellRenderers;

import Entities.Book;
import Forms.BookListItemForm;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class BookCellRenderer extends BookListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Book entry = (Book) value;
        setData(entry);
        setBackground(isSelected);
        return this.getPanel();
    }
}
