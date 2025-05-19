package tn.esprit.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CollaborationParticipant {
    private int id;
    private int collaborationId;
    private int userId;
    private String status; // 'pending', 'accepted', 'rejected'
    private LocalDateTime requestDate;

    // ---------- Constructors ----------

    // Empty constructor
    public CollaborationParticipant() {
    }

    // Full constructor
    public CollaborationParticipant(int id, int collaborationId, int userId, String status, LocalDateTime requestDate) {
        this.id = id;
        this.collaborationId = collaborationId;
        this.userId = userId;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Constructor for creating a pending request (without id/requestDate)
    public CollaborationParticipant(int collaborationId, int userId) {
        this.collaborationId = collaborationId;
        this.userId = userId;
        this.status = "pending"; // default when creating
    }

    // ---------- Getters and Setters ----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCollaborationId() {
        return collaborationId;
    }

    public void setCollaborationId(int collaborationId) {
        this.collaborationId = collaborationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    // ---------- Optional: toString ----------

    @Override
    public String toString() {
        return "CollaborationParticipant{" +
                "id=" + id +
                ", collaborationId=" + collaborationId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }
}
