package Dialogs;

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

    public enum DialogType {
        CREATE,
        EDIT
    }

    public BookDialog(DialogType type, String title, String author, String isbn, String quantity) {

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

        leftButton.addActionListener(e -> onOK());

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

    public static Book getBook(DialogType type, String title, String author, String isbn, String quantity) {

        BookDialog bookDialog = new BookDialog(type, title, author, isbn, quantity);
        bookDialog.pack();
        bookDialog.setLocationRelativeTo(null);

        switch (type) {
            case CREATE -> bookDialog.setTitle("Create book");
            case EDIT -> bookDialog.setTitle("Edit book");
        }

        bookDialog.show();
        return bookDialog.book;
    }

    private void onOK() {

        try {

            if (
                    titleField.getText().isBlank()
                    || authorField.getText().isBlank()
                    || isbnField.getText().isBlank()
                    || quantityField.getText().isBlank()
            ) {
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
                    "Quantity must be a number.",
                    "Invalid quantity",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onCancel() {
        dispose();
    }
}