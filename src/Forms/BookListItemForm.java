package Forms;

import Entities.Book;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public class BookListItemForm {

    private JPanel bookItemPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;

    public JPanel getPanel() {
        return bookItemPanel;
    }

    protected void setData(Book entry) {
        titleLabel.setText("Title: "+ entry.getTitle());
        authorLabel.setText("Author: "+ entry.getAuthor());
        isbnLabel.setText("ISBN: "+ entry.getIsbn());
        quantityLabel.setText("Quantity: "+ entry.getQuantity());
    }

    protected void setBackground(boolean isSelected){

        bookItemPanel.setBackground(
                isSelected ?
                    Color.blue :
                    Color.lightGray);

        titleLabel.setForeground(
                isSelected ?
                    Color.white :
                    Color.BLACK);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
