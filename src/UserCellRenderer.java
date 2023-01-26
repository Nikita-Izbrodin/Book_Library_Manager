import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class UserCellRenderer extends UserListItemForm implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        User entry = (User) value;
        setData(entry);
        return this.getPanel();
    }

}