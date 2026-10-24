
package com.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class IssueRecord {

    private final String issueId;
    private final String studentId;
    private final String studentName;
    private final String bookId;
    private final String bookTitle;

    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    private static final double FINE_PER_DAY = 2.0;

    public IssueRecord(
            String issueId,
            String studentId,
            String studentName,
            String bookId,
            String bookTitle,
            LocalDate issueDate,
            LocalDate dueDate) {

        this.issueId = issueId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public String getIssueId() {
        return issueId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void returnBook(LocalDate date) {
        if (isReturned()) {
            throw new IllegalStateException(
                    "This book has already been returned.");
        }

        if (date.isBefore(issueDate)) {
            throw new IllegalArgumentException(
                    "Return date cannot be before issue date.");
        }

        returnDate = date;
    }

    public long getOverdueDays() {
        LocalDate dateToCheck =
                (returnDate != null) ? returnDate : LocalDate.now();

        if (!dateToCheck.isAfter(dueDate)) {
            return 0;
        }

        return ChronoUnit.DAYS.between(dueDate, dateToCheck);
    }

    public double getFine() {
        return getOverdueDays() * FINE_PER_DAY;
    }

    public String getStatus() {
        if (isReturned()) {
            return "Returned";
        }

        if (LocalDate.now().isAfter(dueDate)) {
            return "Overdue";
        }

        return "Issued";
    }
}
