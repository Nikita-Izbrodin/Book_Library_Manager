package Dialogs;

import Entities.Book;
import DataAccess.LibraryDatabase;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

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
    private LibraryDatabase libraryDB;

    public BookDialog(String type, String title, String author, String isbn, String quantity, LibraryDatabase libraryDB) {
        this.libraryDB = libraryDB;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        if (type.equals("create")) {
            leftButton.setText("Create");
        } else if (type.equals("edit")) {
            leftButton.setText("Save");
            titleField.setText(title);
            authorField.setText(author);
            isbnField.setText(isbn);
            quantityField.setText(quantity);
        }

        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

    private void onOK() {
        if (titleField.getText().isBlank() || authorField.getText().isBlank() || quantityField.getText().isBlank()) {
            return;
        }
        try {
            if (this.libraryDB.doesBookExist(titleField.getText(), authorField.getText(), isbnField.getText())) {
                JOptionPane.showMessageDialog(null, "Entities.Book already exists in the database.", "Cannot add book", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        book = new Book(title, author, isbn, quantity);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Book getBook() {
        return this.book;
    }

}
