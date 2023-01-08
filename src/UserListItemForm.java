import javax.swing.*;

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

}
