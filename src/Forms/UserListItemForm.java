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
        usernameLabel.setText("Username: "+ entry.getUsername());
        fullNameLabel.setText("Full Name: "+ entry.getFullName());
    }

    protected void setBackground(boolean isSelected) {
        userItemPanel.setBackground(isSelected ? Color.blue : Color.lightGray);

        usernameLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        fullNameLabel.setForeground(isSelected ? Color.white : Color.BLACK);
    }
}
