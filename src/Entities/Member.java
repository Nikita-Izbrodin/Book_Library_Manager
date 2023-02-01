package Entities;

public class Member {

    private final int id;
    private final String name;
    private final String surname;
    private final String phoneNo;
    private final String email;
    private final String address;
    private final String postcode;

    public Member(int id, String name, String surname, String phoneNo, String email, String address, String postcode) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNo = phoneNo;
        this.email = email;
        this.address = address;
        this.postcode = postcode;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPostcode() {
        return postcode;
    }
}

/*public record Member(int id, String name, String surname, String phoneNo, String email, String address, String postcode) {

}*/
