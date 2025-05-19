package tn.esprit.services;

import tn.esprit.entities.Collaboration;
import tn.esprit.entities.Projet;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetService implements IProjet {

    private final Connection cnx = MyDataBase.getInstance().getCnx();
    private final CollaborationService collaborationService = new CollaborationService();

    @Override
    public void ajouter(Projet p) throws SQLException {
        String query = "INSERT INTO projet (collaboration_id, title, description, start_date, end_date, status, budget, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, p.getCollaboration().getCollaborationId());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getDescription());
            ps.setDate(4, Date.valueOf(p.getStartDate()));
            ps.setDate(5, Date.valueOf(p.getEndDate()));
            ps.setString(6, p.getStatus());
            ps.setDouble(7, p.getBudget());
            ps.setString(8, p.getImage());
            ps.executeUpdate();
        }
    }

    @Override
    public void modifier(Projet p) throws SQLException {
        String query = "UPDATE projet SET title = ?, description = ?, start_date = ?, end_date = ?, status = ?, budget = ?, image = ? " +
                "WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setDate(3, Date.valueOf(p.getStartDate()));
            ps.setDate(4, Date.valueOf(p.getEndDate()));
            ps.setString(5, p.getStatus());
            ps.setDouble(6, p.getBudget());
            ps.setString(7, p.getImage());
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM projet WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Projet> recuperer() throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String query = "SELECT * FROM projet";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                projets.add(mapProjet(rs));
            }
        }
        return projets;
    }

    @Override
    public List<Projet> getByCollaborationId(int collaborationId) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        String query = "SELECT * FROM projet WHERE collaboration_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, collaborationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    projets.add(mapProjet(rs));
                }
            }
        }
        return projets;
    }

    private Projet mapProjet(ResultSet rs) throws SQLException {
        int collabId = rs.getInt("collaboration_id");
        Collaboration collaboration = collaborationService.getById(collabId); // You must implement this

        return new Projet(
                rs.getInt("id"),
                collaboration,
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("status"),
                rs.getDouble("budget"),
                rs.getString("image")
        );
    }

    public List<Projet> getProjectsByCollaborationId(int collaborationId) throws SQLException {
        return getByCollaborationId(collaborationId); // Reuse logic
    }
}
