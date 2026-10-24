package com.library;

import java.util.Locale;
import java.util.Optional;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    // ==================== DATA ====================

    private final ObservableList<Book> books =
            FXCollections.observableArrayList();

    private final ObservableList<Student> students =
            FXCollections.observableArrayList();
    private final ObservableList<IssueRecord> issueRecords =
        FXCollections.observableArrayList();

    private BorderPane root;
    private VBox content;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("app-root");

        root.setLeft(createSidebar());
        showDashboard();

        Scene scene = new Scene(root, 1100, 700);

        var stylesheet =
                getClass().getResource("/css/dark-theme.css");

        if (stylesheet != null) {
            scene.getStylesheets()
                    .add(stylesheet.toExternalForm());
        }

        stage.setTitle("Library Management System");
        stage.setMinWidth(850);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    // ==================== SIDEBAR ====================

    private VBox createSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(28, 18, 24, 18));
        sidebar.setPrefWidth(220);

        Label logo = new Label("📚  LIBRARY");
        logo.getStyleClass().add("logo-label");

        Label subtitle = new Label("MANAGEMENT SYSTEM");
        subtitle.getStyleClass().add("sidebar-subtitle");

        VBox brand = new VBox(6, logo, subtitle);

        Label menuLabel = new Label("MAIN MENU");
        menuLabel.getStyleClass().add("section-label");

        Button dashboardButton = navButton("▦   Dashboard");
        Button booksButton = navButton("▤   Books");
        Button studentsButton = navButton("♙   Students");
        Button issueButton = navButton("↗   Issue Book");
        Button returnButton = navButton("↙   Return Book");
        Button searchButton = navButton("⌕   Search");
        Button reportsButton = navButton("▥   Reports");

        dashboardButton.setOnAction(e -> showDashboard());
        booksButton.setOnAction(e -> showBooks());

        // Students now opens the working Student Management page.
        studentsButton.setOnAction(e -> showStudents());

        issueButton.setOnAction(e -> showIssueBook());

        returnButton.setOnAction(e -> showReturnBook());

        searchButton.setOnAction(e -> showBooks());

        reportsButton.setOnAction(e ->
                showPlaceholder(
                        "Reports",
                        "Library reports will be added next."
                ));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label footer =
                new Label("Library System  •  Version 1.0");
        footer.getStyleClass().add("hint-label");

        sidebar.getChildren().addAll(
                brand,
                menuLabel,
                dashboardButton,
                booksButton,
                studentsButton,
                issueButton,
                returnButton,
                searchButton,
                reportsButton,
                spacer,
                footer
        );

        return sidebar;
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12, 10, 12, 12));
        return button;
    }

    // ==================== PAGE HELPERS ====================

    private void prepareContent() {
        content = new VBox(20);
        content.setPadding(new Insets(30));
        content.getStyleClass().add("page-content");
        root.setCenter(content);
    }

    private Label pageTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-title");
        return label;
    }

    private Label pageSubtitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-subtitle");
        return label;
    }

    // ==================== DASHBOARD ====================

      
    private void showDashboard() {
        prepareContent();

        content.getChildren().addAll(
                pageTitle("Dashboard"),
                pageSubtitle(
                        "Welcome to your Library Management System."
                )
        );

        // Calculate current library statistics.
        int totalBookTitles = books.size();
        int totalCopies = getTotalCopies();
        int availableCopies = getAvailableCopies();
        int totalStudents = students.size();
        long activeLoans = getActiveLoans();
        long overdueBooks = getOverdueBooks();

        // First row of statistics.
        HBox firstRow = new HBox(15);
        firstRow.getChildren().addAll(
                createStatCard(
                        "TOTAL BOOK TITLES",
                        String.valueOf(totalBookTitles)
                ),
                createStatCard(
                        "TOTAL COPIES",
                        String.valueOf(totalCopies)
                ),
                createStatCard(
                        "AVAILABLE COPIES",
                        String.valueOf(availableCopies)
                )
        );

        // Second row of statistics.
        HBox secondRow = new HBox(15);
        secondRow.getChildren().addAll(
                createStatCard(
                        "REGISTERED STUDENTS",
                        String.valueOf(totalStudents)
                ),
                createStatCard(
                        "BOOKS CURRENTLY ISSUED",
                        String.valueOf(activeLoans)
                ),
                createStatCard(
                        "OVERDUE BOOKS",
                        String.valueOf(overdueBooks)
                )
        );

        content.getChildren().addAll(firstRow, secondRow);

        Label sectionTitle = new Label("Quick Actions");
        sectionTitle.getStyleClass().add("section-title");

        Button manageBooks = new Button("Manage Books");
        manageBooks.getStyleClass().add("primary-button");
        manageBooks.setOnAction(e -> showBooks());

        Button addBook = new Button("＋ Add a Book");
        addBook.getStyleClass().add("secondary-button");
        addBook.setOnAction(e -> addBookDialog());

        Button manageStudents = new Button("Manage Students");
        manageStudents.getStyleClass().add("secondary-button");
        manageStudents.setOnAction(e -> showStudents());

        HBox actions = new HBox(
                12, manageBooks, addBook, manageStudents
        );

        Label info = new Label(
                books.isEmpty()
                        ? "Start by adding the books available in your library."
                        : "Your library inventory is ready to manage."
        );
        info.getStyleClass().add("hint-label");

        content.getChildren().addAll(
                sectionTitle, actions, info
        );
    }


        
                
    private VBox createStatCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        VBox card = new VBox(12, titleLabel, valueLabel);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));
        card.setMinWidth(145);
        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }

    private int getTotalCopies() {
        return books.stream()
                .mapToInt(Book::getQuantity)
                .sum();
    }

    private int getAvailableCopies() {
        return books.stream()
                .mapToInt(Book::getAvailableCopies)
                .sum();
    }
    
    private long getActiveLoans() {
        return issueRecords.stream()
                .filter(record -> !record.isReturned())
                .count();
    }

    private long getOverdueBooks() {
        return issueRecords.stream()
                .filter(record -> !record.isReturned())
                .filter(record ->
                        record.getDueDate().isBefore(LocalDate.now()))
                .count();
    }

    // ==================== BOOKS PAGE ====================

    private void showBooks() {
        prepareContent();

        Label title = pageTitle("Books Inventory");
        Label subtitle = pageSubtitle(
                "Add, view, search and remove books in your library."
        );

        TextField searchField = new TextField();
        searchField.setPromptText(
                "Search by ID, title, author or category..."
        );
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button addButton = new Button("＋ Add Book");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> addBookDialog());

        HBox toolbar = new HBox(12, searchField, addButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<Book> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setPlaceholder(
                new Label("No books found. Click Add Book to begin.")
        );

        TableColumn<Book, String> idColumn =
                new TableColumn<>("Book ID");
        idColumn.setCellValueFactory(data ->
                data.getValue().idProperty());

        TableColumn<Book, String> titleColumn =
                new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data ->
                data.getValue().titleProperty());

        TableColumn<Book, String> authorColumn =
                new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data ->
                data.getValue().authorProperty());

        TableColumn<Book, String> categoryColumn =
                new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data ->
                data.getValue().categoryProperty());

        TableColumn<Book, String> quantityColumn =
                new TableColumn<>("Total Copies");
        quantityColumn.setCellValueFactory(data ->
                data.getValue().quantityProperty());

        TableColumn<Book, String> availableColumn =
                new TableColumn<>("Available");
        availableColumn.setCellValueFactory(data ->
                data.getValue().availableProperty());

        table.getColumns().addAll(
                idColumn,
                titleColumn,
                authorColumn,
                categoryColumn,
                quantityColumn,
                availableColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        FilteredList<Book> filteredBooks =
                new FilteredList<>(books, book -> true);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    String query = newValue == null
                            ? ""
                            : newValue.trim()
                                    .toLowerCase(Locale.ROOT);

                    filteredBooks.setPredicate(book ->
                            query.isEmpty()
                            || book.getId()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                            || book.getTitle()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                            || book.getAuthor()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                            || book.getCategory()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                    );
                }
        );

        table.setItems(filteredBooks);

        Button deleteButton =
                new Button("Delete Selected Book");
        deleteButton.getStyleClass().add("secondary-button");
        deleteButton.setOnAction(
                e -> deleteSelectedBook(table)
        );

        Label countLabel = new Label();
        countLabel.getStyleClass().add("hint-label");

        Runnable updateCount = () -> countLabel.setText(
                "Showing " + filteredBooks.size()
                + " of " + books.size() + " book titles"
        );

        filteredBooks.addListener(
                (ListChangeListener<Book>) change ->
                        updateCount.run()
        );

        books.addListener(
                (ListChangeListener<Book>) change ->
                        updateCount.run()
        );

        updateCount.run();

        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(
                title,
                subtitle,
                toolbar,
                table,
                deleteButton,
                countLabel
        );
    }

    // ==================== ADD BOOK FORM ====================

    private void addBookDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Add a Book");
        dialog.setHeaderText("Enter the book details");

        ButtonType saveButton = new ButtonType(
                "Save Book",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton, ButtonType.CANCEL
        );

        TextField idField = new TextField();
        idField.setPromptText("Example: B001");
        idField.setText(generateBookId());

        TextField titleField = new TextField();
        titleField.setPromptText("Enter book title");

        TextField authorField = new TextField();
        authorField.setPromptText("Enter author name");

        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll(
                "Fiction",
                "Science",
                "Technology",
                "Education",
                "History",
                "Biography",
                "Reference",
                "Other"
        );
        categoryField.setEditable(true);
        categoryField.setPromptText("Select or type a category");
        categoryField.setMaxWidth(Double.MAX_VALUE);

        TextField quantityField = new TextField("1");
        quantityField.setPromptText("Number of copies");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(14);
        form.setPadding(new Insets(15));

        form.add(new Label("Book ID:"), 0, 0);
        form.add(idField, 1, 0);

        form.add(new Label("Title:"), 0, 1);
        form.add(titleField, 1, 1);

        form.add(new Label("Author:"), 0, 2);
        form.add(authorField, 1, 2);

        form.add(new Label("Category:"), 0, 3);
        form.add(categoryField, 1, 3);

        form.add(new Label("Total copies:"), 0, 4);
        form.add(quantityField, 1, 4);

        GridPane.setHgrow(idField, Priority.ALWAYS);
        GridPane.setHgrow(titleField, Priority.ALWAYS);
        GridPane.setHgrow(authorField, Priority.ALWAYS);
        GridPane.setHgrow(quantityField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(480);

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    String id = idField.getText().trim();
                    String bookTitle = titleField.getText().trim();
                    String author = authorField.getText().trim();
                    String category = categoryField.getEditor()
                            .getText().trim();
                    String quantityText =
                            quantityField.getText().trim();

                    if (id.isEmpty() || bookTitle.isEmpty()
                            || author.isEmpty()
                            || category.isEmpty()) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Missing Information",
                                "Please fill in all the book details."
                        );
                        event.consume();
                        return;
                    }

                    boolean duplicateId = books.stream()
                            .anyMatch(book ->
                                    book.getId()
                                            .equalsIgnoreCase(id));

                    if (duplicateId) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Duplicate Book ID",
                                "This ID already exists. Please use a "
                                + "different ID."
                        );
                        event.consume();
                        return;
                    }

                    try {
                        int quantity =
                                Integer.parseInt(quantityText);

                        if (quantity < 1) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Invalid Quantity",
                                "Total copies must be a whole number "
                                + "greater than zero."
                        );
                        event.consume();
                    }
                }
        );

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()
                && result.get() == saveButton) {
            String id = idField.getText().trim();
            String bookTitle = titleField.getText().trim();
            String author = authorField.getText().trim();
            String category =
                    categoryField.getEditor().getText().trim();

            int quantity =
                    Integer.parseInt(quantityField.getText().trim());

            books.add(new Book(
                    id, bookTitle, author, category, quantity
            ));

            showBooks();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Book Added",
                    "\"" + bookTitle + "\" was added successfully."
            );
        }
    }

    private String generateBookId() {
        int number = 1;

        while (true) {
            String candidate = String.format("B%03d", number);

            boolean exists = books.stream()
                    .anyMatch(book ->
                            book.getId()
                                    .equalsIgnoreCase(candidate));

            if (!exists) {
                return candidate;
            }

            number++;
        }
    }

    // ==================== DELETE BOOK ====================

    private void deleteSelectedBook(TableView<Book> table) {
        Book selectedBook =
                table.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Book Selected",
                    "Select a book from the table before deleting."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete \"" + selectedBook.getTitle() + "\"?",
                ButtonType.CANCEL,
                ButtonType.OK
        );

        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete this book?");

        Optional<ButtonType> answer =
                confirmation.showAndWait();

        if (answer.isPresent()
                && answer.get() == ButtonType.OK) {
            books.remove(selectedBook);
            showBooks();
        }
    }

    // ==================== STUDENTS PAGE ====================

    private void showStudents() {
        prepareContent();

        Label title = pageTitle("Student Management");
        Label subtitle = pageSubtitle(
                "Register students and manage their contact details."
        );

        TextField searchField = new TextField();
        searchField.setPromptText(
                "Search by student ID, name, email or phone..."
        );
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button addButton = new Button("＋ Add Student");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> addStudentDialog());

        HBox toolbar = new HBox(12, searchField, addButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<Student> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setPlaceholder(
                new Label("No students found. Click Add Student to begin.")
        );

        TableColumn<Student, String> idColumn =
                new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getStudentId()
                ));

        TableColumn<Student, String> nameColumn =
                new TableColumn<>("Full Name");
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getName()
                ));

        TableColumn<Student, String> emailColumn =
                new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEmail()
                ));

        TableColumn<Student, String> phoneColumn =
                new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPhone()
                ));

        table.getColumns().addAll(
                idColumn, nameColumn, emailColumn, phoneColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        FilteredList<Student> filteredStudents =
                new FilteredList<>(students, student -> true);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    String query = newValue == null
                            ? ""
                            : newValue.trim()
                                    .toLowerCase(Locale.ROOT);

                    filteredStudents.setPredicate(student ->
                            query.isEmpty()
                            || student.getStudentId()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                            || student.getName()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                            || student.getEmail()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                            || student.getPhone()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(query)
                    );
                }
        );

        table.setItems(filteredStudents);

        Button deleteButton =
                new Button("Delete Selected Student");
        deleteButton.getStyleClass().add("secondary-button");
        deleteButton.setOnAction(
                e -> deleteSelectedStudent(table)
        );

        Label countLabel = new Label();
        countLabel.getStyleClass().add("hint-label");

        Runnable updateCount = () -> countLabel.setText(
                "Showing " + filteredStudents.size()
                + " of " + students.size() + " students"
        );

        filteredStudents.addListener(
                (ListChangeListener<Student>) change ->
                        updateCount.run()
        );

        students.addListener(
                (ListChangeListener<Student>) change ->
                        updateCount.run()
        );

        updateCount.run();

        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(
                title,
                subtitle,
                toolbar,
                table,
                deleteButton,
                countLabel
        );
    }

    // ==================== ADD STUDENT FORM ====================

    private void addStudentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter the student's details");

        ButtonType saveButton = new ButtonType(
                "Save Student",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton, ButtonType.CANCEL
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(14);
        form.setPadding(new Insets(15));

        form.add(new Label("Full Name:"), 0, 0);
        form.add(nameField, 1, 0);

        form.add(new Label("Email:"), 0, 1);
        form.add(emailField, 1, 1);

        form.add(new Label("Phone:"), 0, 2);
        form.add(phoneField, 1, 2);

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(phoneField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(450);

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();

                    if (name.isEmpty()
                            || email.isEmpty()
                            || phone.isEmpty()) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Missing Information",
                                "Please fill in the name, email and phone."
                        );
                        event.consume();
                        return;
                    }

                    if (!email.matches(
                            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Invalid Email",
                                "Please enter a valid email address."
                        );
                        event.consume();
                        return;
                    }

                    boolean duplicateEmail = students.stream()
                            .anyMatch(student ->
                                    student.getEmail()
                                            .equalsIgnoreCase(email));

                    if (duplicateEmail) {
                        showAlert(
                                Alert.AlertType.WARNING,
                                "Email Already Registered",
                                "A student with this email already exists."
                        );
                        event.consume();
                    }
                }
        );

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()
                && result.get() == saveButton) {
            String studentId = generateStudentId();

            Student student = new Student(
                    studentId,
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );

            students.add(student);

            showStudents();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Student Added",
                    student.getName()
                    + " was registered successfully.\nStudent ID: "
                    + student.getStudentId()
            );
        }
    }

    private String generateStudentId() {
        int number = 1;

        while (true) {
            String candidate = String.format("S%03d", number);

            boolean exists = students.stream()
                    .anyMatch(student ->
                            student.getStudentId()
                                    .equalsIgnoreCase(candidate));

            if (!exists) {
                return candidate;
            }

            number++;
        }
    }

    // ==================== DELETE STUDENT ====================

    private void deleteSelectedStudent(
            TableView<Student> table) {

        Student selectedStudent =
                table.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Student Selected",
                    "Select a student from the table before deleting."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete student \"" + selectedStudent.getName() + "\"?",
                ButtonType.CANCEL,
                ButtonType.OK
        );

        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete this student?");

        Optional<ButtonType> answer =
                confirmation.showAndWait();

        if (answer.isPresent()
                && answer.get() == ButtonType.OK) {
            students.remove(selectedStudent);
            showStudents();
        }
    }
    
    // ==================== RETURN BOOK PAGE ====================

    private void showReturnBook() {
        prepareContent();

        Label title = pageTitle("Return a Book");
        Label subtitle = pageSubtitle(
                "View currently issued books and process returns."
        );

        TableView<IssueRecord> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setPlaceholder(
                new Label("No books are currently issued.")
        );

        TableColumn<IssueRecord, String> idColumn =
                new TableColumn<>("Issue ID");
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getIssueId()
                ));

        TableColumn<IssueRecord, String> studentColumn =
                new TableColumn<>("Student");
        studentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getStudentName()
                ));

        TableColumn<IssueRecord, String> bookColumn =
                new TableColumn<>("Book");
        bookColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getBookTitle()
                ));

        TableColumn<IssueRecord, String> issueDateColumn =
                new TableColumn<>("Issue Date");
        issueDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getIssueDate().toString()
                ));

        TableColumn<IssueRecord, String> dueDateColumn =
                new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDueDate().toString()
                ));

        TableColumn<IssueRecord, String> statusColumn =
                new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getStatus()
                ));

        TableColumn<IssueRecord, String> fineColumn =
                new TableColumn<>("Current Fine");
        fineColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format(
                                Locale.ROOT,
                                "₹%.2f",
                                data.getValue().getFine()
                        )
                ));

        table.getColumns().addAll(
                idColumn,
                studentColumn,
                bookColumn,
                issueDateColumn,
                dueDateColumn,
                statusColumn,
                fineColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        ObservableList<IssueRecord> activeIssues =
                FXCollections.observableArrayList();

        for (IssueRecord record : issueRecords) {
            if (!record.isReturned()) {
                activeIssues.add(record);
            }
        }

        table.setItems(activeIssues);

        Button returnButton =
                new Button("Return Selected Book");
        returnButton.getStyleClass().add("primary-button");

        returnButton.setOnAction(e -> {
            IssueRecord selectedRecord =
                    table.getSelectionModel().getSelectedItem();

            if (selectedRecord == null) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "No Issue Selected",
                        "Select an issued book from the table first."
                );
                return;
            }

            Alert confirmation = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Record the return of \""
                    + selectedRecord.getBookTitle() + "\"?",
                    ButtonType.CANCEL,
                    ButtonType.OK
            );

            confirmation.setTitle("Confirm Book Return");
            confirmation.setHeaderText("Return this book?");

            Optional<ButtonType> answer =
                    confirmation.showAndWait();

            if (answer.isEmpty()
                    || answer.get() != ButtonType.OK) {
                return;
            }

            LocalDate returnDate = LocalDate.now();

            selectedRecord.returnBook(returnDate);

            Book matchingBook = books.stream()
                    .filter(book ->
                            book.getId().equals(
                                    selectedRecord.getBookId()))
                    .findFirst()
                    .orElse(null);

            if (matchingBook != null) {
                matchingBook.returnCopy();
            }

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Book Returned",
                    "Book: " + selectedRecord.getBookTitle()
                    + "\nReturn date: " + returnDate
                    + "\nOverdue days: "
                    + selectedRecord.getOverdueDays()
                    + "\nFine: "
                    + String.format(
                            Locale.ROOT,
                            "₹%.2f",
                            selectedRecord.getFine()
                    )
            );

            showReturnBook();
        });

        Button backButton =
                new Button("Back to Dashboard");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> showDashboard());

        HBox buttons = new HBox(12, returnButton, backButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label hint = new Label(
                "Fine: ₹2 per overdue day. "
                + "The return date is set to today's date."
        );
        hint.getStyleClass().add("hint-label");

        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(
                title,
                subtitle,
                table,
                hint,
                buttons
        );
    }


    // ==================== PLACEHOLDER PAGES ====================
    
    // ==================== ISSUE BOOK PAGE ====================

    private void showIssueBook() {
        prepareContent();

        Label title = pageTitle("Issue a Book");
        Label subtitle = pageSubtitle(
                "Select a registered student and an available book."
        );

        ComboBox<Student> studentBox = new ComboBox<>();
        studentBox.setPromptText("Select a student");
        studentBox.setItems(students);
        studentBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Book> bookBox = new ComboBox<>();
        bookBox.setPromptText("Select an available book");
        bookBox.setItems(FXCollections.observableArrayList(
                books.stream()
                        .filter(book -> book.getAvailableCopies() > 0)
                        .toList()
        ));
        bookBox.setMaxWidth(Double.MAX_VALUE);

        DatePicker issueDatePicker =
                new DatePicker(LocalDate.now());

        DatePicker dueDatePicker =
                new DatePicker(LocalDate.now().plusDays(14));

        issueDatePicker.setMaxWidth(Double.MAX_VALUE);
        dueDatePicker.setMaxWidth(Double.MAX_VALUE);

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(16);
        form.setPadding(new Insets(20));

        form.add(new Label("Student:"), 0, 0);
        form.add(studentBox, 1, 0);

        form.add(new Label("Book:"), 0, 1);
        form.add(bookBox, 1, 1);

        form.add(new Label("Issue Date:"), 0, 2);
        form.add(issueDatePicker, 1, 2);

        form.add(new Label("Due Date:"), 0, 3);
        form.add(dueDatePicker, 1, 3);

        GridPane.setHgrow(studentBox, Priority.ALWAYS);
        GridPane.setHgrow(bookBox, Priority.ALWAYS);
        GridPane.setHgrow(issueDatePicker, Priority.ALWAYS);
        GridPane.setHgrow(dueDatePicker, Priority.ALWAYS);

        Label hint = new Label(
                "Students may hold up to 3 books at a time. "
                + "The default loan period is 14 days."
        );
        hint.getStyleClass().add("hint-label");

        Button issueButton = new Button("Issue Book");
        issueButton.getStyleClass().add("primary-button");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setOnAction(e -> showDashboard());

        issueButton.setOnAction(e -> {
            Student selectedStudent =
                    studentBox.getValue();

            Book selectedBook =
                    bookBox.getValue();

            LocalDate issueDate =
                    issueDatePicker.getValue();

            LocalDate dueDate =
                    dueDatePicker.getValue();

            if (selectedStudent == null
                    || selectedBook == null
                    || issueDate == null
                    || dueDate == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Missing Information",
                        "Select a student, a book, and both dates."
                );
                return;
            }

            if (issueDate.isBefore(LocalDate.now())) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Issue Date",
                        "The issue date cannot be in the past."
                );
                return;
            }

            if (dueDate.isBefore(issueDate)
                    || dueDate.isEqual(issueDate)) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Due Date",
                        "The due date must be after the issue date."
                );
                return;
            }

            long activeLoans = issueRecords.stream()
                    .filter(record ->
                            record.getStudentId().equals(
                                    selectedStudent.getStudentId()))
                    .filter(record -> !record.isReturned())
                    .count();

            if (activeLoans >= 3) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Book Limit Reached",
                        "This student already has 3 books issued."
                );
                return;
            }

            if (selectedBook.getAvailableCopies() <= 0) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Book Unavailable",
                        "There are no available copies of this book."
                );
                return;
            }

            String issueId = generateIssueId();

            IssueRecord record = new IssueRecord(
                    issueId,
                    selectedStudent.getStudentId(),
                    selectedStudent.getName(),
                    selectedBook.getId(),
                    selectedBook.getTitle(),
                    issueDate,
                    dueDate
            );

            if (!selectedBook.issueCopy()) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Book Unavailable",
                        "This book is no longer available."
                );
                return;
            }

            issueRecords.add(record);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Book Issued Successfully",
                    "Issue ID: " + issueId
                    + "\nStudent: " + selectedStudent.getName()
                    + "\nBook: " + selectedBook.getTitle()
                    + "\nIssue date: " + issueDate
                    + "\nDue date: " + dueDate
            );

            showIssueBook();
        });

        HBox buttons = new HBox(12, issueButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox formCard = new VBox(18, form, hint, buttons);
        formCard.getStyleClass().add("stat-card");
        formCard.setPadding(new Insets(20));
        formCard.setMaxWidth(650);

        content.getChildren().addAll(
                title, subtitle, formCard
        );
    }

    private String generateIssueId() {
        int number = 1;

        while (true) {
            String candidate = String.format("I%03d", number);

            boolean exists = issueRecords.stream()
                    .anyMatch(record ->
                            record.getIssueId().equals(candidate));

            if (!exists) {
                return candidate;
            }

            number++;
        }
    }


    private void showPlaceholder(String title, String message) {
        prepareContent();

        Label heading = pageTitle(title);
        Label description = pageSubtitle(message);

        Button back = new Button("← Back to Dashboard");
        back.getStyleClass().add("primary-button");
        back.setOnAction(e -> showDashboard());

        content.getChildren().addAll(
                heading, description, back
        );
    }

    // ==================== ALERTS ====================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== BOOK MODEL ====================

    public static class Book {

        private final SimpleStringProperty id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleStringProperty category;
        private final SimpleStringProperty quantity;
        private final SimpleStringProperty available;

        public Book(
                String id,
                String title,
                String author,
                String category,
                int quantity) {

            this.id = new SimpleStringProperty(id);
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
            this.category = new SimpleStringProperty(category);
            this.quantity = new SimpleStringProperty(
                    String.valueOf(quantity)
            );
            this.available = new SimpleStringProperty(
                    String.valueOf(quantity)
            );
        }

        public String getId() {
            return id.get();
        }

        public String getTitle() {
            return title.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getCategory() {
            return category.get();
        }

        public int getQuantity() {
            return Integer.parseInt(quantity.get());
        }
        @Override
public String toString() {
    return getId() + " - " + getTitle()
            + " (Available: " + getAvailableCopies() + ")";
}

        public int getAvailableCopies() {
            return Integer.parseInt(available.get());
        }
        public boolean issueCopy() {
    int currentAvailable = getAvailableCopies();

    if (currentAvailable <= 0) {
        return false;
    }

    available.set(String.valueOf(currentAvailable - 1));
    return true;
}

public void returnCopy() {
    int currentAvailable = getAvailableCopies();

    if (currentAvailable < getQuantity()) {
        available.set(String.valueOf(currentAvailable + 1));
    }
}

        public SimpleStringProperty idProperty() {
            return id;
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public SimpleStringProperty authorProperty() {
            return author;
        }

        public SimpleStringProperty categoryProperty() {
            return category;
        }

        public SimpleStringProperty quantityProperty() {
            return quantity;
        }

        public SimpleStringProperty availableProperty() {
            return available;
        }
    }

    // ==================== APPLICATION ENTRY POINT ====================

    public static void main(String[] args) {
        launch(args);
    }
}