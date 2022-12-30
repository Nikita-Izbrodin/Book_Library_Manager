import javax.swing.*;
import java.awt.*;

public class BookCellRenderer extends bookListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Book entry = (Book) value;
        setData(entry);
        return this.getPanel();
    }

}
