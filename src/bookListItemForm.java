import javax.swing.*;

public class bookListItemForm {
    private JPanel bookItemPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;

    public JPanel GetPanel(){
        return bookItemPanel;
    }

    public void SetData(Book entry) {
        titleLabel.setText("Title: "+ entry.getTitle());
        authorLabel.setText("Author: "+ entry.getAuthor());
        isbnLabel.setText("ISBN: "+ entry.getIsbn());
        quantityLabel.setText("Quantity: "+ entry.getQuantity());
    }

}
