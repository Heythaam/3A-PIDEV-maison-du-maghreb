package tn.esprit.services;

import tn.esprit.entities.Personne;
import tn.esprit.entities.Collaboration;


import java.sql.SQLException;
import java.util.List;

public interface IServices<T> {
    void ajouter(T t) throws SQLException;
    void supprimer(T t);
    void modifier(int id,String nom) throws SQLException;
    List<T> recuperer() throws SQLException;
}
