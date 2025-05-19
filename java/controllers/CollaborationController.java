package tn.esprit.controllers;

import com.google.zxing.BarcodeFormat;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.time.format.DateTimeFormatter;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import tn.esprit.entities.Collaboration;
import tn.esprit.entities.CollaborationParticipant;
import tn.esprit.services.CollaborationParticipantService;
import tn.esprit.services.CollaborationService;
import tn.esprit.entities.Projet;
import tn.esprit.services.ProjetService;
import tn.esprit.services.GeminiService;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

import javafx.stage.FileChooser;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class CollaborationController {

    @FXML private ImageView imagePreview;
    @FXML private Label imagePathLabel;
    private File selectedImageFile;
    private File selectedProjectImageFile;
  //  @FXML private ImageView logoView;
  //  @FXML private ImageView detailsImage;
    @FXML private StackPane mainContent;
    @FXML private VBox dashboardView, qrCodeContainer;
    @FXML private VBox formContainer;
    //@FXML private FlowPane cardContainer;
    @FXML private HBox cardSlider;
    @FXML private TextField  descField;
    @FXML private TextField  typeField, statusField;
    @FXML private TextField nameField,  budgetField, locationField, prerequisField;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Button showFormButton, requestToJoinButton, handleAddProject;
    @FXML private Button dashboardButton;
    @FXML private TextField searchField;
 //   @FXML private Button sortButton;
    @FXML private VBox modifyFormContainer;
    @FXML private TextField modNameField, modDescField, modTypeField, modStatusField, modBudgetField, modLocationField, modPrerequisField;
    @FXML private DatePicker modStartDatePicker, modEndDatePicker;
    @FXML private VBox detailsContainer;
    @FXML private HBox projectListContainer;
    @FXML private Button modifyButton, deleteButton;
    @FXML private Label detailName;
    @FXML private Text detailDesc;
    @FXML private Label detailType;
    @FXML private Label detailStatus;
    @FXML private Label detailBudget;
    @FXML private Label detailLocation;
    @FXML private Label detailPrerequis;
    @FXML private Label detailStartDate;
    @FXML private Label detailEndDate;
    @FXML private VBox projectDetailsContainer;
    @FXML private ImageView projectImage;
    @FXML private ImageView qrCodeImageView, qrCodeRequest;
    @FXML private Text projectDesc;
    @FXML private Label projectTitle,  projectStatus, projectBudget, projectDates;
    @FXML private VBox projectFormContainer;
    @FXML private TextField projectNameField;
    @FXML private TextArea projectDescriptionField;
    @FXML private TextField projectStatusField, projectTypeField;
    @FXML private TextField projectBudgetField;
    @FXML private DatePicker projectStartDatePicker;
    @FXML private DatePicker projectEndDatePicker;
    private Collaboration selectedCollaborationForProject;
    @FXML private VBox projectModifyForm;
    @FXML private TextField modProjectTitle, modProjectDesc, modProjectStatus, modProjectBudget;
    @FXML private DatePicker modProjectStartDate, modProjectEndDate;
    @FXML private Label modProjectImagePath;
    @FXML private ImageView modProjectImagePreview;
    private Projet selectedProject;
    @FXML private Label newProjectImageLabel;
    @FXML private ImageView newProjectImagePreview;
    private File selectedNewProjectImageFile;
    @FXML private HBox topNavbar;
    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label descErrorLabel;

    @FXML
    private Label typeErrorLabel;

    @FXML
    private Label statusErrorLabel;

    @FXML
    private Label budgetErrorLabel;

    @FXML
    private Label locationErrorLabel;

    @FXML
    private Label startDateErrorLabel;

    @FXML
    private Label endDateErrorLabel;
    @FXML
    private Label projectNameErrorLabel;
    @FXML
    private Label projectDescriptionErrorLabel;
    @FXML
    private Label projectStatusErrorLabel;
    @FXML
    private Label projectBudgetErrorLabel;
    @FXML
    private Label projectStartDateErrorLabel;
    @FXML
    private Label projectEndDateErrorLabel;
    @FXML
    private Label newProjectImageErrorLabel;
    @FXML private Label modNameErrorLabel;
    @FXML private Label modDescErrorLabel;
    @FXML private Label modTypeErrorLabel;
    @FXML private Label modStatusErrorLabel;
    @FXML private Label modBudgetErrorLabel;
    @FXML private Label modLocationErrorLabel;
    @FXML private Label modPrerequisErrorLabel;
    @FXML private Label modStartDateErrorLabel;
    @FXML private Label modEndDateErrorLabel;
    @FXML private Label modImageErrorLabel;
    @FXML
    private Label modProjectTitleError;

    @FXML
    private Label modProjectDescError;

    @FXML
    private Label modProjectBudgetError;

    @FXML
    private Label modProjectDatesError;

    @FXML
    private Label modProjectImageError;
    @FXML
    private VBox requestsContainer;
    @FXML
    private ScrollPane requestsScrollPane;
    @FXML private AnchorPane chatWindow;
    @FXML private VBox chatBody;
    @FXML private TextField userInput;
    @FXML private ScrollPane chatScrollPane;
    private int collaborationOwnerId;
    private int loggedInUserId = 4;


    private Collaboration selectedCollaboration;
    private ObservableList<Collaboration> data;

    @FXML
    public void initialize() {
        nameErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        descErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        typeErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        budgetErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        locationErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        startDateErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        endDateErrorLabel.setMinHeight(Region.USE_PREF_SIZE);
        loadRequestsForCurrentUser();

        CollaborationService service = new CollaborationService();

        try {
            data = FXCollections.observableArrayList(service.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set logo if logoView is defined
   /*     if (logoView != null) {
            Image logo = new Image(getClass().getResource("/images/logo.png").toExternalForm());
            logoView.setImage(logo);
        }*/

        // Initial visibility setup
        setSectionVisibility(true, false, false, false);

        // Display initial cards
        displayCards(data);

        // --- FILTER LOGIC ---
        FilteredList<Collaboration> filtered = new FilteredList<>(data, p -> true);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            String lower = newVal.toLowerCase();
            filtered.setPredicate(collab ->
                    collab.getName().toLowerCase().contains(lower) ||
                            collab.getType().toLowerCase().contains(lower) ||
                            collab.getStatus().toLowerCase().contains(lower)
            );
            displayCards(filtered);
        });

        // --- SORT BUTTON ---
  /*      sortButton.setOnAction(e -> {
            FXCollections.sort(filtered, Comparator.comparingInt(Collaboration::getBudget));
            displayCards(filtered);
        });*/

        // --- SHOW FORM BUTTON ---
        showFormButton.setOnAction(e -> {
            setSectionVisibility(false, true, false, false);
        });

        // --- DASHBOARD REDIRECTION ---
        dashboardButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                Parent dashboardView = loader.load();
                mainContent.getChildren().setAll(dashboardView);
                if (topNavbar != null) {
                    topNavbar.setVisible(false);
                    topNavbar.setManaged(false);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // --- Card Slider for front view ---
        initSliderCards();
    }

    private void setSectionVisibility(boolean showDashboard, boolean showForm, boolean showModify, boolean showDetails) {
        dashboardView.setVisible(showDashboard);
        formContainer.setVisible(showForm);
        modifyFormContainer.setVisible(showModify);
        detailsContainer.setVisible(showDetails);
    }


    private void initSliderCards() {
        try {
            List<Collaboration> collaborations = new CollaborationService().recuperer();
            cardSlider.getChildren().clear();  // Clear old cards

            for (Collaboration collab : collaborations) {
                VBox card = new VBox(10);
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0.2, 0, 2);");
                card.setPrefWidth(160);

                Label name = new Label(collab.getName());
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
                name.setWrapText(true);

                Label type = new Label("Type: " + collab.getType());
                type.setStyle("-fx-text-fill: #64748b;");

                Label status = new Label("Status: " + collab.getStatus());
                status.setStyle("-fx-text-fill: #64748b;");

                // 👇 Make the whole card clickable
                card.setCursor(Cursor.HAND);
                card.setOnMouseClicked(e -> {
                    selectedCollaboration = collab;
                    showCollaborationDetails(collab);
                });

                card.getChildren().addAll( name, type, status);
                cardSlider.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private void populateModifyForm(Collaboration c) {
        modNameField.setText(c.getName());
        modDescField.setText(c.getDescription());
        modTypeField.setText(c.getType());
        modBudgetField.setText(String.valueOf(c.getBudget()));
        modLocationField.setText(c.getLocation());
        modPrerequisField.setText(c.getPrerequis());
        modStartDatePicker.setValue(c.getStartDate());
        modEndDatePicker.setValue(c.getEndDate());
    }

    @FXML
    private void handleAddCollaboration() {
        System.out.println("👉 [DEBUG] Enregistrer button clicked.");

        if (!validateForm()) {
            System.out.println("❌ [DEBUG] Form validation failed.");
            debugValidationErrors();
            return;
        }

        System.out.println("✅ [DEBUG] Form validation passed.");

        try {
            String imageName = (selectedImageFile != null) ? selectedImageFile.getName() : "default.png";
            System.out.println("[DEBUG] Selected image: " + imageName);

            // ✅ Dynamically determine the status based on dates
            String dynamicStatus;
            LocalDate today = LocalDate.now();
            if (startDatePicker.getValue().isAfter(today)) {
                dynamicStatus = "En attente";
            } else if (!endDatePicker.getValue().isBefore(today)) {
                dynamicStatus = "En cours";
            } else {
                dynamicStatus = "Terminé";
            }
            System.out.println("[DEBUG] Calculated status: " + dynamicStatus);

            Collaboration c = new Collaboration(
                    nameField.getText(),
                    descField.getText(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue(),
                    typeField.getText(),
                    dynamicStatus,
                    Integer.parseInt(budgetField.getText()),
                    locationField.getText(),
                    prerequisField.getText(),
                    imageName,
                    loggedInUserId
            );

            CollaborationService service = new CollaborationService();

            // ✅ Save image to XAMPP directory
            if (selectedImageFile != null) {
                Path destinationDir = Paths.get("C:/xampp/htdocs/uploads/");
                Files.createDirectories(destinationDir);
                Path destination = destinationDir.resolve(imageName);
                Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[DEBUG] Image copied to: " + destination.toString());
            }

            service.ajouter(c);
            data.add(c);
            displayCards(data);

            System.out.println("✅ [DEBUG] Collaboration added successfully.");
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La collaboration a été ajoutée avec succès !");
            formContainer.setVisible(false);
            dashboardView.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez vérifier les champs et réessayer.");
        }
    }


    private void debugValidationErrors() {
        System.out.println("🛠 [DEBUG] Field States:");

        System.out.println("- nameField: '" + nameField.getText() + "'");
        System.out.println("- descField: '" + descField.getText() + "'");
        System.out.println("- typeField: '" + typeField.getText() + "'");
        System.out.println("- budgetField: '" + budgetField.getText() + "'");
        System.out.println("- locationField: '" + locationField.getText() + "'");
        System.out.println("- startDatePicker: " + startDatePicker.getValue());
        System.out.println("- endDatePicker: " + endDatePicker.getValue());

        System.out.println("🛠 [DEBUG] Error Label Texts:");
        System.out.println("- nameErrorLabel: '" + nameErrorLabel.getText() + "' (visible=" + nameErrorLabel.isVisible() + ")");
        System.out.println("- descErrorLabel: '" + descErrorLabel.getText() + "' (visible=" + descErrorLabel.isVisible() + ")");
        System.out.println("- typeErrorLabel: '" + typeErrorLabel.getText() + "' (visible=" + typeErrorLabel.isVisible() + ")");
        System.out.println("- budgetErrorLabel: '" + budgetErrorLabel.getText() + "' (visible=" + budgetErrorLabel.isVisible() + ")");
        System.out.println("- locationErrorLabel: '" + locationErrorLabel.getText() + "' (visible=" + locationErrorLabel.isVisible() + ")");
        System.out.println("- startDateErrorLabel: '" + startDateErrorLabel.getText() + "' (visible=" + startDateErrorLabel.isVisible() + ")");
        System.out.println("- endDateErrorLabel: '" + endDateErrorLabel.getText() + "' (visible=" + endDateErrorLabel.isVisible() + ")");
    }




    @FXML
    private void handleModifyCollaboration() {
        if (selectedCollaboration == null) return;

        if (!validateModifyForm()) {
            return; // Stop if validation fails
        }

        try {
            // --------- Copy new image if selected ---------
            String imageName = selectedCollaboration.getImagePath(); // fallback to old image if no new image
            if (selectedImageFile != null) {
                imageName = selectedImageFile.getName();

                // ✅ Save image to external XAMPP uploads folder
                Path uploadsDir = Paths.get("C:/xampp/htdocs/uploads/");
                Files.createDirectories(uploadsDir);

                Path destination = uploadsDir.resolve(imageName);
                Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            }

            // --------- Create Updated Collaboration Object ---------
            Collaboration updated = new Collaboration(
                    selectedCollaboration.getCollaborationId(),  // 🟰 Keep ID of original collaboration
                    modNameField.getText(),
                    modDescField.getText(),
                    modStartDatePicker.getValue(),
                    modEndDatePicker.getValue(),
                    modTypeField.getText(),
                    computeStatus(modStartDatePicker.getValue(), modEndDatePicker.getValue()),
                    Integer.parseInt(modBudgetField.getText()),
                    modLocationField.getText(),
                    modPrerequisField.getText(),
                    imageName,
                    selectedCollaboration.getUserId()
            );
            updated.setCollaborationId(selectedCollaboration.getCollaborationId());

            // --------- Update in database ---------
            CollaborationService service = new CollaborationService();
            service.modifier(updated);

            // --------- Update your view ---------
            int index = data.indexOf(selectedCollaboration);
            if (index != -1) {
                data.set(index, updated);
            }

            displayCardsWithFallback(data);

            modifyFormContainer.setVisible(false);
            dashboardView.setVisible(true);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "La collaboration a été modifiée avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de la collaboration.");
        }
    }

    private String computeStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return "en attente"; // waiting
        } else if ((today.isEqual(startDate) || today.isAfter(startDate)) && today.isBefore(endDate)) {
            return "en cours";   // in progress
        } else {
            return "terminé";    // finished
        }
    }





    private void displayCardsWithFallback(List<Collaboration> collaborations) {
        for (Collaboration col : collaborations) {
            Image img;

            try {
                File file = new File("C:/xampp/htdocs/uploads/" + col.getImagePath());
                if (file.exists()) {
                    img = new Image(file.toURI().toString());
                } else {
                    img = new Image(getClass().getResource("/images/default.png").toExternalForm());
                }
            } catch (Exception e) {
                img = new Image(getClass().getResource("/images/default.png").toExternalForm());
            }

            // TODO: Assign the image to your ImageView or logic
            // Example:
            // imageView.setImage(img);
        }
    }




    @FXML
    private void handleCancelForm() {
        clearFormFields();
        formContainer.setVisible(false);
        dashboardView.setVisible(true);
    }

    @FXML
    private void handleModifyCancelForm() {
        clearFormFields();
        modifyFormContainer.setVisible(false);
        detailsContainer.setVisible(true);
    }
    @FXML
    private void handleCancelDetails() {
        detailsContainer.setVisible(false);
        dashboardView.setVisible(true);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(imagePreview.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imagePathLabel.setText(file.getName());
            imagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleDeleteCollaboration() {
        if (selectedCollaboration != null) {
            try {
                CollaborationService service = new CollaborationService();
                service.supprimer(selectedCollaboration.getCollaborationId());
                data.remove(selectedCollaboration);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La collaboration a été supprimée avec succès !");
                displayCards(data);

                modifyFormContainer.setVisible(false);
                dashboardView.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFormFields() {
        nameField.clear();
        descField.clear();
        typeField.clear();     // ✅ Deselects any selected item
        budgetField.clear();
        locationField.clear();
        prerequisField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private boolean validateModifyForm() {
        boolean isValid = true;

        // Reset all error labels first
        modNameErrorLabel.setVisible(false);
        modDescErrorLabel.setVisible(false);
        modTypeErrorLabel.setVisible(false);
        modBudgetErrorLabel.setVisible(false);
        modLocationErrorLabel.setVisible(false);
        modPrerequisErrorLabel.setVisible(false);
        modStartDateErrorLabel.setVisible(false);
        modEndDateErrorLabel.setVisible(false);
        modImageErrorLabel.setVisible(false);

        // Now validate each field
        if (modNameField.getText().isEmpty()) {
            modNameErrorLabel.setText("Nom requis");
            modNameErrorLabel.setVisible(true);
            isValid = false;
        }

        if (modDescField.getText().isEmpty()) {
            modDescErrorLabel.setText("Description requise");
            modDescErrorLabel.setVisible(true);
            isValid = false;
        }

        if (modTypeField.getText().isEmpty()) {
            modTypeErrorLabel.setText("Type requis");
            modTypeErrorLabel.setVisible(true);
            isValid = false;
        }


        if (modBudgetField.getText().isEmpty()) {
            modBudgetErrorLabel.setText("Budget requis");
            modBudgetErrorLabel.setVisible(true);
            isValid = false;
        } else {
            try {
                Double.parseDouble(modBudgetField.getText());
            } catch (NumberFormatException e) {
                modBudgetErrorLabel.setText("Budget invalide");
                modBudgetErrorLabel.setVisible(true);
                isValid = false;
            }
        }

        if (modLocationField.getText().isEmpty()) {
            modLocationErrorLabel.setText("Lieu requis");
            modLocationErrorLabel.setVisible(true);
            isValid = false;
        }

        if (modPrerequisField.getText().isEmpty()) {
            modPrerequisErrorLabel.setText("Pré-requis requis");
            modPrerequisErrorLabel.setVisible(true);
            isValid = false;
        }

        if (modStartDatePicker.getValue() == null) {
            modStartDateErrorLabel.setText("Date début requise");
            modStartDateErrorLabel.setVisible(true);
            isValid = false;
        }

        if (modEndDatePicker.getValue() == null) {
            modEndDateErrorLabel.setText("Date fin requise");
            modEndDateErrorLabel.setVisible(true);
            isValid = false;
        }

        if (modStartDatePicker.getValue() != null && modEndDatePicker.getValue() != null &&
                modEndDatePicker.getValue().isBefore(modStartDatePicker.getValue())) {
            endDateErrorLabel.setText("La date de fin doit être après la date de début");
            endDateErrorLabel.setVisible(true);
            isValid = false;
        }

        return isValid;
    }


    private boolean validateForm() {
        boolean isValid = true;

        // Hide all error labels at the start
        nameErrorLabel.setVisible(false);
        descErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);
        budgetErrorLabel.setVisible(false);
        locationErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);
        endDateErrorLabel.setVisible(false);

        // Now check each field and set both TEXT and VISIBLE

        if (nameField.getText().trim().isEmpty()) {
            nameErrorLabel.setText("Nom requis"); // <--- you MUST setText
            nameErrorLabel.setVisible(true);
            isValid = false;
        }
        if (descField.getText().trim().isEmpty()) {
            descErrorLabel.setText("Description requise");
            descErrorLabel.setVisible(true);
            isValid = false;
        }
        if (typeField.getText().trim().isEmpty()) {
            typeErrorLabel.setText("Type requis");
            typeErrorLabel.setVisible(true);
            isValid = false;
        }
        if (budgetField.getText().trim().isEmpty()) {
            budgetErrorLabel.setText("Budget requis");
            budgetErrorLabel.setVisible(true);
            isValid = false;
        }
        if (locationField.getText().trim().isEmpty()) {
            locationErrorLabel.setText("Lieu requis");
            locationErrorLabel.setVisible(true);
            isValid = false;
        }
        if (startDatePicker.getValue() == null) {
            startDateErrorLabel.setText("Date de début requise");
            startDateErrorLabel.setVisible(true);
            isValid = false;
        }
        if (endDatePicker.getValue() == null) {
            endDateErrorLabel.setText("Date de fin requise");
            endDateErrorLabel.setVisible(true);
            isValid = false;
        }
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null &&
                endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            endDateErrorLabel.setText("La date de fin doit être après la date de début");
            endDateErrorLabel.setVisible(true);
            isValid = false;
        }

        return isValid;
    }




    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayCards(List<Collaboration> collaborations) {
        cardSlider.getChildren().clear();

        for (Collaboration c : collaborations) {
            VBox card = new VBox(10);
            card.setPrefSize(220, 250);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            card.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4);
        """);

            // Hover effect
            card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #e3f2fd;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.1, 0, 6);
        """));
            card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4);
        """));

            // Labels
            Label nameLabel = new Label(c.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

            Label typeLabel = new Label("Type: " + c.getType());
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

            Label statusLabel = new Label("Statut: " + c.getStatus());
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

            card.getChildren().addAll(nameLabel, typeLabel, statusLabel);
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> showCollaborationDetails(c));

            cardSlider.getChildren().add(card);
        }
    }






    void showCollaborationDetails(Collaboration c) {
        selectedCollaboration = c;
        Timeline refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(8), e -> refreshJoinRequestStatus())
        );
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();

        // Fill collaboration detail fields
        detailName.setText(c.getName());
        detailDesc.setText(c.getDescription());
        detailType.setText(c.getType());
        detailStatus.setText(c.getStatus());
        detailBudget.setText(String.valueOf(c.getBudget()));
        detailLocation.setText(c.getLocation());
        detailPrerequis.setText(c.getPrerequis());
        detailStartDate.setText(c.getStartDate().toString());
        detailEndDate.setText(c.getEndDate().toString());

        try {
            File file = new File("C:/xampp/htdocs/uploads/" + c.getImagePath());
            if (file.exists()) {
                projectImage.setImage(new Image(file.toURI().toString()));
            } else {
                throw new Exception("Collab image not found.");
            }
        } catch (Exception e) {
            System.out.println("Image loading error: " + e.getMessage());
            projectImage.setImage(new Image(getClass().getResource("/images/default.png").toExternalForm()));
        }

        projectListContainer.getChildren().clear();

        try {
            ProjetService projetService = new ProjetService();
            List<Projet> projets = projetService.getProjectsByCollaborationId(c.getCollaborationId());

            for (Projet p : projets) {
                VBox card = new VBox(8);
                card.setPadding(new Insets(12));
                card.setPrefWidth(220);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("""
                -fx-background-color: white;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0.1, 0, 4);
            """);
                card.setCursor(Cursor.HAND);

                ImageView imgView = new ImageView();
                try {
                    File projectFile = new File("C:/xampp/htdocs/uploads/" + p.getImage());
                    if (projectFile.exists()) {
                        imgView.setImage(new Image(projectFile.toURI().toString()));
                    } else {
                        throw new Exception("Project image not found.");
                    }
                } catch (Exception e) {
                    imgView.setImage(new Image(getClass().getResource("/images/default.png").toExternalForm()));
                }

                imgView.setFitWidth(180);
                imgView.setFitHeight(100);
                imgView.setPreserveRatio(true);

                Label title = new Label(p.getTitle());
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label date = new Label("\uD83D\uDCC5 " + p.getStartDate() + " → " + p.getEndDate());
                date.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                Label status = new Label("\uD83D\uDCCC " + p.getStatus());
                status.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");

                Label budget = new Label("\uD83D\uDCB0 " + p.getBudget() + " DT");
                budget.setStyle("-fx-text-fill: #222; -fx-font-size: 13px;");

                card.getChildren().addAll(imgView, title, date, status, budget);
                card.setOnMouseClicked(event -> {
                    selectedProject = p;
                    showProjectDetails(p);
                });

                projectListContainer.getChildren().add(card);
            }

            if (projets.isEmpty()) {
                Label empty = new Label("Aucun projet lié.");
                empty.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                projectListContainer.getChildren().add(empty);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Label error = new Label("Erreur lors du chargement des projets.");
            error.setStyle("-fx-text-fill: red;");
            projectListContainer.getChildren().add(error);
        }

        modifyButton.setOnAction(e -> {
            populateModifyForm(c);
            detailsContainer.setVisible(false);
            modifyFormContainer.setVisible(true);
        });

        deleteButton.setOnAction(e -> handleDeleteCollaboration());

        if (loggedInUserId == c.getUserId()) {
            modifyButton.setVisible(true);
            deleteButton.setVisible(true);
            handleAddProject.setVisible(true);
            requestToJoinButton.setVisible(false);
            qrCodeImageView.setImage(null);
        } else {
            modifyButton.setVisible(false);
            deleteButton.setVisible(false);
            handleAddProject.setVisible(false);
            requestToJoinButton.setVisible(true);

            try {
                CollaborationParticipantService participantService = new CollaborationParticipantService();
                String status = participantService.getRequestStatus(c.getCollaborationId(), loggedInUserId);

                if ("pending".equals(status)) {
                    requestToJoinButton.setText("La demande est envoyée !");
                    requestToJoinButton.setDisable(true);
                    requestToJoinButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                } else if ("accepted".equals(status)) {
                    requestToJoinButton.setText("✅ Votre demande a été acceptée");
                    requestToJoinButton.setDisable(true);
                    requestToJoinButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                } else if ("rejected".equals(status)) {
                    requestToJoinButton.setText("❌ Votre demande a été rejetée");
                    requestToJoinButton.setDisable(true);
                    requestToJoinButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                } else {
                    requestToJoinButton.setText("Demander à rejoindre");
                    requestToJoinButton.setDisable(false);
                    requestToJoinButton.setStyle(null);
                }

                String qrText = "http://192.168.1.8/join_request.php?collaboration_id=" + c.getCollaborationId()
                        + "&user_id=" + loggedInUserId;

                Image qrImage = generateQRCodeImage(qrText, 150, 150);
                qrCodeRequest.setImage(qrImage);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        dashboardView.setVisible(false);
        formContainer.setVisible(false);
        modifyFormContainer.setVisible(false);
        projectDetailsContainer.setVisible(false);
        detailsContainer.setVisible(true);
    }


    private void refreshJoinRequestStatus() {
        if (selectedCollaboration == null) return;

        try {
            CollaborationParticipantService service = new CollaborationParticipantService();
            String status = service.getRequestStatus(selectedCollaboration.getCollaborationId(), loggedInUserId);

            Platform.runLater(() -> {
                switch (status) {
                    case "pending" -> {
                        requestToJoinButton.setText("La demande est envoyée!");
                        requestToJoinButton.setDisable(true);
                        requestToJoinButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                    }
                    case "accepted" -> {
                        requestToJoinButton.setText("✅ Votre demande a été acceptée!");
                        requestToJoinButton.setDisable(true);
                        requestToJoinButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                    }
                    case "rejected" -> {
                        requestToJoinButton.setText("❌ Votre demande a été rejetée!");
                        requestToJoinButton.setDisable(true);
                        requestToJoinButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    }
                    default -> {
                        requestToJoinButton.setText("Demander à rejoindre");
                        requestToJoinButton.setDisable(false);
                        requestToJoinButton.setStyle(null);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }












    private void showProjectDetails(Projet p) {
        projectTitle.setText(p.getTitle());
        projectDesc.setText(p.getDescription());
        projectStatus.setText(p.getStatus());
        projectBudget.setText(p.getBudget() + " DT");
        projectDates.setText(p.getStartDate() + " → " + p.getEndDate());

        // Load project image with fallback
        try {
            String imagePath = p.getImage();
            Image img;

            if (imagePath != null && !imagePath.isEmpty()) {
                URL res = getClass().getResource("/uploads/" + imagePath);
                if (res != null) {
                    img = new Image(res.toExternalForm());
                } else {
                    File fallback = new File("C:/xampp/htdocs/uploads/" + imagePath);
                    if (fallback.exists()) {
                        img = new Image(fallback.toURI().toString());
                    } else {
                        throw new Exception("Image not found");
                    }
                }
            } else {
                throw new Exception("Image is null or empty");
            }

            projectImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Project image fallback used: " + e.getMessage());
            projectImage.setImage(new Image(getClass().getResource("/images/default.png").toExternalForm()));
        }

        projectImage.setPreserveRatio(true);
        projectImage.setSmooth(true);

        // Generate and set QR Code
        String qrText = "Projet: " + p.getTitle() +
                "\nStatus: " + p.getStatus() +
                "\nDates: " + p.getStartDate() + " → " + p.getEndDate() +
                "\nBudget: " + p.getBudget() + " DT";

        Image qrImage = generateQRCodeImage(qrText, 150, 150);
        qrCodeImageView.setImage(qrImage);

        // Toggle visibility
        detailsContainer.setVisible(false);
        projectDetailsContainer.setVisible(true);
    }


    @FXML
    private void handleBackToCollabDetails() {
        projectDetailsContainer.setVisible(false);
        detailsContainer.setVisible(true);
    }

    public Image generateQRCodeImage(String text, int width, int height) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleShowProjectForm(ActionEvent event) {
        selectedCollaborationForProject = selectedCollaboration;
        detailsContainer.setVisible(false);
        projectFormContainer.setVisible(true);
    }

    @FXML
    private void handleCancelProjectForm(ActionEvent event) {
        projectFormContainer.setVisible(false);
        detailsContainer.setVisible(true);
    }

    @FXML
    private void handleAddProject() {
        try {
            if (!validateProjectForm()) return;

            if (selectedCollaborationForProject == null) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Aucune collaboration sélectionnée pour ce projet.");
                return;
            }

            String imageName = (selectedNewProjectImageFile != null) ? selectedNewProjectImageFile.getName() : "default.png";

            if (selectedNewProjectImageFile != null) {
                Path destinationDir = Paths.get("C:/xampp/htdocs/uploads/");
                Files.createDirectories(destinationDir);
                Path destination = destinationDir.resolve(imageName);
                Files.copy(selectedNewProjectImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            }

            // ✅ Compute Status Automatically
            LocalDate now = LocalDate.now();
            LocalDate start = projectStartDatePicker.getValue();
            LocalDate end = projectEndDatePicker.getValue();
            String status;
            if (now.isBefore(start)) {
                status = "En attente"; // before start
            } else if (!now.isAfter(end)) {
                status = "En cours"; // between start and end
            } else {
                status = "Terminé"; // after end
            }

            Projet newProject = new Projet(
                    selectedCollaborationForProject,
                    projectNameField.getText(),
                    projectDescriptionField.getText(),
                    start,
                    end,
                    status,
                    Double.parseDouble(projectBudgetField.getText()),
                    imageName
            );

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Ajouter ce projet ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                ProjetService ps = new ProjetService();
                ps.ajouter(newProject);

                showCollaborationDetails(selectedCollaborationForProject);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le projet a été ajouté avec succès !");
                projectFormContainer.setVisible(false);
                detailsContainer.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez vérifier les champs du projet.");
        }
    }




    @FXML
    private void handleChooseNewProjectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedNewProjectImageFile = file;
            newProjectImageLabel.setText(file.getName());
            newProjectImagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private boolean validateProjectForm() {
        boolean isValid = true;

        // Reset all error labels
        projectNameErrorLabel.setText("");
        projectDescriptionErrorLabel.setText("");
        projectStatusErrorLabel.setText("");
        projectBudgetErrorLabel.setText("");
        projectStartDateErrorLabel.setText("");
        projectEndDateErrorLabel.setText("");
        newProjectImageErrorLabel.setText("");

        // Validate each field
        if (projectNameField.getText().isEmpty()) {
            projectNameErrorLabel.setText("Nom requis");
            isValid = false;
        }

        if (projectDescriptionField.getText().isEmpty()) {
            projectDescriptionErrorLabel.setText("Description requise");
            isValid = false;
        }

        if (projectStatusField.getText().isEmpty()) {
            projectStatusErrorLabel.setText("Statut requis");
            isValid = false;
        }

        if (projectBudgetField.getText().isEmpty()) {
            projectBudgetErrorLabel.setText("Budget requis");
            isValid = false;
        } else {
            try {
                Double.parseDouble(projectBudgetField.getText());
            } catch (NumberFormatException e) {
                projectBudgetErrorLabel.setText("Le budget doit être un nombre");
                isValid = false;
            }
        }

        if (projectStartDatePicker.getValue() == null) {
            projectStartDateErrorLabel.setText("Date de début requise");
            isValid = false;
        }

        if (projectEndDatePicker.getValue() == null) {
            projectEndDateErrorLabel.setText("Date de fin requise");
            isValid = false;
        }

        if (projectStartDatePicker.getValue() != null && projectEndDatePicker.getValue() != null &&
                projectEndDatePicker.getValue().isBefore(projectStartDatePicker.getValue())) {
            projectEndDateErrorLabel.setText("La date de fin doit être après la date de début");
            projectEndDateErrorLabel.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    private boolean validateModifyProjectForm() {
        boolean isValid = true;

        // Reset all error labels
        modProjectTitleError.setText("");
        modProjectDescError.setText("");
        modProjectBudgetError.setText("");
        modProjectDatesError.setText("");
        modProjectImageError.setText("");

        // Validate Title
        if (modProjectTitle.getText().isEmpty()) {
            modProjectTitleError.setText("Titre requis");
            isValid = false;
        }

        // Validate Description
        if (modProjectDesc.getText().isEmpty()) {
            modProjectDescError.setText("Description requise");
            isValid = false;
        }

        // Validate Budget
        if (modProjectBudget.getText().isEmpty()) {
            modProjectBudgetError.setText("Budget requis");
            isValid = false;
        } else {
            try {
                double budget = Double.parseDouble(modProjectBudget.getText());
                if (budget < 0) {
                    modProjectBudgetError.setText("Le budget ne peut pas être négatif");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                modProjectBudgetError.setText("Le budget doit être un nombre valide");
                isValid = false;
            }
        }

        // Validate Dates
        if (modProjectStartDate.getValue() == null) {
            modProjectDatesError.setText("Date de début requise");
            isValid = false;
        }
        if (modProjectEndDate.getValue() == null) {
            modProjectDatesError.setText("Date de fin requise");
            isValid = false;
        }
        if (modProjectStartDate.getValue() != null && modProjectEndDate.getValue() != null &&
                modProjectEndDate.getValue().isBefore(modProjectStartDate.getValue())) {
            modProjectDatesError.setText("La date de fin doit être après la date de début");
            isValid = false;
        }

        return isValid;
    }


    @FXML
    private void handleModifyProject() {
        if (selectedProject == null) return;

        // Populate fields
        modProjectTitle.setText(selectedProject.getTitle());
        modProjectDesc.setText(selectedProject.getDescription());
        modProjectBudget.setText(String.valueOf(selectedProject.getBudget()));
        modProjectStartDate.setValue(selectedProject.getStartDate());
        modProjectEndDate.setValue(selectedProject.getEndDate());
        modProjectImagePath.setText(selectedProject.getImage());

        // Image loading with support for external uploads folder
        try {
            String imageName = selectedProject.getImage();
            File imageFile = new File("C:/xampp/htdocs/uploads/" + imageName); // XAMPP uploads path

            if (imageFile.exists()) {
                modProjectImagePreview.setImage(new Image(imageFile.toURI().toString()));
            } else {
                // Fallback to default image inside resources
                URL fallback = getClass().getResource("/images/default.png");
                if (fallback != null) {
                    modProjectImagePreview.setImage(new Image(fallback.toExternalForm()));
                } else {
                    System.out.println("Default image not found in /images/");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                URL fallback = getClass().getResource("/images/default.png");
                if (fallback != null) {
                    modProjectImagePreview.setImage(new Image(fallback.toExternalForm()));
                }
            } catch (Exception ignored) {
                System.out.println("Even fallback image failed to load.");
            }
        }

        projectDetailsContainer.setVisible(false);
        projectModifyForm.setVisible(true);
    }


    @FXML
    private void handleSaveProjectModifications() {
        if (selectedProject == null) return;

        if (!validateModifyProjectForm()) {
            return; // ❌ Stop if validation fails
        }

        try {
            String title = modProjectTitle.getText();
            String desc = modProjectDesc.getText();
            LocalDate start = modProjectStartDate.getValue();
            LocalDate end = modProjectEndDate.getValue();

            // ✅ Auto-compute project status
            LocalDate now = LocalDate.now();
            String status;
            if (now.isBefore(start)) {
                status = "En attente";
            } else if (!now.isAfter(end)) {
                status = "En cours";
            } else {
                status = "Terminé";
            }

            String image = (selectedProjectImageFile != null) ? selectedProjectImageFile.getName() : selectedProject.getImage();

            // ✅ Handle image upload
            if (selectedProjectImageFile != null) {
                Path dest = Paths.get("C:/xampp/htdocs/uploads/").resolve(image);
                Files.copy(selectedProjectImageFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            }

            // ✅ Create updated project object
            Projet updated = new Projet(
                    selectedProject.getId(),
                    selectedCollaboration,
                    title, desc, start, end, status,
                    Double.parseDouble(modProjectBudget.getText()),  // Safe because already validated
                    image
            );

            // ✅ Confirm before saving
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Modifier ce projet ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                new ProjetService().modifier(updated);
                showCollaborationDetails(selectedCollaboration);
                projectModifyForm.setVisible(false);
                detailsContainer.setVisible(true);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le projet a été modifié avec succès !");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vérifiez les données saisies.");
        }
    }





    @FXML
    private void handleCancelModifyProject() {
        projectModifyForm.setVisible(false);
        projectDetailsContainer.setVisible(true);
    }
    @FXML
    private void handleDeleteProject() {
        if (selectedProject == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce projet ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                new ProjetService().supprimer(selectedProject.getId());
                showCollaborationDetails(selectedCollaboration);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer ce projet.");
            }
        }
    }

    @FXML
    private void handleChooseProjectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedProjectImageFile = file;
            modProjectImagePath.setText(file.getName());
            modProjectImagePreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private void exportProjectDetailsToPDF(Projet p) {
        try {
            // Create document with better margins
            Document document = new Document(PageSize.A4, 36, 36, 54, 54);
            String pdfPath = "project_details_" + p.getId() + "_" + System.currentTimeMillis() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            // ---------- Modern Color Palette ----------
            BaseColor primaryColor = new BaseColor(44, 62, 80);    // Dark blue-gray
            BaseColor secondaryColor = new BaseColor(52, 152, 219); // Bright blue
            BaseColor accentColor = new BaseColor(231, 76, 60);     // Coral red
            BaseColor lightBg = new BaseColor(245, 245, 245);       // Light gray bg

            // ---------- Modern Font Setup ----------
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, primaryColor);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, secondaryColor);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.DARK_GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            Font highlightFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, secondaryColor);

            // ---------- Header with Background ----------
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(20f);

            PdfPCell headerCell = new PdfPCell(new Phrase("PROJECT DETAILS", titleFont));
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setBackgroundColor(lightBg);
            headerCell.setPadding(12);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(headerCell);
            document.add(headerTable);

            // ---------- Project Title Section ----------
            Paragraph projectTitle = new Paragraph(p.getTitle(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor));
            projectTitle.setAlignment(Element.ALIGN_CENTER);
            projectTitle.setSpacingAfter(15f);
            document.add(projectTitle);

            // ---------- Divider Line ----------
            LineSeparator divider = new LineSeparator();
            divider.setLineColor(new BaseColor(200, 200, 200));
            divider.setPercentage(50);
            divider.setAlignment(LineSeparator.ALIGN_CENTER);
            document.add(divider);
            document.add(Chunk.NEWLINE);

            // ---------- Main Info Table ----------
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setSpacingBefore(15f);
            table.setSpacingAfter(25f);
            table.setWidths(new float[]{1.5f, 3.5f});
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Add table rows with improved styling
            addStyledCell(table, "Status:", getStatusWithColor(p.getStatus(), accentColor), labelFont);
            addStyledCell(table, "Budget:", String.format("%,.2f DT", p.getBudget()), labelFont, highlightFont);
            addStyledCell(table, "Timeline:", formatDates(p.getStartDate(), p.getEndDate()), labelFont, valueFont);

            String collabName = (p.getCollaboration() != null) ? p.getCollaboration().getName() : "No collaboration";
            addStyledCell(table, "Collaboration:", collabName, labelFont, valueFont);

            document.add(table);

            // ---------- Additional Sections ----------
            addSection(document, "Project Summary", sectionFont);
            Paragraph summary = new Paragraph(p.getDescription(),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY));
            summary.setSpacingAfter(15f);
            document.add(summary);

            // ---------- Footer ----------
            LocalDate today = LocalDate.now();
            Paragraph footer = new Paragraph("Generated on " + today.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                    " • Confidential",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            showAlert(Alert.AlertType.INFORMATION, "PDF Exported",
                    "Project details exported successfully:\n" + new File(pdfPath).getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Error",
                    "Failed to export project to PDF: " + e.getMessage());
        }
    }

    // Helper method for styled table cells
    private void addStyledCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setPaddingLeft(10);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    // Overloaded version - new version for Phrase value
    private void addStyledCell(PdfPTable table, String label, Phrase value, Font labelFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setPaddingLeft(10);

        PdfPCell valueCell = new PdfPCell(value);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    // Helper method for status with color
    private Phrase getStatusWithColor(String status, BaseColor color) {
        Chunk statusChunk = new Chunk(status, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, color));
        return new Phrase(statusChunk);
    }

    // Helper method for date formatting
    private String formatDates(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return start.format(formatter) + " to " + end.format(formatter) + " (" + calculateDuration(start, end) + ")";
    }

    // Helper method to calculate duration
    private String calculateDuration(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return days + " days";
    }

    // Helper method for section headings
    private void addSection(Document doc, String title, Font font) throws DocumentException {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(20f);
        section.setSpacingAfter(10f);
        doc.add(section);

        LineSeparator sectionDivider = new LineSeparator();
        sectionDivider.setLineColor(new BaseColor(220, 220, 220));
        sectionDivider.setPercentage(30);
        sectionDivider.setAlignment(LineSeparator.ALIGN_LEFT);
        doc.add(sectionDivider);
    }


    // ---------- QR Code Generation ----------
    /*  private Image generateQRCodeImage(Projet projet) {
        try {
            String text = "Projet: " + projet.getTitle() +
                    "\nStatus: " + projet.getStatus() +
                    "\nDates: " + projet.getStartDate() + " → " + projet.getEndDate() +
                    "\nBudget: " + projet.getBudget() + " DT";

            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 150, 150);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
          //  return Image.getInstance(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    // ---------- Helper to add rows to table ----------
    private void addCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell1.setPaddingBottom(6f);
        cell2.setPaddingBottom(6f);
        table.addCell(cell1);
        table.addCell(cell2);
    }


    @FXML
    private void handleExportProjectToPDF() {
        if (selectedProject != null) {
            exportProjectDetailsToPDF(selectedProject);
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun Projet", "Veuillez sélectionner un projet à exporter.");
        }
    }

    @FXML
    private void handleRequestToJoin() {
        try {
            // Instantiate your service
            CollaborationParticipantService participantService = new CollaborationParticipantService();

            // Insert join request into database
            participantService.requestToJoin(selectedCollaboration.getCollaborationId(), loggedInUserId);

            // Change button text and disable
            requestToJoinButton.setText("La demande est envoyée !");
            requestToJoinButton.setDisable(true);

            // Optional: Popup to confirm
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demande envoyée");
            alert.setHeaderText(null);
            alert.setContentText("Votre demande de rejoindre cette collaboration a été envoyée avec succès !");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'envoi de la demande. Veuillez réessayer.");
            alert.showAndWait();
        }
    }


    private final CollaborationService collaborationService = new CollaborationService();
    private void addRequestCard(CollaborationParticipant p) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        FontIcon icon = new FontIcon("fas-user-tie");
        icon.setIconSize(28);

        VBox textContainer = new VBox(4);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        // 🔵 Fetch artisan name if you have a method, else dummy
        String artisanName = "Artisan #" + p.getUserId(); // Later replace with real name from database
        String projectName = "";
        try {
            projectName = collaborationService.getCollaborationNameById(p.getCollaborationId());
        } catch (SQLException e) {
            e.printStackTrace();
            projectName = "Projet inconnu"; // fallback
        }

        HBox nameSpeciality = new HBox(10);
        nameSpeciality.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(artisanName);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label specialityLabel = new Label("Artisan"); // Replace with real specialization later
        specialityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 3 8; -fx-background-color: #f5f7fa; -fx-background-radius: 10;");

        nameSpeciality.getChildren().addAll(nameLabel, specialityLabel);

        Label projectLabel = new Label(projectName);
        projectLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        Label dateLabel = new Label("Demandé le: " + p.getRequestDate());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");

        textContainer.getChildren().addAll(nameSpeciality, projectLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button acceptButton = new Button("Accepter");
        acceptButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 10;");
        acceptButton.setGraphic(new FontIcon("fas-check"));
        acceptButton.setOnAction(e -> handleAcceptRequest(p.getId()));

        Button refuseButton = new Button("Refuser");
        refuseButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10;");
        refuseButton.setGraphic(new FontIcon("fas-times"));
        refuseButton.setOnAction(e -> handleRefuseRequest(p.getId()));


        HBox buttonsBox = new HBox(10, acceptButton, refuseButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(icon, textContainer, spacer, buttonsBox);
        requestsContainer.getChildren().add(card);
    }

    private void handleAcceptRequest(int participantId) {
        try {
            CollaborationParticipantService participantService = new CollaborationParticipantService();
            participantService.acceptRequest(participantId);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demande acceptée");
            alert.setHeaderText(null);
            alert.setContentText("✅ La demande a été acceptée avec succès !");
            alert.showAndWait();

            reloadRequests(); // Refresh the requests list in the UI
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("❌ Erreur lors de l'acceptation de la demande.");
            alert.showAndWait();
        }
    }

    private void handleRefuseRequest(int participantId) {
        try {
            CollaborationParticipantService participantService = new CollaborationParticipantService();
            participantService.rejectRequest(participantId);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Demande refusée");
            alert.setHeaderText(null);
            alert.setContentText("❌ La demande a été refusée.");
            alert.showAndWait();

            reloadRequests(); // Refresh the requests list in the UI
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("❌ Erreur lors du refus de la demande.");
            alert.showAndWait();
        }
    }

    private void reloadRequests() {
        try {
            // Clear the request cards
            requestsContainer.getChildren().clear();

            // Load again for the currently logged-in artisan (owner)
            CollaborationParticipantService participantService = new CollaborationParticipantService();
            List<CollaborationParticipant> requests = participantService.getPendingRequestsForUser(loggedInUserId);

            if (requests.isEmpty()) {
                Label empty = new Label("Aucune demande de participation pour vos collaborations.");
                empty.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
                requestsContainer.getChildren().add(empty);
            } else {
                for (CollaborationParticipant p : requests) {
                    addRequestCard(p); // reuse your method to add a styled card
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label error = new Label("Erreur lors du rechargement des demandes.");
            error.setStyle("-fx-text-fill: red;");
            requestsContainer.getChildren().add(error);
        }
    }



    private final CollaborationParticipantService collaborationParticipantService = new CollaborationParticipantService();
    public void loadRequestsForCurrentUser() {
        requestsContainer.getChildren().clear();

        try {
            List<CollaborationParticipant> requests = collaborationParticipantService.getPendingRequestsForUser(loggedInUserId);

            System.out.println("Fetched requests count: " + requests.size());
            if (requests.isEmpty()) {
                Label empty = new Label("Aucune demande de participation pour vos collaborations.");
                empty.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
                requestsContainer.getChildren().add(empty);
            } else {
                for (CollaborationParticipant p : requests) {
                    addRequestCard(p); // Generate card as before
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void toggleChatbot() {
        chatWindow.setVisible(!chatWindow.isVisible());
    }



    private void addMessage(String sender, String text, String bgColor) {
        VBox messageBox = new VBox();
        messageBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-padding: 10;");
        messageBox.setMaxWidth(250);

        Text senderText = new Text(sender + ":");
        senderText.setStyle("-fx-font-weight: bold;");

        Text messageText = new Text(text);
        messageText.setWrappingWidth(220);

        messageBox.getChildren().addAll(senderText, messageText);
        chatBody.getChildren().add(messageBox);

        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }


    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    @FXML
    public void sendMessage() {
        if (userInput == null || chatBody == null) return;

        String message = userInput.getText().trim();
        if (message.isEmpty()) return;

        // Display user message
        displayUserMessage(message);
        userInput.clear();

        // Check if question is relevant to cinema complaint system
        boolean isRelevant = keywordsReclamation.stream()
                .anyMatch(keyword -> message.toLowerCase().contains(keyword.toLowerCase()));

        // Display thinking message
        Text thinkingText = new Text("En train de réfléchir...");
        thinkingText.setFill(Color.GRAY);

        TextFlow thinkingFlow = new TextFlow(thinkingText);
        HBox thinkingBox = new HBox(thinkingFlow);
        thinkingBox.setAlignment(Pos.CENTER_LEFT);

        Platform.runLater(() -> {
            chatBody.getChildren().add(thinkingBox);
            scrollToBottom();
        });

        // Simulate thinking time
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (isRelevant) {
                // Process with Gemini API
                CompletableFuture.runAsync(() -> {
                    try {
                        String botResponse = callGeminiAPI(message);

                        // Remove thinking message and display actual response
                        Platform.runLater(() -> {
                            chatBody.getChildren().remove(thinkingBox);
                            displayBotMessage(botResponse);
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            chatBody.getChildren().remove(thinkingBox);
                            displayBotMessage("Désolé, je ne peux pas répondre pour le moment. Erreur technique.");
                        });
                        ex.printStackTrace();
                    }
                });
            } else {
                // Not relevant to cinema complaints
                Platform.runLater(() -> {
                    chatBody.getChildren().remove(thinkingBox);
                    displayBotMessage("Désolé, je ne peux répondre qu'aux questions concernant les collaborations, projets, ateliers ou artisans. Veuillez poser une question liée à la gestion de ces entités.");
                });
            }
        });
        pause.play();
    }

    private void displayUserMessage(String message) {
        if (chatBody == null) return;

        Text text = new Text(message);
        text.setFill(Color.BLACK);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 15px; -fx-padding: 8px;");

        HBox hbox = new HBox(textFlow);
        hbox.setAlignment(Pos.CENTER_RIGHT);

        Platform.runLater(() -> {
            chatBody.getChildren().add(hbox);
            scrollToBottom();
        });
    }

    private void displayBotMessage(String message) {
        if (chatBody == null) return;

        Text text = new Text(message);
        text.setFill(Color.BLACK);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #F1F0F0; -fx-background-radius: 15px; -fx-padding: 8px;");

        HBox hbox = new HBox(textFlow);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Platform.runLater(() -> {
            chatBody.getChildren().add(hbox);
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        if (chatScrollPane != null) {
            chatScrollPane.setVvalue(1.0);
        }
    }

    private final List<String> keywordsReclamation = Arrays.asList(
            "artisanat", "artisan", "collaboration", "coopérative", "projet", "atelier",
            "produit local", "savoir-faire", "tissage", "poterie", "cuir", "bois", "textile",
            "bijoux", "mosaïque", "céramique", "tapis", "cooperative", "Tunisie", "Algérie",
            "Maroc", "traditionnel", "métiers d'art", "fait main", "exposition", "foire",
            "promotion", "commerce équitable", "culturel"
    );

    private String callGeminiAPI(String userMessage) throws Exception {
        URL url = new URL(API_URL + "?key=" + API_KEY);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Prepare request JSON
        String requestBody = "{"
                + "\"contents\": [{"
                + "\"parts\": [{"
                + "\"text\": \"" + userMessage.replace("\"", "\\\"") + "\""
                + "}]"
                + "}]"
                + "}";

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        JSONObject firstPart = parts.getJSONObject(0);

        return firstPart.getString("text");
    }




}