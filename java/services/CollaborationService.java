package tn.esprit.services;

import tn.esprit.entities.Collaboration;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollaborationService implements ICollaboration<Collaboration> {

    private final Connection cnx;

    public CollaborationService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public List<Collaboration> getAll() throws SQLException {
        String sql = "SELECT * FROM collaboration";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Collaboration> collaborations = new ArrayList<>();

        while (rs.next()) {
            collaborations.add(mapCollaboration(rs));
        }

        return collaborations;
    }

    public List<Collaboration> recuperer() throws SQLException {
        String sql = "SELECT * FROM collaboration";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Collaboration> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapCollaboration(rs));
        }
        return list;
    }

    public void ajouter(Collaboration collaboration) throws SQLException {
        String sql = "INSERT INTO collaboration (name, description, start_date, end_date, type, status, budget, location, prerequis, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, collaboration.getName());
            ps.setString(2, collaboration.getDescription());
            ps.setDate(3, Date.valueOf(collaboration.getStartDate()));
            ps.setDate(4, Date.valueOf(collaboration.getEndDate()));
            ps.setString(5, collaboration.getType());
            ps.setString(6, collaboration.getStatus());
            ps.setInt(7, collaboration.getBudget());
            ps.setString(8, collaboration.getLocation());
            ps.setString(9, collaboration.getPrerequis());
            ps.setString(10, collaboration.getImagePath());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    collaboration.setCollaborationId(rs.getInt(1));
                }
            }
        }

        System.out.println("✅ Collaboration ajoutée.");
    }

    public void modifier(Collaboration collaboration) throws SQLException {
        String sql = "UPDATE collaboration SET name = ?, description = ?, start_date = ?, end_date = ?, type = ?, status = ?, budget = ?, location = ?, prerequis = ?, image = ? WHERE collaboration_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, collaboration.getName());
            ps.setString(2, collaboration.getDescription());
            ps.setDate(3, Date.valueOf(collaboration.getStartDate()));
            ps.setDate(4, Date.valueOf(collaboration.getEndDate()));
            ps.setString(5, collaboration.getType());
            ps.setString(6, collaboration.getStatus());
            ps.setInt(7, collaboration.getBudget());
            ps.setString(8, collaboration.getLocation());
            ps.setString(9, collaboration.getPrerequis());
            ps.setString(10, collaboration.getImagePath());
            ps.setInt(11, collaboration.getCollaborationId());

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "✅ Collaboration modifiée." : "⚠️ Aucune modification effectuée.");
        }
    }

    public void supprimer(int collaborationId) throws SQLException {
        String sql = "DELETE FROM collaboration WHERE collaboration_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, collaborationId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "🗑 Collaboration supprimée." : "⚠️ Aucune collaboration trouvée.");
        }
    }

    public Collaboration getById(int id) throws SQLException {
        String sql = "SELECT * FROM collaboration WHERE collaboration_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCollaboration(rs);
                }
            }
        }
        return null;
    }

    private Collaboration mapCollaboration(ResultSet rs) throws SQLException {
        return new Collaboration(
                rs.getInt("collaboration_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("type"),
                rs.getString("status"),
                rs.getInt("budget"),
                rs.getString("location"),
                rs.getString("prerequis"),
                rs.getString("image"),
                rs.getInt("user_id")
        );
    }
    public String getCollaborationNameById(int collaborationId) throws SQLException {
        String collabName = "Collaboration inconnue"; // Default if not found

        String query = "SELECT name FROM collaboration WHERE collaboration_id = ?";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, collaborationId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            collabName = rs.getString("name");
        }

        return collabName;
    }
}
