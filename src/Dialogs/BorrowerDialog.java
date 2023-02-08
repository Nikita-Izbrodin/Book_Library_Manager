package Dialogs;

import Entities.Borrower;
import DataAccess.LibraryDatabase;

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
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;

public class BorrowerDialog extends JDialog {
    private JPanel contentPane;
    private JTextField memberIDField;
    private JTextField returnDateField;
    private JButton leftButton;
    private JButton cancelButton;
    private Borrower borrower;

    // dependencies
    private final LibraryDatabase libraryDB;

    public enum DialogType {
        CREATE,
        EDIT
    }

    public BorrowerDialog(DialogType type, int memberID, LocalDate returnDate, int bookID, LibraryDatabase libraryDB) {

        this.libraryDB = libraryDB;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        switch (type) {
            case CREATE -> {
                leftButton.setText("Add Borrower");
            }
            case EDIT -> {
                leftButton.setText("Save");
                memberIDField.setText(String.valueOf(memberID));
                returnDateField.setText(String.valueOf(returnDate));
            }
        }

        leftButton.addActionListener(e -> onOK(bookID, type));

        cancelButton.addActionListener(e -> onCancel());

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

    public static Borrower getBorrower(DialogType type, int memberID, LocalDate returnDate, int bookID, LibraryDatabase libraryDB) {
        BorrowerDialog borrowerDialog = new BorrowerDialog(type, memberID, returnDate, bookID, libraryDB);
        borrowerDialog.pack();
        borrowerDialog.setLocationRelativeTo(null);
        switch (type) {
            case CREATE -> borrowerDialog.setTitle("Create borrower");
            case EDIT -> borrowerDialog.setTitle("Edit borrower");
        }
        borrowerDialog.show();
        return borrowerDialog.borrower;
    }

    private void onOK(int bookID, DialogType type) {

        if (memberIDField.getText().isBlank() || returnDateField.getText().isBlank()) {
            return;
        }

        try {

            if (this.libraryDB.isBookBorrowedByMember(bookID, Integer.parseInt(memberIDField.getText())) && type == DialogType.CREATE) {
                JOptionPane.showMessageDialog(null, "This member has already borrowed this book.", "Cannot create borrower", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int memberID = Integer.parseInt(memberIDField.getText());

            LocalDate returnDate = LocalDate.parse(returnDateField.getText());
            LocalDate currentDate = java.time.LocalDate.now();
            if (returnDate.isBefore(currentDate) || returnDate.equals(currentDate)) {
                JOptionPane.showMessageDialog(null, "Return date must be in the future.", "Invalid return date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            borrower = new Borrower(bookID, memberID, null, returnDate);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Member ID must be a number.", "Invalid member ID", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeException ex) {
            JOptionPane.showMessageDialog(null, "Return date must be in the following format:\nYYYY-MM-DD", "Invalid return date", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }
}
