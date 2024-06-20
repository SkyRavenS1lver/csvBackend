package com.mycompany.myapp.domain;

import com.networknt.schema.ValidationMessage;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import java.util.Date;
import java.util.Set;

public class Patient {

    @CsvBindByName(column = "first_name")
    private String first_name;

    @CsvBindByName(column = "last_name")
    private String last_name;

    @CsvBindByName(column = "gender")
    private String gender;

    @CsvBindByName(column = "dob")
    //    @CsvDate(value = "dd/MM/yyyy")
    private String dob;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "suburb")
    private String suburb;

    @CsvBindByName(column = "state")
    private String state;

    @CsvBindByName(column = "postcode")
    private String postcode;

    @CsvBindByName(column = "phone")
    private String phone;

    @CsvBindByName(column = "email")
    private String email;

    private Set<ValidationMessage> errors;

    public Set<ValidationMessage> getErrors() {
        return errors;
    }

    public void setErrors(Set<ValidationMessage> errors) {
        this.errors = errors;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return (
            "Patient{" +
            "first_name='" +
            first_name +
            '\'' +
            ", last_name='" +
            last_name +
            '\'' +
            ", gender='" +
            gender +
            '\'' +
            ", dob='" +
            dob +
            '\'' +
            ", address='" +
            address +
            '\'' +
            ", suburb='" +
            suburb +
            '\'' +
            ", state='" +
            state +
            '\'' +
            ", postcode=" +
            postcode +
            ", phone='" +
            phone +
            '\'' +
            ", email='" +
            email +
            '\'' +
            '}'
        );
    }
}
