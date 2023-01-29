import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindow {

    private JComboBox menuComboBox;
    private JPanel mainPanel;
    private JButton addNewButton;
    private JComboBox searchByComboBox;
    private JTextField searchTextField;
    private JList<Book> bookList;
    private JPanel searchResultsParentCardPanel;
    private JPanel selectedParentCardPanel;
    private JPanel bookCardPanel;
    private JPanel memberCardPanel;
    private JPanel userCardPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;
    private JButton deleteBookButton;
    private JButton editBookButton;
    private JButton editMemberButton;
    private JButton deleteMemberButton;
    private JLabel postcodeLabel;
    private JLabel phoneNoLabel;
    private JLabel surnameLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;
    private JPanel bookSearchCardPanel;
    private JPanel memberSearchCardPanel;
    private JPanel userSearchCardPanel;
    private JList<Member> memberList;
    private JList<Borrower> borrowerList;
    private JButton addBorrowerButton;
    private JLabel idLabel;
    private JButton deleteUserButton;
    private JButton editUserButton;
    private JList<User> userList;
    private JLabel usernameLabel;
    private JLabel fullNameLabel;
    private JLabel totalMembersLabel;
    private JLabel totalBooksLabel;
    private JLabel numOfBooksAvailableLabel;
    private JLabel numOfBooksBorrowedLabel;

    // dependencies
    private HashGenerator hashGenerator;
    private LibraryDatabase libraryDB;

    public static void showMainWindow(HashGenerator hashGenerator, LibraryDatabase libraryDB) {
        JFrame frame = new JFrame("Book Library Manager");
        MainWindow mainWindow = new MainWindow(hashGenerator, libraryDB);
        frame.setContentPane(mainWindow.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 800));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public MainWindow(HashGenerator hashGenerator, LibraryDatabase libraryDB) { // constructor
        this.hashGenerator = hashGenerator;
        this.libraryDB = libraryDB;
        selectBooksBy();
        updateTotalMembers();
        updateTotalBooks();
        updateNumOfBooksBorrowed();
        updateNumOfBooksAvailable();

        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuComboBox.getSelectedItem() == null) {
                    return;
                }
                if (menuComboBox.getSelectedItem().toString().equals("Books")) {
                    createNewBook();
                } else if (menuComboBox.getSelectedItem().toString().equals("Members")) {
                    createNewMember();
                } else if (menuComboBox.getSelectedItem().toString().equals("Users")) {
                    createNewUser();
                }
            }
        });

        addBorrowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                try {
                    if (libraryDB.noMembers()) {
                        JOptionPane.showMessageDialog(null, "There are no members in the database.", "Cannot add borrower", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    if (bookList.getSelectedValue().getQuantity() - borrowerList.getModel().getSize() == 0) {
                        JOptionPane.showMessageDialog(null,"There are no available books.", "Cannot add borrower", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    BorrowerDialog borrowerDialog = null;
                    int bookID =  libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                    borrowerDialog = new BorrowerDialog("create", -1, null, bookID, libraryDB);
                    borrowerDialog.pack();
                    borrowerDialog.setLocationRelativeTo(null);
                    borrowerDialog.show();
                    Borrower newBorrower = borrowerDialog.getBorrower();
                    if (newBorrower == null) {
                        return;
                    }
                    libraryDB.createBorrower(newBorrower);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                displayBorrowers();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();
            }
        });

        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                BookDialog bookDialog = new BookDialog("edit", titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText(), libraryDB);
                bookDialog.pack();
                bookDialog.setLocationRelativeTo(null);
                bookDialog.show();
                Book editedBook = bookDialog.getBook();
                if (editedBook == null) { // if cancel pressed
                    return;
                }
                try {
                    libraryDB.updateBook(editedBook.getTitle(), editedBook.getAuthor(), String.valueOf(editedBook.getIsbn()), String.valueOf(editedBook.getQuantity()), libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()));
                    JOptionPane.showMessageDialog(null, "Book updated successfully.", "Book update", JOptionPane.INFORMATION_MESSAGE);
                    titleLabel.setText(editedBook.getTitle());
                    authorLabel.setText(editedBook.getAuthor());
                    isbnLabel.setText(String.valueOf(editedBook.getIsbn()));
                    quantityLabel.setText(String.valueOf(editedBook.getQuantity()));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectBooksBy();
            }
        });

        editMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                MemberDialog memberDialog = new MemberDialog("edit", Integer.parseInt(idLabel.getText()),nameLabel.getText(), surnameLabel.getText(), phoneNoLabel.getText(), emailLabel.getText(), addressLabel.getText(), postcodeLabel.getText());
                memberDialog.pack();
                memberDialog.setLocationRelativeTo(null);
                memberDialog.show();
                Member editedMember = memberDialog.getMember();
                if (editedMember == null) { // if cancel pressed
                    return;
                }
                try {
                    libraryDB.updateMember(editedMember.getID(), editedMember.getName(), editedMember.getSurname(), editedMember.getPhoneNo(), editedMember.getEmail(), editedMember.getAddress(), editedMember.getPostcode(), Integer.parseInt(idLabel.getText()));
                    JOptionPane.showMessageDialog(null, "Member updated successfully.", "Member update", JOptionPane.INFORMATION_MESSAGE);
                    idLabel.setText(String.valueOf(editedMember.getID()));
                    nameLabel.setText(editedMember.getName());
                    surnameLabel.setText(editedMember.getSurname());
                    phoneNoLabel.setText(editedMember.getPhoneNo());
                    emailLabel.setText(editedMember.getEmail());
                    addressLabel.setText(editedMember.getAddress());
                    postcodeLabel.setText(editedMember.getPostcode());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectMembersBy();
            }
        });

        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usernameLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                try {
                    // TODO: change!
                    String oldPassword = (libraryDB.selectUsersByUsername(usernameLabel.getText())).get(0).getPassword();
                    User editedUser = UserDialog.getUser(UserDialog.DialogType.EDIT, hashGenerator);
                    if (editedUser == null) { // if cancel pressed
                        return;
                    }
                    if (editedUser.getPassword() == null) {
                        editedUser.setPassword(oldPassword);
                    }
                    libraryDB.updateUser(editedUser.getUsername(), editedUser.getFullName(), editedUser.getPassword(), usernameLabel.getText());
                    JOptionPane.showMessageDialog(null, "User updated successfully.", "User update", JOptionPane.INFORMATION_MESSAGE);
                    usernameLabel.setText(editedUser.getUsername());
                    fullNameLabel.setText(editedUser.getFullName());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectUsersBy();
            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?\nTitle: "+titleLabel.getText()+"\nAuthor: "+authorLabel.getText()+"\nISBN: "+isbnLabel.getText()+"\nQuantity: "+quantityLabel.getText(), "Delete book", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                    return;
                }
                try {
                    int bookID = libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                    libraryDB.deleteBorrowerByBookID(bookID); // removes foreign key to allow deletion
                    libraryDB.deleteBook(libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectBooksBy();
                displayBorrowers();
                titleLabel.setText("");
                authorLabel.setText("");
                isbnLabel.setText("");
                quantityLabel.setText("");
                updateTotalBooks();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();
            }
        });

        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this member?\nName: "+nameLabel.getText()+"\nSurname: "+surnameLabel.getText()+"\nPhone No: "+phoneNoLabel.getText()+"\nEmail: "+emailLabel.getText()+"\nAddress: "+addressLabel.getText()+"\nPostcode: "+postcodeLabel.getText(), "Delete member", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                    return;
                }
                try {
                    libraryDB.deleteBorrowerByMemberID(Integer.parseInt(idLabel.getText())); // removes foreign key to allow deletion
                    libraryDB.deleteMember(Integer.parseInt(idLabel.getText()));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectMembersBy();
                idLabel.setText("");
                nameLabel.setText("");
                surnameLabel.setText("");
                phoneNoLabel.setText("");
                emailLabel.setText("");
                addressLabel.setText("");
                postcodeLabel.setText("");
                updateTotalMembers();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usernameLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?\nUsername: "+usernameLabel.getText()+"\nFull Name: "+fullNameLabel.getText(), "Delete user", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                    return;
                }
                try {
                    libraryDB.deleteUser(usernameLabel.getText());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectUsersBy();
                usernameLabel.setText("");
                fullNameLabel.setText("");
            }
        });

        bookList.addMouseListener(new MouseAdapter() { // when item in list is selected
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // TODO: make selected item highlighted
                if (bookList.getSelectedValue() == null) {
                    return;
                }
                titleLabel.setText(bookList.getSelectedValue().getTitle());
                authorLabel.setText(bookList.getSelectedValue().getAuthor());
                isbnLabel.setText(bookList.getSelectedValue().getIsbn());
                quantityLabel.setText(String.valueOf(bookList.getSelectedValue().getQuantity()));
                displayBorrowers();
            }
        });

        memberList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (memberList.getSelectedValue() == null) {
                    return;
                }
                idLabel.setText(String.valueOf(memberList.getSelectedValue().getID()));
                nameLabel.setText(memberList.getSelectedValue().getName());
                surnameLabel.setText(memberList.getSelectedValue().getSurname());
                phoneNoLabel.setText(memberList.getSelectedValue().getPhoneNo());
                emailLabel.setText(memberList.getSelectedValue().getEmail());
                addressLabel.setText(memberList.getSelectedValue().getAddress());
                postcodeLabel.setText(memberList.getSelectedValue().getPostcode());
            }
        });

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (userList.getSelectedValue() == null) {
                    return;
                }
                usernameLabel.setText(userList.getSelectedValue().getUsername());
                fullNameLabel.setText(userList.getSelectedValue().getFullName());
            }
        });

        borrowerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (borrowerList.getSelectedValue() == null) {
                    return;
                }
                String[] options = {"Edit", "Book returned"};
                int answer = JOptionPane.showOptionDialog(
                        null,
                        "Choose an option",
                        "Edit / Returned",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options, // custom buttons
                        options[1] // default button
                );
                if (answer == 0) { // if "Edit" pressed
                    editBorrower();
                } else if (answer == 1) { // if "Book returned" pressed
                    returnBook();
                }
            }
        });

        searchByComboBox.addActionListener(new ActionListener() { // updates list when the searchByComboBox value is changed
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBooksBy();
                selectMembersBy();
                selectUsersBy();
            }
        });

        menuComboBox.addActionListener(new ActionListener() { // sets specific values in model of searchByComboBox when menuComboBox value is changed & updates list
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedParentCardPanel.removeAll();
                searchResultsParentCardPanel.removeAll();
                if (menuComboBox.getSelectedItem() == null) {
                    return;
                }
                String menuOption = menuComboBox.getSelectedItem().toString();
                if (menuOption.equals("Books")) {
                    showBookCardPanel();
                } else if (menuOption.equals("Members")) {
                    showMemberCardPanel();
                } else if (menuOption.equals("Users")) {
                    showUserCardPanel();
                }
                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //super.keyTyped(e); // TODO: not sure why this line was added. might remove
                selectBooksBy();
                selectMembersBy();
                selectUsersBy();
            }
        });
    }

    private void showUserCardPanel() {
        selectedParentCardPanel.add(userCardPanel);
        searchResultsParentCardPanel.add(userSearchCardPanel);
        DefaultComboBoxModel userModel = (DefaultComboBoxModel) searchByComboBox.getModel();
        userModel.removeAllElements();
        ArrayList<String> types = new ArrayList<>(Arrays.asList("Username", "Full Name"));
        for (String type : types) {
            userModel.addElement(type);
        }
        searchByComboBox.setModel(userModel);
        selectUsersBy();
    }

    private void showMemberCardPanel() {
        selectedParentCardPanel.add(memberCardPanel);
        searchResultsParentCardPanel.add(memberSearchCardPanel);
        DefaultComboBoxModel memberModel = (DefaultComboBoxModel) searchByComboBox.getModel();
        memberModel.removeAllElements();
        ArrayList<String> types = new ArrayList<>(Arrays.asList("Name", "Surname", "Phone No", "Email", "Address", "Postcode"));
        for (String type : types) {
            memberModel.addElement(type);
        }
        searchByComboBox.setModel(memberModel);
        selectMembersBy();
    }

    private void showBookCardPanel() {
        selectedParentCardPanel.add(bookCardPanel);
        searchResultsParentCardPanel.add(bookSearchCardPanel);
        DefaultComboBoxModel bookModel = (DefaultComboBoxModel) searchByComboBox.getModel();
        bookModel.removeAllElements();
        ArrayList<String> types = new ArrayList<>(Arrays.asList("Title", "Author", "ISBN"));
        for (String type : types) {
            bookModel.addElement(type);
        }
        searchByComboBox.setModel(bookModel);
        selectBooksBy();
        displayBorrowers();
    }

    private void returnBook() {
        try {
            String fullName = this.libraryDB.selectMemberNameSurnameByMemberID(borrowerList.getSelectedValue().getMemberID());

            int bookReturned = JOptionPane.showConfirmDialog(
                    null,
                    "Has "+fullName+" returned "+titleLabel.getText()+"?",
                    "Book returned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (bookReturned == 0) { // if "Yes" pressed
                this.libraryDB.deleteBorrower(borrowerList.getSelectedValue().getBookID(), borrowerList.getSelectedValue().getMemberID());
                displayBorrowers();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editBorrower() {
        BorrowerDialog borrowerDialog = null;
        try {
            borrowerDialog = new BorrowerDialog(
                    "edit",
                    borrowerList.getSelectedValue().getMemberID(),
                    borrowerList.getSelectedValue().getReturnDate(),
                    this.libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()),
                    this.libraryDB);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        borrowerDialog.pack();
        borrowerDialog.setLocationRelativeTo(null);
        borrowerDialog.show();
        Borrower editedBorrower = borrowerDialog.getBorrower();
        if (editedBorrower == null) { // if cancel pressed
            return;
        }
        try {
            this.libraryDB.updateBorrower(editedBorrower.getMemberID(), editedBorrower.getReturnDate(), this.libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()), borrowerList.getSelectedValue().getMemberID());
            JOptionPane.showMessageDialog(null, "Borrow updated successfully.", "Borrow update", JOptionPane.INFORMATION_MESSAGE);
            displayBorrowers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewUser() {
        User newUser = UserDialog.getUser(UserDialog.DialogType.CREATE, this.hashGenerator);
        if (newUser == null) {
            return;
        }
        try {
            this.libraryDB.createUser(newUser);
            JOptionPane.showMessageDialog(null, "User created successfully.", "User create", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        selectUsersBy();
    }

    private void createNewMember() {
        MemberDialog memberDialog = new MemberDialog("create", -1, null, null, null, null, null, null);
        memberDialog.pack();
        memberDialog.setLocationRelativeTo(null);
        memberDialog.show();
        Member newMember = memberDialog.getMember();
        if (newMember == null) {
            return;
        }
        try {
            this.libraryDB.createMember(newMember);
            JOptionPane.showMessageDialog(null, "Member created successfully.", "Member create", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        selectMembersBy();
        updateTotalMembers();
    }

    private void createNewBook() {
        BookDialog bookDialog = new BookDialog("create", null, null, null, null, this.libraryDB);
        bookDialog.pack();
        bookDialog.setLocationRelativeTo(null);
        bookDialog.show();
        Book newBook = bookDialog.getBook();
        if (newBook == null) { // cancel pressed
            return;
        }
        try {
            libraryDB.createBook(newBook);
            JOptionPane.showMessageDialog(null, "Book created successfully.", "Book create", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        selectBooksBy();
        updateTotalBooks();
        updateNumOfBooksAvailable();
    }


    private void selectBooksBy() { // updates list of displayed books
        List<Book> booksList = null;
        if (searchByComboBox.getSelectedItem() == null) { // not sure why this is needed
            return;
        }
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Title")) {
                booksList = this.libraryDB.selectBooksByTitle(searchTextField.getText());

            } else if (searchBy.equals("Author")) {
                booksList = this.libraryDB.selectBooksByAuthor(searchTextField.getText());

            } else if (searchBy.equals("ISBN")) {
                booksList = this.libraryDB.selectBooksByISBN(searchTextField.getText());
            }
            if (booksList == null) {
                return;
            }
            Book[] booksArray = booksList.toArray(new Book[booksList.size()]);
            bookList.setListData(booksArray);
            bookList.setCellRenderer(new BookCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectMembersBy() { // updates list of displayed members
        List<Member> membersList = null;
        if (searchByComboBox.getSelectedItem() == null) { // not sure why this is needed
            return;
        }
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Name")) {
                membersList = this.libraryDB.selectMembersByName(searchTextField.getText());
            } else if (searchBy.equals("Surname")) {
                membersList = this.libraryDB.selectMembersBySurname(searchTextField.getText());
            } else if (searchBy.equals("Phone No")) {
                membersList = this.libraryDB.selectMembersByPhoneNo(searchTextField.getText());
            } else if (searchBy.equals("Email")) {
                membersList = this.libraryDB.selectMembersByEmail(searchTextField.getText());
            } else if (searchBy.equals("Address")) {
                membersList = this.libraryDB.selectMembersByAddress(searchTextField.getText());
            } else if (searchBy.equals("Postcode")) {
                membersList = this.libraryDB.selectMembersByPostcode(searchTextField.getText());
            }
            if (membersList == null) {
                return;
            }
            Member[] membersArray = membersList.toArray(new Member[membersList.size()]);
            memberList.setListData(membersArray);
            memberList.setCellRenderer(new MemberCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayBorrowers() { // updates list of displayed borrowers
        List<Borrower> borrowersList = null;
        try {
            int bookID = this.libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
            borrowersList = this.libraryDB.selectBorrowersByBookID(bookID);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (borrowersList == null) {
            return;
        }
        Borrower[] borrowersArray = borrowersList.toArray(new Borrower[borrowersList.size()]);
        borrowerList.setListData(borrowersArray);
        borrowerList.setCellRenderer(new BorrowerCellRenderer());
    }

    private void selectUsersBy() { // updates list of displayed users
        List<User> usersList = null;
        if (searchByComboBox.getSelectedItem() == null) {
            return;
        }
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Username")) {
                usersList = this.libraryDB.selectUsersByUsername(searchTextField.getText());
            } else if (searchBy.equals("Full Name")) {
                usersList = this.libraryDB.selectUsersByFullName(searchTextField.getText());
            }
            if (usersList == null) {
                return;
            }
            User[] usersArray = usersList.toArray(new User[usersList.size()]);
            userList.setListData(usersArray);
            userList.setCellRenderer(new UserCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotalMembers() {
        int totalMembers = -1;
        try {
            totalMembers = this.libraryDB.countMembers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        totalMembersLabel.setText(String.valueOf(totalMembers));
    }

    private void updateTotalBooks() {
        int totalBooks = -1;
        try {
            totalBooks = this.libraryDB.countBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        totalBooksLabel.setText(String.valueOf(totalBooks));
    }

    private void updateNumOfBooksBorrowed() {
        int numOfBooksBorrowed = -1;
        try {
            numOfBooksBorrowed = this.libraryDB.countBorrowers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        numOfBooksBorrowedLabel.setText(String.valueOf(numOfBooksBorrowed));
    }

    private void updateNumOfBooksAvailable() {
        int totalBooks = -1;
        int numOfBooksBorrowed = -1;
        try {
            totalBooks = this.libraryDB.countBooks();
            numOfBooksBorrowed = this.libraryDB.countBorrowers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        numOfBooksAvailableLabel.setText(String.valueOf(totalBooks - numOfBooksBorrowed));
    }
}
