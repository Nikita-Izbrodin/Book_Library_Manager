package Dialogs;

import DataAccess.LibraryDatabase;
import Entities.Book;

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

/**
 * A class for showing a book related dialog.
 * Returns data from the fields in the dialog in the form of Book.
 */
public class BookDialog extends JDialog {
    private JPanel contentPane;
    private JButton leftButton;
    private JButton cancelButton;
    private JTextField quantityField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private Book book;

    // dependencies
    private final LibraryDatabase libraryDatabase;

    public enum DialogType {
        CREATE,
        EDIT
    }

    private BookDialog(DialogType type,
                      String title, String author, String isbn, String quantity,
                      LibraryDatabase libraryDatabase
    ) {

        this.libraryDatabase = libraryDatabase;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        switch (type) {
            case CREATE -> {
                leftButton.setText("Create");
            }
            case EDIT -> {
                leftButton.setText("Save");

                titleField.setText(title);
                authorField.setText(author);
                isbnField.setText(isbn);
                quantityField.setText(quantity);
            }
        }

        leftButton.addActionListener(e -> onOK(type));

        cancelButton.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> {
            onCancel();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static Book getBook(DialogType type,
                               String title, String author, String isbn, String quantity,
                               LibraryDatabase libraryDatabase
    ) {

        BookDialog bookDialog = new BookDialog(type, title, author, isbn, quantity, libraryDatabase);
        bookDialog.pack();
        bookDialog.setLocationRelativeTo(null);

        switch (type) {
            case CREATE -> bookDialog.setTitle("Create book");
            case EDIT -> bookDialog.setTitle("Edit book");
        }

        bookDialog.show();
        return bookDialog.book;
    }

    private void onOK(DialogType type) {

        try {

            if (
                    titleField.getText().isBlank()
                    || authorField.getText().isBlank()
                    || isbnField.getText().isBlank()
                    || quantityField.getText().isBlank()
            ) {
                return;
            }

            if (
                    libraryDatabase.doesBookExist(titleField.getText(), authorField.getText(), isbnField.getText())
                    && type == DialogType.CREATE
            ) {
                JOptionPane.showMessageDialog(
                        null,
                        "This book already exists.",
                        "Invalid book",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (Integer.parseInt(quantityField.getText()) < 0 ) {
                JOptionPane.showMessageDialog(
                        null,
                        "Quantity must be a whole positive number.",
                        "Invalid quantity",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            book = new Book(title, author, isbn, quantity);

            dispose();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(
                    null,
                    "Quantity must be a whole positive number.",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onCancel() {
        dispose();
    }
}