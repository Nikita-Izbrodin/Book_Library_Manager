import javax.swing.*;
import java.awt.event.*;

public class CreateUserDialog extends JDialog {
    private JPanel mainPanel;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField usernameField;
    private User newUser;

    public CreateUserDialog() {
        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
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

        if (usernameField.getText().isEmpty() || nameField.getText().isEmpty() || passwordField.getPassword().length == 0) {
            return;
        }

        String username = usernameField.getText();
        String full_name = nameField.getText();
        String password = HashGenerator.getHashValue(String.valueOf(passwordField.getPassword()));
        newUser = new User(username, full_name, password);

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public User getNewUser(){
        return this.newUser;
    }

}
