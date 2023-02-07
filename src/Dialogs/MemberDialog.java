package Dialogs;

import Entities.Member;
import Utils.EmailAddressChecker;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MemberDialog extends JDialog {
    private JPanel contentPane;
    private JButton leftButton;
    private JButton buttonCancel;
    private JTextField postcodeTextField;
    private JTextField nameTextField;
    private JTextField surnameTextField;
    private JTextField phoneNoTextField;
    private JTextField emailTextField;
    private JTextField addressTextField;
    private JTextField idTextField;
    private Member member;

    // dependencies
    private final EmailAddressChecker emailAddressChecker;

    public enum DialogType {
        CREATE,
        EDIT
    }

    public MemberDialog(DialogType type, int id, String name, String surname, String phone, String email, String address, String postcode, EmailAddressChecker emailAddressChecker) {

        this.emailAddressChecker = emailAddressChecker;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        if (type == DialogType.CREATE) {
            leftButton.setText("Create");
        } else if (type == DialogType.EDIT) {
            leftButton.setText("Save");
            idTextField.setText(String.valueOf(id));
            nameTextField.setText(name);
            surnameTextField.setText(surname);
            phoneNoTextField.setText(phone);
            emailTextField.setText(email);
            addressTextField.setText(address);
            postcodeTextField.setText(postcode);
        }

        leftButton.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static Member getMember(DialogType type, int id, String name, String surname, String phone, String email, String address, String postcode, EmailAddressChecker emailAddressChecker) {

        MemberDialog memberDialog = new MemberDialog(type, id, name, surname, phone, email, address, postcode, emailAddressChecker);
        memberDialog.pack();
        memberDialog.setLocationRelativeTo(null);

        switch (type) {
            case CREATE -> memberDialog.setTitle("Create member");
            case EDIT -> memberDialog.setTitle("Edit member");
        }

        memberDialog.show();
        return memberDialog.member;
    }

    private void onOK() {
        if (idTextField.getText().isBlank() || nameTextField.getText().isBlank() || surnameTextField.getText().isBlank() || addressTextField.getText().isBlank() || postcodeTextField.getText().isBlank()) { // phone num and email not checked because some members may not have one
            return;
        }
        if (!emailAddressChecker.isValidEmailAddress(emailTextField.getText())) {
            JOptionPane.showMessageDialog(null, emailTextField.getText() + " doesn't look like a valid email address.\n\nPlease check and correct.", "Invalid email address", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idTextField.getText());
        } catch (Exception ex) { // if idTextField contains a string
            return;
        }
        String name = nameTextField.getText();
        String surname = surnameTextField.getText();
        String phoneNo = phoneNoTextField.getText();
        String email = emailTextField.getText();
        String address = addressTextField.getText();
        String postcode = postcodeTextField.getText();
        member = new Member(id, name, surname, phoneNo, email, address, postcode);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
