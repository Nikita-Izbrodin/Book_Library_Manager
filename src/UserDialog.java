import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class UserDialog extends JDialog {
    private JPanel mainPanel;
    private JButton leftButton;
    private JButton buttonCancel;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField usernameField;
    private User user;

    public UserDialog(String type, String username, String fullName) {
        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        if (type.equals("create")) {
            leftButton.setText("Create");
        } else if (type.equals("edit")) {
            leftButton.setText("Save");

        }

        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onOK() {
        if (usernameField.getText().isBlank() || fullNameField.getText().isBlank() || passwordField.getText().isBlank()) {
            return;
        }
        String username = usernameField.getText();
        String full_name = fullNameField.getText();
        String password = HashGenerator.getHashValue(String.valueOf(passwordField.getPassword()));
        user = new User(username, full_name, password);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public User getUser(){
        return this.user;
    }

}
