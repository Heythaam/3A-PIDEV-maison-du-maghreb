package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Collaboration;
import tn.esprit.entities.Projet;
import tn.esprit.services.CollaborationService;
import tn.esprit.services.ProjetService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalProjectsLabel;
    @FXML private Label totalCollaborationsLabel;
    @FXML private PieChart statsChart;

    // Collaboration table fields
    @FXML private TableView<Collaboration> collaborationTable;
    @FXML private TableColumn<Collaboration, String> nameColumn;
    @FXML private ImageView logoView;
    @FXML private TableColumn<Collaboration, String> artisanColumn;
    @FXML private TableColumn<Collaboration, String> typeColumn;
    @FXML private TableColumn<Collaboration, String> statusColumn;
    @FXML private TableColumn<Collaboration, Integer> budgetColumn;
    @FXML private TableColumn<Collaboration, Void> actionsColumn;
    @FXML private Label pageLabel;
    @FXML private TextField searchField;
    @FXML private Button dashboardButton;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TextField searchProjectField;
    @FXML private ComboBox<String> sortProjectComboBox;
    @FXML private ComboBox<String> statusProjectFilterComboBox;

    private List<Projet> allProjects;
    private List<Projet> filteredProjects;

    // Project table fields
    @FXML private TableView<Projet> projectTable;
    @FXML private TableColumn<Projet, String> projectTitleColumn;
    @FXML private TableColumn<Projet, String> projectCollabColumn;
    @FXML private TableColumn<Projet, String> projectStatusColumn;
    @FXML private TableColumn<Projet, String> projectStartColumn;
    @FXML private TableColumn<Projet, String> projectEndColumn;
    @FXML private TableColumn<Projet, Integer> projectBudgetColumn;
    @FXML private TableColumn<Projet, Void> projectActionsColumn;

    private final CollaborationService collaborationService = new CollaborationService();
    private final ProjetService projetService = new ProjetService();

    private int currentPage = 1;
    private final int itemsPerPage = 5;
    private List<Collaboration> allCollaborations;
    private List<Collaboration> filteredCollaborations;
    private int projectCurrentPage = 1;
    private final int projectItemsPerPage = 5;
    @FXML private Label projectPageLabel;

    public void initialize() {
        // Set static labels
        totalUsersLabel.setText("25 Utilisateurs");
        totalProjectsLabel.setText("12 Projets");
        totalCollaborationsLabel.setText("7 Collaborations");

        // ------------------- COLLABORATION UI LOGIC --------------------
        sortComboBox.getItems().addAll("Budget", "Start Date", "End Date");
        sortComboBox.setOnAction(event -> handleSort());

        statusFilterComboBox.getItems().addAll("Tous", "Active", "En attente", "Terminé", "Annulé");
        statusFilterComboBox.setValue("Tous");
        statusFilterComboBox.setOnAction(event -> handleFilterByStatus());

        initCollaborationTable();
        File file = new File("C:/xampp/htdocs/images/logo.png");
        if (file.exists()) {
            logoView.setImage(new Image(file.toURI().toString()));
        } else {
            System.out.println("Logo image not found at " + file.getAbsolutePath());
        }

        // ------------------- PROJECT UI LOGIC --------------------
        searchProjectField.setOnKeyReleased(this::handleProjectSearch);

        sortProjectComboBox.getItems().addAll("Budget", "Start Date", "End Date");
        sortProjectComboBox.setOnAction(e -> handleProjectSort());

        statusProjectFilterComboBox.getItems().addAll("Tous", "En cours", "Prévu", "Terminé", "Annulé");
        statusProjectFilterComboBox.setValue("Tous");
        statusProjectFilterComboBox.setOnAction(e -> handleProjectFilterByStatus());

        initProjectTable();

        // ------------------- CHART INIT --------------------
        initChart();
    }

    @FXML
    private void handleProjectSearch(KeyEvent event) {
        String query = searchProjectField.getText().toLowerCase();
        filteredProjects = allProjects.stream()
                .filter(p -> p.getTitle().toLowerCase().contains(query)
                        || p.getStatus().toLowerCase().contains(query))
                .collect(Collectors.toList());
        updateProjectTable();
    }

    private void handleProjectFilterByStatus() {
        String selectedStatus = statusProjectFilterComboBox.getValue();
        if (selectedStatus == null || selectedStatus.equals("Tous")) {
            filteredProjects = new ArrayList<>(allProjects);
        } else {
            filteredProjects = allProjects.stream()
                    .filter(p -> p.getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
        }
        updateProjectTable();
    }

    private void handleProjectSort() {
        String selected = sortProjectComboBox.getValue();
        if (selected == null) return;

        switch (selected) {
            case "Budget" -> filteredProjects.sort(Comparator.comparingDouble(Projet::getBudget));
            case "Start Date" -> filteredProjects.sort(Comparator.comparing(Projet::getStartDate));
            case "End Date" -> filteredProjects.sort(Comparator.comparing(Projet::getEndDate));
        }
        updateProjectTable();
    }

    private void updateProjectTable() {
        int fromIndex = (projectCurrentPage - 1) * projectItemsPerPage;
        int toIndex = Math.min(fromIndex + projectItemsPerPage, filteredProjects.size());
        projectTable.getItems().setAll(filteredProjects.subList(fromIndex, toIndex));
        int totalPages = (filteredProjects.size() + projectItemsPerPage - 1) / projectItemsPerPage;
        projectPageLabel.setText("Page " + projectCurrentPage + " of " + totalPages);
    }

    public void nextProjectPage() {
        if (projectCurrentPage * projectItemsPerPage < filteredProjects.size()) {
            projectCurrentPage++;
            updateProjectTable();
        }
    }

    public void previousProjectPage() {
        if (projectCurrentPage > 1) {
            projectCurrentPage--;
            updateProjectTable();
        }
    }


    private void initCollaborationTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artisanColumn.setCellValueFactory(new PropertyValueFactory<>("type")); // Placeholder
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));
        addActionsColumn();

        try {
            allCollaborations = collaborationService.recuperer();
            filteredCollaborations = new ArrayList<>(allCollaborations);
            updateCollaborationTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCollaborationTable() {
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredCollaborations.size());
        collaborationTable.getItems().setAll(filteredCollaborations.subList(fromIndex, toIndex));
        pageLabel.setText("Page " + currentPage + " of " + ((filteredCollaborations.size() + itemsPerPage - 1) / itemsPerPage));
    }

    public void nextPage() {
        if (currentPage * itemsPerPage < filteredCollaborations.size()) {
            currentPage++;
            updateCollaborationTable();
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateCollaborationTable();
        }
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String query = searchField.getText().toLowerCase();
        filteredCollaborations = allCollaborations.stream()
                .filter(c -> c.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());
        currentPage = 1;
        updateCollaborationTable();
    }

    private void handleSort() {
        String selected = sortComboBox.getValue();
        if (selected == null) return;

        switch (selected) {
            case "Budget" -> filteredCollaborations.sort(Comparator.comparingInt(Collaboration::getBudget));
            case "Start Date" -> filteredCollaborations.sort(Comparator.comparing(Collaboration::getStartDate));
            case "End Date" -> filteredCollaborations.sort(Comparator.comparing(Collaboration::getEndDate));
        }
        currentPage = 1;
        updateCollaborationTable();
    }

    private void handleFilterByStatus() {
        String selectedStatus = statusFilterComboBox.getValue();
        if (selectedStatus == null || selectedStatus.equals("Tous")) {
            filteredCollaborations = new ArrayList<>(allCollaborations);
        } else {
            filteredCollaborations = allCollaborations.stream()
                    .filter(c -> c.getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
        }
        currentPage = 1;
        updateCollaborationTable();
    }

    private void initProjectTable() {
        projectTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        projectCollabColumn.setCellValueFactory(cellData -> {
            Projet p = cellData.getValue();
            String collabName = (p.getCollaboration() != null) ? p.getCollaboration().getName() : "N/A";
            return new ReadOnlyStringWrapper(collabName);
        });
        projectStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        projectStartColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        projectEndColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        projectBudgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));

        addProjectActionsColumn();

        try {
            allProjects = projetService.recuperer();
            filteredProjects = new ArrayList<>(allProjects);
            updateProjectTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void initChart() {
        try {
            List<Collaboration> collaborations = collaborationService.recuperer();
            List<Projet> projets = projetService.recuperer();

            long actives = collaborations.stream()
                    .filter(c -> "Active".equalsIgnoreCase(c.getStatus()))
                    .count();

            long enAttente = collaborations.stream()
                    .filter(c -> "En attente".equalsIgnoreCase(c.getStatus()))
                    .count();

            long terminees = collaborations.stream()
                    .filter(c -> "Terminé".equalsIgnoreCase(c.getStatus()))
                    .count();

            long annulees = collaborations.stream()
                    .filter(c -> "Annulé".equalsIgnoreCase(c.getStatus()))
                    .count();

            long projetsActifs = projets.stream()
                    .filter(p -> "En cours".equalsIgnoreCase(p.getStatus()))
                    .count();

            long projetsPrevus = projets.stream()
                    .filter(p -> "Prévu".equalsIgnoreCase(p.getStatus()))
                    .count();

            long projetsTermines = projets.stream()
                    .filter(p -> "Terminé".equalsIgnoreCase(p.getStatus()))
                    .count();

            statsChart.getData().clear();

            statsChart.getData().addAll(
                    new PieChart.Data("Collabs actives", actives),
                    new PieChart.Data("Collabs en attente", enAttente),
                    new PieChart.Data("Collabs terminées", terminees),
                    new PieChart.Data("Collabs annulées", annulees),
                    new PieChart.Data("Projets actifs", projetsActifs),
                    new PieChart.Data("Projets prévus", projetsPrevus),
                    new PieChart.Data("Projets terminés", projetsTermines)
            );

            // Set label color
            Platform.runLater(() -> {
                for (Node node : statsChart.lookupAll(".chart-legend-item")) {
                    if (node instanceof Label label) {
                        label.setStyle("-fx-text-fill: black;");
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    private void addProjectActionsColumn() {
        projectActionsColumn.setCellFactory(getProjectActionCellFactory());
    }

    private void addActionsColumn() {
        actionsColumn.setCellFactory(getCollabActionCellFactory());
    }

    private Callback<TableColumn<Collaboration, Void>, TableCell<Collaboration, Void>> getCollabActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button viewButton = new Button("Voir");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox actionBox = new HBox(10, viewButton, deleteButton);

            {
                // Style buttons
                viewButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6;");

                // Handle 'Voir'
                viewButton.setOnAction(e -> {
                    Collaboration collab = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/collaboration.fxml"));
                        Parent root = loader.load();

                        CollaborationController controller = loader.getController();
                        controller.showCollaborationDetails(collab); // Send data

                        // Replace content on main pane (adjust to your layout)
                        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                // Handle 'Supprimer'
                deleteButton.setOnAction(e -> {
                    Collaboration collab = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette collaboration ?", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Confirmation");
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) {
                        try {
                            CollaborationService service = new CollaborationService();
                            service.supprimer(collab.getCollaborationId());
                            getTableView().getItems().remove(collab); // update UI
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            Alert err = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
                            err.showAndWait();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        };
    }

    private Callback<TableColumn<Projet, Void>, TableCell<Projet, Void>> getProjectActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button viewButton = new Button("Voir");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonBox = new HBox(10, viewButton, deleteButton);

            {
                viewButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6;");

                viewButton.setOnAction(event -> {
                    Projet project = getTableView().getItems().get(getIndex());
                    System.out.println("Viewing project: " + project.getTitle());
                    // Add actual view logic if needed
                });

                deleteButton.setOnAction(event -> {
                    Projet project = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce projet ?", ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText("Confirmation de suppression");
                    confirm.showAndWait();

                    if (confirm.getResult() == ButtonType.YES) {
                        try {
                            ProjetService service = new ProjetService();
                            service.supprimer(project.getId());
                            getTableView().getItems().remove(project);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Alert err = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression du projet.");
                            err.showAndWait();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        };
    }

    @FXML
    private void handleBackToCollaborationCards(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application.fxml"));
            Parent collaborationsView = loader.load();
            dashboardButton.getScene().setRoot(collaborationsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenProjectsView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/projets.fxml")); // Path to your projects FXML
            Parent projectView = loader.load();
            dashboardButton.getScene().setRoot(projectView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
