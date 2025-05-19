package tn.esprit.entities;

import java.time.LocalDate;
import java.util.List;

public class Collaboration {
    private int collaborationId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private String status;
    private int budget;
    private String location;
    private String prerequis;
    private String imagePath;
    private int userId; // 👈 NEW: ID of the artisan (creator/owner)
    private String qrCodePath; // 👈 NEW: QR code path
    private String barcodePath; // 👈 NEW: Barcode path (optional)
    private List<Projet> projets;

    // ---------- Constructors ----------

    public Collaboration() {}

    public Collaboration(String name, String description, LocalDate startDate, LocalDate endDate,
                         String type, String status, int budget, String location, String prerequis,
                         String imagePath, int userId) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.status = status;
        this.budget = budget;
        this.location = location;
        this.prerequis = prerequis;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    public Collaboration(int collaborationId, String name, String description, LocalDate startDate, LocalDate endDate,
                         String type, String status, int budget, String location, String prerequis,
                         String imagePath, int userId) {
        this.collaborationId = collaborationId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.status = status;
        this.budget = budget;
        this.location = location;
        this.prerequis = prerequis;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    // ---------- Getters and Setters ----------

    public int getCollaborationId() { return collaborationId; }
    public void setCollaborationId(int collaborationId) { this.collaborationId = collaborationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getBudget() { return budget; }
    public void setBudget(int budget) { this.budget = budget; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPrerequis() { return prerequis; }
    public void setPrerequis(String prerequis) { this.prerequis = prerequis; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public List<Projet> getProjets() { return projets; }
    public void setProjets(List<Projet> projets) { this.projets = projets; }

    // ---------- toString ----------

    @Override
    public String toString() {
        return "Collaboration{" +
                "collaborationId=" + collaborationId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", budget=" + budget +
                ", location='" + location + '\'' +
                ", prerequis='" + prerequis + '\'' +
                ", userId=" + userId +
                '}';
    }
}
