import javax.swing.*;
import java.awt.event.*;

public class BookDialog extends JDialog {
    private JPanel contentPane;
    private JButton leftButton;
    private JButton cancelButton;
    private JTextField quantityField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JPanel buttonsPanel;
    private Book book;

    public BookDialog(String type, String title, String author, String isbn, String quantity) {
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
        if (titleField.getText().isBlank() || authorField.getText().isBlank() || isbnField.getText().isBlank() || quantityField.getText().isBlank()) {
            return;
        }
        String title = titleField.getText();
        String author = authorField.getText();
        int isbn = Integer.parseInt(isbnField.getText());
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
