package tn.esprit.services;

import java.sql.SQLException;
import java.util.List;
import tn.esprit.entities.CollaborationParticipant;

public interface ICollaborationParticipant {

    // Check if the artisan already sent a join request
    boolean hasAlreadyRequested(int collaborationId, int userId) throws SQLException;

    // Send a join request (insert pending request)
    void requestToJoin(int collaborationId, int userId) throws SQLException;

    // Accept a join request
    void acceptRequest(int participantId) throws SQLException;

    // Reject a join request
    void rejectRequest(int participantId) throws SQLException;

    // List all pending join requests for a collaboration
    List<CollaborationParticipant> listPendingRequests(int collaborationId) throws SQLException;
}
