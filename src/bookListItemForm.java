import javax.swing.*;

public class bookListItemForm {
    private JPanel bookItemPanel;
    private JLabel authorLabel;
    private JLabel titleLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;

    public JPanel GetPanel(){
        return bookItemPanel;
    }

    public void SetData(Book entry) {
        authorLabel.setText("Author: "+ entry.getAuthor());
        titleLabel.setText("Title: "+ entry.getTitle());
        isbnLabel.setText("ISBN: "+ entry.getIsbn());
        quantityLabel.setText("Quantity: "+ entry.getQuantity());
    }

}
