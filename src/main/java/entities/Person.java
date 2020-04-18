package entities;

import lombok.Getter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Person {

    public Person(String firstName, String lastName, String country, String email, List<Date> availableDates) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.email = email;
        this.availableDates = availableDates;
    }

    @Getter private String firstName;
    @Getter private String lastName;
    @Getter private String country;
    @Getter private String email;
    @Getter private List<Date> availableDates;

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", availableDates=" + availableDates +
                '}';
    }
}
