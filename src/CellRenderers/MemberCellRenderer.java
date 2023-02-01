package CellRenderers;

import Entities.Member;
import Forms.MemberListItemForm;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class MemberCellRenderer extends MemberListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Member entry = (Member) value;
        setData(entry);
        setBackground(isSelected);
        return this.getPanel();
    }
}