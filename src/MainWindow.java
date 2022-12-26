import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainWindow {

    private JPanel MainWindow;
    private JPanel statsPanel;
    private JPanel toolbarPanel;
    private JComboBox menuComboBox;
    private JPanel mainPanel;
    private JButton addNewButton;
    private JComboBox searchByComboBox;
    private JTextField textField1;
    private JList<Book> bookList;
    private JScrollBar scrollBar1;
    private JPanel allContentsPanel;
    private JPanel selectedParentCardPanel;
    private JPanel searchPanel;
    private JPanel bookCardPanel;
    private JPanel memberCardPanel;
    private JPanel staffCardPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;
    private JButton deleteBookButton;
    private JButton editBookButton;
    private JPanel bookEditCardPanel;
    private JTextField editTitleTextField;
    private JTextField editAuthorTextField;
    private JTextField editISBNTextField;
    private JTextField editQuantityTextField;
    private JButton editSaveButton;
    private JButton editCancelButton;
    private JLabel editTitleLabel;
    private JLabel editAuthorLabel;
    private JLabel editISBNLabel;
    private JLabel editQuantityLabel;

    public static void main(String[] args) {
        LibraryDB db = new LibraryDB();
        try {
            if (db.noStaff()){
                CreateUserDialog createUserDialog = new CreateUserDialog();
                createUserDialog.pack();
                createUserDialog.show();
                User newUser = createUserDialog.getNewUser();
                if (newUser == null) {
                    return; // exit from the application
                }
                db.createUser(newUser);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        showMainWindow();
    }

    private static void showMainWindow() {
        JFrame frame = new JFrame("MainWindow");
        MainWindow mainWindow = new MainWindow();
        frame.setContentPane(mainWindow.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MainWindow() {
        LibraryDB db = new LibraryDB();
        showAllBooks();

        // start of actionlisteners
        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateBookDialog createBookDialog = new CreateBookDialog();
                createBookDialog.pack();
                createBookDialog.show();
                Book newBook = createBookDialog.getNewBook();
                if (newBook == null) {
                    // exit from the application
                    return;
                }
                try {
                    db.createBook(newBook);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bookList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //System.out.println(bookList.getSelectedValue());
                // TODO: make selected item highlighted

                titleLabel.setText("Title: "+bookList.getSelectedValue().getTitle());
                authorLabel.setText("Author: "+bookList.getSelectedValue().getAuthor());
                isbnLabel.setText("ISBN: "+bookList.getSelectedValue().getIsbn());
                quantityLabel.setText("Quantity: "+bookList.getSelectedValue().getQuantity());
            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //super.keyTyped(e); // not sure why this line was added. might remove
                selectBooksBy();
            }
        });

        searchByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBooksBy();
            }
        });

        menuComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedParentCardPanel.removeAll();
                String menuOption = menuComboBox.getSelectedItem().toString();
                if (menuOption.equals("Books")) {
                    selectedParentCardPanel.add(bookCardPanel);
                } else if (menuOption.equals("Members")) {
                    selectedParentCardPanel.add(memberCardPanel);
                } else if (menuOption.equals("Staff")) {
                    selectedParentCardPanel.add(staffCardPanel);
                }
                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });

        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookList.getSelectedValue() == null) {
                    return;
                }
                selectedParentCardPanel.removeAll();
                selectedParentCardPanel.add(bookEditCardPanel);

                editTitleTextField.setText(bookList.getSelectedValue().getTitle());
                editAuthorTextField.setText(bookList.getSelectedValue().getAuthor());
                editISBNTextField.setText(String.valueOf(bookList.getSelectedValue().getIsbn()));
                editQuantityTextField.setText(String.valueOf(bookList.getSelectedValue().getQuantity()));

                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });

        editCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedParentCardPanel.removeAll();
                selectedParentCardPanel.add(bookCardPanel);
                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });

        editSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibraryDB db = new LibraryDB();
                try { // TODO: maybe change edit book into joptionpane so that search can't be used
                    db.updateBook(editTitleTextField.getText(), editAuthorTextField.getText(), editISBNTextField.getText(), editQuantityTextField.getText(),
                                  bookList.getSelectedValue().getTitle(), bookList.getSelectedValue().getAuthor(), String.valueOf(bookList.getSelectedValue().getIsbn()), String.valueOf(bookList.getSelectedValue().getQuantity()));
                    JOptionPane.showMessageDialog(null, "Book updated successfully.", "Book update", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectedParentCardPanel.removeAll();
                selectedParentCardPanel.add(bookCardPanel);

                // to update info on bookCardPanel
                titleLabel.setText("Title: "+editTitleTextField.getText());
                authorLabel.setText("Author: "+editAuthorTextField.getText());
                isbnLabel.setText("ISBN: "+String.valueOf(editISBNTextField.getText()));
                quantityLabel.setText("Quantity: "+String.valueOf(editQuantityTextField.getText()));

                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
                int index = bookList.getSelectedIndex();
                selectBooksBy(); // to update list after changes
                bookList.setSelectedIndex(index);
            }
        });
        // end of actionlisteners
    }

    private void selectBooksBy() {
        LibraryDB db = new LibraryDB();
        List<Book> booksList = null;
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Title")) {
                booksList = db.selectBooksByTitle(textField1.getText());

            } else if (searchBy.equals("Author")) {
                booksList = db.selectBooksByAuthor(textField1.getText());

            } else if (searchBy.equals("ISBN")) {
                booksList = db.selectBooksByISBN(textField1.getText());

            }
            Book[] booksArray = booksList.toArray(new Book[booksList.size()]);
            bookList.setListData(booksArray);
            bookList.setCellRenderer(new BookCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAllBooks() {
        LibraryDB db = new LibraryDB();
        try {
            List<Book> booksList = db.selectAllBooks();
            Book[] booksArray = booksList.toArray(new Book[booksList.size()]);
            bookList.setListData(booksArray);
            bookList.setCellRenderer(new BookCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
