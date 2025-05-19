package tn.esprit.services;

import tn.esprit.entities.CollaborationParticipant;
import tn.esprit.tools.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollaborationParticipantService implements ICollaborationParticipant {
    private final Connection cnx;

    public CollaborationParticipantService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    // Check if the artisan already requested
    @Override
    public boolean hasAlreadyRequested(int collaborationId, int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM collaboration_participants WHERE collaboration_id = ? AND user_id = ? AND status = 'pending'";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, collaborationId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }


    // Send Join Request
    @Override
    public void requestToJoin(int collaborationId, int userId) throws SQLException {
        if (!hasAlreadyRequested(collaborationId, userId)) {
            String query = "INSERT INTO collaboration_participants (collaboration_id, user_id) VALUES (?, ?)";
            PreparedStatement ps = cnx.prepareStatement(query); // FIXED here
            ps.setInt(1, collaborationId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("Request sent successfully.");
        } else {
            System.out.println("You have already requested to join this collaboration.");
        }
    }
    @Override
    public void acceptRequest(int participantId) throws SQLException {
        String query = "UPDATE collaboration_participants SET status = 'accepted' WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, participantId);
        ps.executeUpdate();
        System.out.println("Request accepted successfully.");
    }

    @Override
    public void rejectRequest(int participantId) throws SQLException {
        String query = "UPDATE collaboration_participants SET status = 'rejected' WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, participantId);
        ps.executeUpdate();
        System.out.println("Request rejected successfully.");
    }


    @Override
    public List<CollaborationParticipant> listPendingRequests(int collaborationId) throws SQLException {
        List<CollaborationParticipant> pendingRequests = new ArrayList<>();

        String query = "SELECT * FROM collaboration_participants WHERE collaboration_id = ? AND status = 'pending'";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, collaborationId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            CollaborationParticipant participant = new CollaborationParticipant();
            participant.setId(rs.getInt("id"));
            participant.setCollaborationId(rs.getInt("collaboration_id"));
            participant.setUserId(rs.getInt("user_id"));
            participant.setStatus(rs.getString("status"));
            participant.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());

            pendingRequests.add(participant);
        }

        return pendingRequests;
    }

    public List<CollaborationParticipant> getPendingRequestsForUser(int ownerUserId) throws SQLException {
        List<CollaborationParticipant> list = new ArrayList<>();

        String query = """
        SELECT cp.*
        FROM collaboration_participants cp
        JOIN collaboration c ON cp.collaboration_id = c.collaboration_id
        WHERE c.user_id = ? AND cp.status = 'pending'
    """;

        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, ownerUserId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            CollaborationParticipant p = new CollaborationParticipant();
            p.setId(rs.getInt("id"));
            p.setUserId(rs.getInt("user_id"));
            p.setCollaborationId(rs.getInt("collaboration_id"));
            p.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
            list.add(p);
        }
        return list;
    }

    public String getRequestStatus(int collaborationId, int userId) throws SQLException {
        String query = "SELECT status FROM collaboration_participants WHERE collaboration_id = ? AND user_id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, collaborationId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("status");
        }
        return null;
    }

}
