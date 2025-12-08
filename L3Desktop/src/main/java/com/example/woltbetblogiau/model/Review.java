package com.example.woltbetblogiau.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int rating;
    private String reviewText;
    private LocalDate dateCreated;
    private LocalDateTime createdAt;
    @ManyToOne
    private BasicUser commentOwner;
    @ManyToOne
    private BasicUser feedbackUser;
    @ManyToOne
    private Chat chat;

    public Review(String reviewText, BasicUser commentOwner, Chat chat) {
        this.reviewText = reviewText;
        this.commentOwner = commentOwner;
        this.chat = chat;
        this.dateCreated = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }
    @Override
    public String toString() {
        String textPart = (reviewText != null) ? reviewText : "";
        String datePart = (dateCreated != null) ? (" (" + dateCreated.toString() + ")") : "";
        return textPart + datePart;
    }
    public String getAuthorLabel() {
        if (commentOwner == null) {
            return "Unknown";
        }

        String role;
        if (commentOwner.isAdmin()) {
            role = "Admin";
        } else if (commentOwner instanceof Driver) {
            role = "Driver";
        } else if (commentOwner instanceof Restaurant) {
            role = "Restaurant";
        } else {
            role = "Client";
        }

        String namePart = "";
        if (commentOwner.getName() != null) {
            namePart += commentOwner.getName();
        }
        if (commentOwner.getSurname() != null) {
            if (!namePart.isEmpty()) {
                namePart += " ";
            }
            namePart += commentOwner.getSurname();
        }
        if (namePart.isEmpty()) {
            return role;
        }
        return role + " " + namePart;
    }



    public String getTimeLabel() {
        if (createdAt == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return createdAt.format(formatter);
    }

}
