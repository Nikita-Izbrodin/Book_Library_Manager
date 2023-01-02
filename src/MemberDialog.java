import javax.swing.*;
import java.awt.event.*;

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

    public MemberDialog(String type, int id, String name, String surname, String phone, String email, String address, String postcode) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        if (type.equals("create")) {
            leftButton.setText("Create");
        } else if (type.equals("edit")) {
            leftButton.setText("Edit");
            idTextField.setText(String.valueOf(id));
            nameTextField.setText(name);
            surnameTextField.setText(surname);
            phoneNoTextField.setText(phone);
            emailTextField.setText(email);
            addressTextField.setText(address);
            postcodeTextField.setText(postcode);
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (idTextField.getText().isBlank() || nameTextField.getText().isBlank() || surnameTextField.getText().isBlank() || addressTextField.getText().isBlank() || postcodeTextField.getText().isBlank()) { // phone num and email not checked because some members may not have one
            return;
        }
        int id = -1;
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

    public Member getMember() {
        return this.member;
    }

}
