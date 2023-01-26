import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class BorrowerCellRenderer extends BorrowerListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Borrower entry = (Borrower) value;
        setData(entry);
        return this.getPanel();
    }
}