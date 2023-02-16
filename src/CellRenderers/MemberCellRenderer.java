package CellRenderers;

import Entities.Member;
import Forms.MemberListItemForm;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

/**
 * A class for setting data in a cell from a JList displaying members.
 * Changes the colour of a cell depending on if the cell has been selected.
 */
public class MemberCellRenderer extends MemberListItemForm implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Member entry = (Member) value;
        setData(entry);
        setBackground(isSelected);
        return this.getPanel();
    }
}