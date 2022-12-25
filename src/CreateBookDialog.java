import javax.swing.*;
import java.awt.event.*;

public class CreateBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField quantityField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private Book newBook;

    public CreateBookDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
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

        if (titleField.getText().isEmpty() || authorField.getText().isEmpty() || isbnField.getText().isEmpty() || quantityField.getText().isEmpty()) {
            return;
        }

        String title = titleField.getText();
        String author = authorField.getText();
        int isbn = Integer.parseInt(isbnField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        newBook = new Book(title, author, isbn, quantity);

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Book getNewBook() {
        return this.newBook;
    }

}
