
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Book> books = new ArrayList<>();
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Loan> loans = new ArrayList<>();

    static final int FINE_PER_DAY = 2;

    static class Book {
        String id, title, author;

        Book(String id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
        }
    }

    static class Student {
        String id, name;

        Student(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Loan {
        String studentId, bookId;
        LocalDate issueDate, dueDate, returnDate;

        Loan(String studentId, String bookId,
             LocalDate issueDate, LocalDate dueDate) {
            this.studentId = studentId;
            this.bookId = bookId;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.returnDate = null;
        }

        boolean isActive() {
            return returnDate == null;
        }
    }

    static Book findBook(String id) {
        for (Book b : books) {
            if (b.id.equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }

    static Student findStudent(String id) {
        for (Student s : students) {
            if (s.id.equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    static Loan findActiveLoan(String bookId, String studentId) {
        for (Loan l : loans) {
            if (l.isActive()
                    && l.bookId.equalsIgnoreCase(bookId)
                    && l.studentId.equalsIgnoreCase(studentId)) {
                return l;
            }
        }
        return null;
    }

    static boolean isBookIssued(String bookId) {
        for (Loan l : loans) {
            if (l.isActive() && l.bookId.equalsIgnoreCase(bookId)) {
                return true;
            }
        }
        return false;
    }

    static int activeBookCount(String studentId) {
        int count = 0;
        for (Loan l : loans) {
            if (l.isActive()
                    && l.studentId.equalsIgnoreCase(studentId)) {
                count++;
            }
        }
        return count;
    }

    static void addBook() {
        System.out.print("Enter book ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || findBook(id) != null) {
            System.out.println("Invalid or duplicate book ID.");
            return;
        }

        System.out.print("Enter book title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter author name: ");
        String author = sc.nextLine().trim();

        if (title.isEmpty() || author.isEmpty()) {
            System.out.println("Title and author cannot be empty.");
            return;
        }

        books.add(new Book(id, title, author));
        System.out.println("Book added successfully!");
    }

    static void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        System.out.println("\n--- BOOK LIST ---");
        for (Book b : books) {
            String status = isBookIssued(b.id)
                    ? "Issued" : "Available";

            System.out.println(
                    b.id + " | " + b.title + " | "
                    + b.author + " | " + status);
        }
    }

    static void registerStudent() {
        System.out.print("Enter student ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || findStudent(id) != null) {
            System.out.println("Invalid or duplicate student ID.");
            return;
        }

        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        students.add(new Student(id, name));
        System.out.println("Student registered successfully!");
    }

    static void issueBook() {
        System.out.print("Enter student ID: ");
        String studentId = sc.nextLine().trim();

        Student student = findStudent(studentId);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        if (activeBookCount(studentId) >= 3) {
            System.out.println("A student can borrow only 3 books.");
            return;
        }

        System.out.print("Enter book ID: ");
        String bookId = sc.nextLine().trim();

        Book book = findBook(bookId);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        if (isBookIssued(bookId)) {
            System.out.println("Book is already issued.");
            return;
        }

        System.out.print("Enter due date (YYYY-MM-DD): ");
        LocalDate dueDate;

        try {
            dueDate = LocalDate.parse(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid date. Example: 2026-11-01");
            return;
        }

        LocalDate issueDate = LocalDate.now();

        if (dueDate.isBefore(issueDate)) {
            System.out.println("Due date cannot be before issue date.");
            return;
        }

        loans.add(new Loan(studentId, bookId, issueDate, dueDate));

        System.out.println("Book issued successfully!");
        System.out.println("Issue date: " + issueDate);
        System.out.println("Due date: " + dueDate);
    }

    static void returnBook() {
        System.out.print("Enter student ID: ");
        String studentId = sc.nextLine().trim();

        System.out.print("Enter book ID: ");
        String bookId = sc.nextLine().trim();

        Loan loan = findActiveLoan(bookId, studentId);

        if (loan == null) {
            System.out.println("No active issue found for these IDs.");
            return;
        }

        LocalDate returnDate = LocalDate.now();
        long lateDays = Math.max(
                0, ChronoUnit.DAYS.between(loan.dueDate, returnDate));

        long fine = lateDays * FINE_PER_DAY;
        loan.returnDate = returnDate;

        System.out.println("\n--- RETURN RECEIPT ---");
        System.out.println("Student: " + findStudent(studentId).name);
        System.out.println("Book: " + findBook(bookId).title);
        System.out.println("Issue date: " + loan.issueDate);
        System.out.println("Due date: " + loan.dueDate);
        System.out.println("Return date: " + returnDate);
        System.out.println("Late days: " + lateDays);
        System.out.println("Fine: Rs. " + fine);
    }

    static void searchBook() {
        System.out.print("Enter book ID or title: ");
        String query = sc.nextLine().trim();

        boolean found = false;

        for (Book b : books) {
            if (b.id.equalsIgnoreCase(query)
                    || b.title.toLowerCase().contains(
                            query.toLowerCase())) {

                String status = isBookIssued(b.id)
                        ? "Issued" : "Available";

                System.out.println(
                        b.id + " | " + b.title + " | "
                        + b.author + " | " + status);

                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching book found.");
        }
    }

    static void viewLoans() {
        boolean found = false;

        System.out.println("\n--- ACTIVE ISSUES ---");

        for (Loan l : loans) {
            if (l.isActive()) {
                Student s = findStudent(l.studentId);
                Book b = findBook(l.bookId);

                long lateDays = Math.max(
                        0, ChronoUnit.DAYS.between(
                                l.dueDate, LocalDate.now()));

                System.out.println(
                        "Student: " + s.name
                        + " | Book: " + b.title
                        + " | Due: " + l.dueDate
                        + " | Current fine: Rs. "
                        + (lateDays * FINE_PER_DAY));

                found = true;
            }
        }

        if (!found) {
            System.out.println("No active book issues.");
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Register Student");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. Search Book");
            System.out.println("7. View Active Issues and Fines");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    viewBooks();
                    break;
                case "3":
                    registerStudent();
                    break;
                case "4":
                    issueBook();
                    break;
                case "5":
                    returnBook();
                    break;
                case "6":
                    searchBook();
                    break;
                case "7":
                    viewLoans();
                    break;
                case "8":
                    System.out.println("Thank you for using the system!");
                    sc.close();
                    return;
                default:
                    System.out.println("Please enter a number from 1 to 8.");
            }
        }
    }
}
