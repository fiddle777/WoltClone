package com.example.l3android.model;


import java.time.LocalDate;


public class Review {

    private int id;
    private int rating;
    private String reviewText;
    private LocalDate dateCreated;
    private String authorLabel;
    private String timeLabel;


    public Review(int id, int rating, String reviewText, LocalDate dateCreated) {
        this.id = id;
        this.rating = rating;
        this.reviewText = reviewText;
        this.dateCreated = dateCreated;
    }

    public Review() {
    }

    public Review(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (authorLabel != null && !authorLabel.isEmpty()) {
            sb.append("[")
                    .append(authorLabel);
            if (timeLabel != null && !timeLabel.isEmpty()) {
                sb.append(" @ ").append(timeLabel);
            }
            sb.append("] ");
        }

        if (reviewText != null && !reviewText.isEmpty()) {
            sb.append(reviewText);
        }

        if (dateCreated != null) {
            sb.append(" (").append(dateCreated).append(")");
        }

        if (sb.length() == 0 && reviewText != null) {
            return reviewText;
        }
        return sb.toString();
    }


    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getAuthorLabel() {
        return authorLabel;
    }

    public void setAuthorLabel(String authorLabel) {
        this.authorLabel = authorLabel;
    }
}
