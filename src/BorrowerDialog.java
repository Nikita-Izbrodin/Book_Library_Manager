import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;

public class BorrowerDialog extends JDialog {
    private JPanel contentPane;
    private JTextField memberIDField;
    private JTextField returnDateField;
    private JButton leftButton;
    private JButton cancelButton;
    private Borrower borrower;

    public BorrowerDialog(String type, int memberID, String returnDate, int bookID) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        if (type.equals("create")) {
            leftButton.setText("Confirm");
        } else if (type.equals("edit")) {
            leftButton.setText("Save");
            memberIDField.setText(String.valueOf(memberID));
            returnDateField.setText(returnDate);
        }

        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(bookID);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
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

    private void onOK(int bookID) {
        if (memberIDField.getText().isBlank() || returnDateField.getText().isBlank()) {
            return;
        }
        LibraryDB db = new LibraryDB();
        try {
            if (!db.selectBorrowersByMemberID(bookID, Integer.parseInt(memberIDField.getText())).isEmpty()) {
                JOptionPane.showMessageDialog(null, "This member has already borrowed this book.", "Cannot create borrower", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {

        }
        int memberID = Integer.parseInt(memberIDField.getText());
        String returnDate = returnDateField.getText();
        borrower = new Borrower(bookID, memberID, returnDate);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Borrower getBorrower() {
        return this.borrower;
    }

}
