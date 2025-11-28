package Entities;

import java.time.LocalDate;

public class Patient {
    private int patientId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String amka;

    public Patient() {}

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.lastName = lastName.trim();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future!" );
        }
        this.dateOfBirth = dateOfBirth;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender != null) {
            gender.toUpperCase();
            if (!gender.equals("M") && !gender.equals("F") ) {
                throw new IllegalArgumentException("Gender must be 'M' or 'F'.");
            }
            this.gender = gender;
        } else {
            this.gender = null;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null) {
            if (!phone.matches("\\d+")) {   // digits only
                throw new IllegalArgumentException("Phone must contain digits only.");
            }
            this.phone = phone;
        } else {
            this.phone = null;
        }
    }

    public String getAmka() {
        return amka;
    }

    public void setAmka(String amka) {
        this.amka = amka;
    }

    @Override
    public String toString() {
        String s = String.format("%-4d %-15s %-15s %-12s %-8s %-15s %-15s%n",
                patientId, firstName, lastName, dateOfBirth, gender, phone, amka);
        return s;
    }
}
