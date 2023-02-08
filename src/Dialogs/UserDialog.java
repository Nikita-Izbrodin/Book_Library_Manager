package Dialogs;

import Entities.User;
import Utils.HashGenerator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserDialog extends JDialog {
    private JPanel mainPanel;
    private JButton leftButton;
    private JButton buttonCancel;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField usernameField;
    private JLabel fullNameLabel;
    private User user;

    // dependencies
    private final HashGenerator hashGenerator;

    public enum DialogType {
        CREATE,
        LOGIN,
        EDIT
    }

    public UserDialog(DialogType type, String username, String fullName, HashGenerator hashGenerator) {

        assert hashGenerator != null;
        this.hashGenerator = hashGenerator;

        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        switch (type) { // TODO: use enum + case with other dialogs
            case CREATE -> {
                leftButton.setText("Create");
            }
            case EDIT -> {
                leftButton.setText("Save");
                usernameField.setText(username);
                fullNameField.setText(fullName);
            }
            case LOGIN -> {

                leftButton.setText("Log In");
                fullNameLabel.setVisible(false);
                fullNameField.disable();
                fullNameField.setVisible(false);
                passwordField.setToolTipText(null);
            }
        }

        leftButton.addActionListener(e -> onOK(type));

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    public static User getUser(DialogType type, String username, String fullName, HashGenerator hashGenerator) {

        UserDialog userDialog = new UserDialog(type, username, fullName, hashGenerator);
        userDialog.pack();
        userDialog.setLocationRelativeTo(null);

        switch (type) {
            case CREATE -> userDialog.setTitle("Create user");
            case EDIT -> userDialog.setTitle("Edit user");
            case LOGIN -> userDialog.setTitle("Log in");
        }

        userDialog.show();
        return userDialog.user;
    }

    private void onOK(DialogType type) {

        if (type == DialogType.CREATE && (usernameField.getText().isBlank() || fullNameField.getText().isBlank() || passwordField.getText().isBlank())) {
            return;
        } else if (type == DialogType.EDIT && (usernameField.getText().isBlank() || fullNameField.getText().isBlank())) {
            return;
        } else if (type == DialogType.LOGIN && (usernameField.getText().isBlank() || passwordField.getText().isBlank())) {
            return;
        }

        String username = usernameField.getText();
        String full_name = fullNameField.getText();
        String password = null;
        if (!passwordField.getText().isBlank()) {
            password = hashGenerator.getHashValue(String.valueOf(passwordField.getPassword()));
        }
        user = new User(username, full_name, password);

        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
