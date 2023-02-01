package Forms;

import Entities.User;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public class UserListItemForm {
    private JPanel userItemPanel;
    private JLabel usernameLabel;
    private JLabel fullNameLabel;

    public JPanel getPanel() {
        return userItemPanel;
    }

    public void setData(User entry) {
        usernameLabel.setText("Username: "+ entry.username());
        fullNameLabel.setText("Full Name: "+ entry.fullName());
    }

    protected void setBackground(boolean isSelected) {
        userItemPanel.setBackground(isSelected ? Color.blue : Color.lightGray);

        Color foregroundColor = isSelected ? Color.white : Color.BLACK;

        usernameLabel.setForeground(foregroundColor);
        fullNameLabel.setForeground(foregroundColor);
    }
}
