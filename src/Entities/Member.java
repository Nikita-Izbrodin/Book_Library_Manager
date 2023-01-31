package Entities;

public class Member {

    private int id;
    private String name;
    private String surname;
    private String phoneNo;
    private String email;
    private String address;
    private String postcode;

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
