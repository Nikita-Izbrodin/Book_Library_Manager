import javax.swing.*;
import java.awt.*;

public class BorrowerCellRenderer extends BorrowerListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Borrower entry = (Borrower) value;
        setData(entry);
        return this.getPanel();
    }

}