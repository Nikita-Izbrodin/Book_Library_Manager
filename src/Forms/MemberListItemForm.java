package Forms;

import Entities.Member;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public class MemberListItemForm {
    private JPanel memberItemPanel;
    private JLabel nameLabel;
    private JLabel surnameLabel;
    private JLabel phoneNoLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;
    private JLabel postcodeLabel;
    private JLabel idLabel;

    public JPanel getPanel(){
        return memberItemPanel;
    }

    public void setData(Member member) {
        idLabel.setText("ID: "+member.id());
        nameLabel.setText("Name: "+member.name());
        surnameLabel.setText("Surname: "+member.surname());
        phoneNoLabel.setText("Phone No: "+member.phoneNo());
        emailLabel.setText("Email: "+member.email());
        addressLabel.setText("Address: "+member.address());
        postcodeLabel.setText("Postcode: "+member.postcode());
    }

    protected void setBackground(boolean isSelected) {
        memberItemPanel.setBackground(isSelected ? Color.blue : Color.lightGray);

        Color foregroundColor = isSelected ? Color.white : Color.BLACK;

        idLabel.setForeground(foregroundColor);
        nameLabel.setForeground(foregroundColor);
        surnameLabel.setForeground(foregroundColor);
        phoneNoLabel.setForeground(foregroundColor);
        emailLabel.setForeground(foregroundColor);
        addressLabel.setForeground(foregroundColor);
        postcodeLabel.setForeground(foregroundColor);
    }
}
