package tn.esprit.services;

import tn.esprit.entities.Projet;

import java.sql.SQLException;
import java.util.List;

public interface IProjet {
    void ajouter(Projet p) throws SQLException;
    void modifier(Projet p) throws SQLException;
    void supprimer(int id) throws SQLException;
    List<Projet> recuperer() throws SQLException;
    List<Projet> getByCollaborationId(int collaborationId) throws SQLException;
}
