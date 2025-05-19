package tn.esprit.services;

import tn.esprit.entities.Collaboration;

import java.sql.SQLException;
import java.util.List;

public interface ICollaboration<T> {
    List<T> getAll() throws SQLException;
    List<T> recuperer() throws SQLException;
    void ajouter(T t) throws SQLException;
    void supprimer(int collaborationId) throws SQLException;
    void modifier(T t) throws SQLException;
}
