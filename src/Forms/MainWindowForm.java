package Forms;

import CellRenderers.BookCellRenderer;
import CellRenderers.BorrowerCellRenderer;
import CellRenderers.MemberCellRenderer;
import CellRenderers.UserCellRenderer;
import Dialogs.BookDialog;
import Dialogs.BorrowerDialog;
import Dialogs.MemberDialog;
import Dialogs.UserDialog;
import Entities.Book;
import Entities.Borrower;
import Entities.Member;
import Entities.User;
import Utils.EmailAddressChecker;
import Utils.HashGenerator;
import DataAccess.LibraryDatabase;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindowForm {

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
    private final HashGenerator hashGenerator;
    private final EmailAddressChecker emailAddressChecker;
    private final LibraryDatabase libraryDB;

    public static void showMainWindow(HashGenerator hashGenerator, EmailAddressChecker emailAddressChecker, LibraryDatabase libraryDB) {
        assert hashGenerator != null;
        assert emailAddressChecker!= null;
        assert libraryDB != null;

        JFrame frame = new JFrame("Book Library Manager");
        MainWindowForm mainWindowForm = new MainWindowForm(hashGenerator, emailAddressChecker,libraryDB);
        frame.setContentPane(mainWindowForm.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 800));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public MainWindowForm(HashGenerator hashGenerator, EmailAddressChecker emailAddressChecker, LibraryDatabase libraryDB) { // constructor
        this.hashGenerator = hashGenerator;
        this.emailAddressChecker = emailAddressChecker;
        this.libraryDB = libraryDB;
        selectBooksBy();
        updateTotalMembers();
        updateTotalBooks();
        updateNumOfBooksBorrowed();
        updateNumOfBooksAvailable();

        addNewButton.addActionListener(e -> {
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
        });

        addBorrowerButton.addActionListener(e -> {
            if (titleLabel.getText().isBlank()) { // if nothing is selected
                return;
            }
            try {
                if (libraryDB.noMembers()) {
                    JOptionPane.showMessageDialog(null, "There are no members in the database.", "Cannot add borrower", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (Integer.parseInt(quantityLabel.getText()) - borrowerList.getModel().getSize() == 0) {
                    JOptionPane.showMessageDialog(null,"There are no available books.", "Cannot add borrower", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int bookID =  libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                Borrower newBorrower = BorrowerDialog.getBorrower(BorrowerDialog.DialogType.CREATE, -1, null, bookID, libraryDB);
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
        });

        editBookButton.addActionListener(e -> {
            if (titleLabel.getText().isBlank()) { // if nothing is selected
                return;
            }
            Book editedBook = BookDialog.getBook(BookDialog.DialogType.EDIT, titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
            if (editedBook == null) { // if cancel pressed
                return;
            }
            try {
                libraryDB.updateBook(editedBook.title(), editedBook.author(), String.valueOf(editedBook.isbn()), String.valueOf(editedBook.quantity()), libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()));
                JOptionPane.showMessageDialog(null, "Book updated successfully.", "Book update", JOptionPane.INFORMATION_MESSAGE);
                titleLabel.setText(editedBook.title());
                authorLabel.setText(editedBook.author());
                isbnLabel.setText(String.valueOf(editedBook.isbn()));
                quantityLabel.setText(String.valueOf(editedBook.quantity()));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
            selectBooksBy();
        });

        editMemberButton.addActionListener(e -> {
            if (nameLabel.getText().isBlank()) { // if nothing is selected
                return;
            }
            Member editedMember = MemberDialog.getMember(
                    MemberDialog.DialogType.EDIT,
                    Integer.parseInt(idLabel.getText()),
                    nameLabel.getText(),
                    surnameLabel.getText(),
                    phoneNoLabel.getText(),
                    emailLabel.getText(),
                    addressLabel.getText(),
                    postcodeLabel.getText(),
                    emailAddressChecker
            );
            if (editedMember == null) { // if cancel pressed
                return;
            }
            try {
                libraryDB.updateMember(editedMember.id(), editedMember.name(), editedMember.surname(), editedMember.phoneNo(), editedMember.email(), editedMember.address(), editedMember.postcode(), Integer.parseInt(idLabel.getText()));
                JOptionPane.showMessageDialog(null, "Member updated successfully.", "Member update", JOptionPane.INFORMATION_MESSAGE);
                idLabel.setText(String.valueOf(editedMember.id()));
                nameLabel.setText(editedMember.name());
                surnameLabel.setText(editedMember.surname());
                phoneNoLabel.setText(editedMember.phoneNo());
                emailLabel.setText(editedMember.email());
                addressLabel.setText(editedMember.address());
                postcodeLabel.setText(editedMember.postcode());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
            selectMembersBy();
        });

        editUserButton.addActionListener(e -> {
            if (usernameLabel.getText().isBlank()) { // if nothing is selected
                return;
            }
            try {
                // TODO: change!
                String oldPassword = (libraryDB.selectUsersByUsername(usernameLabel.getText())).get(0).password();
                User editedUser = UserDialog.getUser(UserDialog.DialogType.EDIT, usernameLabel.getText(), fullNameLabel.getText(), hashGenerator);
                if (editedUser == null) { // if cancel pressed
                    return;
                }
                if (editedUser.password() == null) { // TODO: check this change of removing setPassword works
                    libraryDB.updateUser(editedUser.username(), editedUser.fullName(), oldPassword, usernameLabel.getText());
                } else {
                    libraryDB.updateUser(editedUser.username(), editedUser.fullName(), editedUser.password(), usernameLabel.getText());
                }
                JOptionPane.showMessageDialog(null, "User updated successfully.", "User update", JOptionPane.INFORMATION_MESSAGE);
                usernameLabel.setText(editedUser.username());
                fullNameLabel.setText(editedUser.fullName());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
            selectUsersBy();
        });

        deleteBookButton.addActionListener(e -> {
            if (titleLabel.getText().isBlank()) { // if nothing is selected
                return;
            }
            int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?\nTitle: "+titleLabel.getText()+"\nAuthor: "+authorLabel.getText()+"\nISBN: "+isbnLabel.getText()+"\nQuantity: "+quantityLabel.getText(), "Delete book", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                return;
            }
            try {
                int bookID = libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                if (!libraryDB.selectBorrowersByBookID(bookID).isEmpty()) { // if this book has been borrowed
                    JOptionPane.showMessageDialog(null,"Cannot delete this book.\nThis book is being borrowed.", "Delete failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    libraryDB.deleteBook(libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()));
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
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteMemberButton.addActionListener(e -> {
            if (nameLabel.getText().isBlank()) { // if nothing is selected
                return;
            }
            int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this member?\nName: "+nameLabel.getText()+"\nSurname: "+surnameLabel.getText()+"\nPhone No: "+phoneNoLabel.getText()+"\nEmail: "+emailLabel.getText()+"\nAddress: "+addressLabel.getText()+"\nPostcode: "+postcodeLabel.getText(), "Delete member", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                return;
            }
            try {
                if (libraryDB.hasMemberBorrowedBook(Integer.parseInt(idLabel.getText()))) {
                    JOptionPane.showMessageDialog(null,"Cannot delete this member.\nThis member has borrowed a book.", "Delete failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    libraryDB.deleteMember(Integer.parseInt(idLabel.getText()));
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
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteUserButton.addActionListener(e -> {
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
        });

        bookList.addMouseListener(new MouseAdapter() { // when item in list is selected
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (bookList.getSelectedValue() == null) {
                    return;
                }
                titleLabel.setText(bookList.getSelectedValue().title());
                authorLabel.setText(bookList.getSelectedValue().author());
                isbnLabel.setText(bookList.getSelectedValue().isbn());
                quantityLabel.setText(String.valueOf(bookList.getSelectedValue().quantity()));
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
                idLabel.setText(String.valueOf(memberList.getSelectedValue().id()));
                nameLabel.setText(memberList.getSelectedValue().name());
                surnameLabel.setText(memberList.getSelectedValue().surname());
                phoneNoLabel.setText(memberList.getSelectedValue().phoneNo());
                emailLabel.setText(memberList.getSelectedValue().email());
                addressLabel.setText(memberList.getSelectedValue().address());
                postcodeLabel.setText(memberList.getSelectedValue().postcode());
            }
        });

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (userList.getSelectedValue() == null) {
                    return;
                }
                usernameLabel.setText(userList.getSelectedValue().username());
                fullNameLabel.setText(userList.getSelectedValue().fullName());
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

        // updates list when the searchByComboBox value is changed
        searchByComboBox.addActionListener(e -> {
            selectBooksBy();
            selectMembersBy();
            selectUsersBy();
        });

        // sets specific values in model of searchByComboBox when menuComboBox value is changed & updates list
        menuComboBox.addActionListener(e -> {
            selectedParentCardPanel.removeAll();
            searchResultsParentCardPanel.removeAll();
            if (menuComboBox.getSelectedItem() == null) {
                return;
            }
            String menuOption = menuComboBox.getSelectedItem().toString();
            switch (menuOption) {
                case "Books" -> showBookCardPanel();
                case "Members" -> showMemberCardPanel();
                case "Users" -> showUserCardPanel();
            }
            selectedParentCardPanel.repaint();
            selectedParentCardPanel.revalidate();
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
        ArrayList<String> types = new ArrayList<>(Arrays.asList("ID", "Name", "Surname", "Phone No", "Email", "Address", "Postcode"));
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
            String fullName = this.libraryDB.selectMemberNameSurnameByMemberID(borrowerList.getSelectedValue().memberID());

            int bookReturned = JOptionPane.showConfirmDialog(
                    null,
                    "Has "+fullName+" returned "+titleLabel.getText()+"?",
                    "Book returned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (bookReturned == 0) { // if "Yes" pressed
                this.libraryDB.deleteBorrower(borrowerList.getSelectedValue().bookID(), borrowerList.getSelectedValue().memberID());
                displayBorrowers();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editBorrower() {
        try {
            Borrower editedBorrower = BorrowerDialog.getBorrower(
                    BorrowerDialog.DialogType.EDIT,
                    borrowerList.getSelectedValue().memberID(),
                    borrowerList.getSelectedValue().returnDate(),
                    this.libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()),
                    this.libraryDB
            );
            if (editedBorrower == null) { // if cancel pressed
                return;
            }
            this.libraryDB.updateBorrower(editedBorrower.memberID(), editedBorrower.returnDate(), this.libraryDB.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText()), borrowerList.getSelectedValue().memberID());
            JOptionPane.showMessageDialog(null, "Borrow updated successfully.", "Borrow update", JOptionPane.INFORMATION_MESSAGE);
            displayBorrowers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewUser() {
        User newUser = UserDialog.getUser(UserDialog.DialogType.CREATE, null, null, this.hashGenerator); // TODO: "get" for other entities
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
        Member newMember = MemberDialog.getMember(
                MemberDialog.DialogType.CREATE,
                -1,
                null,
                null,
                null,
                null,
                null,
                null,
                this.emailAddressChecker
        );
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
        Book newBook = BookDialog.getBook(BookDialog.DialogType.CREATE, null, null, null, null);
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
            switch (searchBy) {
                case "Title" -> booksList = this.libraryDB.selectBooksByTitle(searchTextField.getText());
                case "Author" -> booksList = this.libraryDB.selectBooksByAuthor(searchTextField.getText());
                case "ISBN" -> booksList = this.libraryDB.selectBooksByISBN(searchTextField.getText());
            }
            if (booksList == null) {
                return;
            }
            Book[] booksArray = booksList.toArray(new Book[0]);
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
            switch (searchBy) {
                case "ID" -> membersList = this.libraryDB.selectMembersByID(Integer.parseInt(searchTextField.getText()));
                case "Name" -> membersList = this.libraryDB.selectMembersByName(searchTextField.getText());
                case "Surname" -> membersList = this.libraryDB.selectMembersBySurname(searchTextField.getText());
                case "Phone No" -> membersList = this.libraryDB.selectMembersByPhoneNo(searchTextField.getText());
                case "Email" -> membersList = this.libraryDB.selectMembersByEmail(searchTextField.getText());
                case "Address" -> membersList = this.libraryDB.selectMembersByAddress(searchTextField.getText());
                case "Postcode" -> membersList = this.libraryDB.selectMembersByPostcode(searchTextField.getText());
            }
            if (membersList == null) {
                return;
            }
            Member[] membersArray = membersList.toArray(new Member[0]);
            memberList.setListData(membersArray);
            memberList.setCellRenderer(new MemberCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            // do nothing
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
        Borrower[] borrowersArray = borrowersList.toArray(new Borrower[0]);
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
            User[] usersArray = usersList.toArray(new User[0]);
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
