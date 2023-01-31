package Dialogs;

import Entities.User;
import Utils.HashGenerator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private User user;

    private HashGenerator hashGenerator;

    public enum DialogType {
        CREATE,
        LOGIN,
        EDIT
    }

    public UserDialog(DialogType type, String username, String fullName, HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;

        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        switch (type) {
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
                fullNameField.disable();
                fullNameField.setBackground(Color.lightGray);
                fullNameField.setToolTipText("This field is disabled for log in.");
                passwordField.setToolTipText(null);
            }
        }

        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(type);
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

    public static User getUser(DialogType type, HashGenerator hashGenerator) {
        UserDialog userDialog = new UserDialog(type, null, null, hashGenerator);
        userDialog.pack();
        userDialog.setLocationRelativeTo(null);
        userDialog.show();
        User newUser = userDialog.getUser();
        return newUser;
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
        if ((type == DialogType.EDIT || type == DialogType.LOGIN) && !passwordField.getText().isBlank()) {
            password = hashGenerator.getHashValue(String.valueOf(passwordField.getPassword()));
        }
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
