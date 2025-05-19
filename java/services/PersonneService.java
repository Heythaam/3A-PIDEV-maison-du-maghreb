package tn.esprit.services;

import tn.esprit.entities.Personne;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements IServices<Personne>{
    Connection cnx;
    public PersonneService() {
        cnx= MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Personne personne) throws SQLException {
        String sql="insert into personne(nom,prenom,age)" +
                "values(?,?,?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1,personne.getNom());
        ps.setInt(3,personne.getAge());
        ps.setString(2,personne.getPrenom());
        ps.executeUpdate();
        System.out.println("Personne ajoutée");
    }

    @Override
    public void supprimer(Personne personne) {

    }

    @Override
    public void modifier(int id,String nom) throws SQLException {
        String sql ="UPDATE personne SET nom ='"+nom+"'"+"where id ="+id;
        Statement ste = cnx.createStatement();
        ste.executeUpdate(sql);
        System.out.println("Personne modifiée");
    }

    @Override
    public List<Personne> recuperer() throws SQLException {
        String sql="select * from personne";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        List<Personne> personnes =new ArrayList<>();
        while(rs.next()){
            int id = rs.getInt("id");
            String nom =rs.getString("nom");
            String prenom=rs.getString("prenom");
            int age = rs.getInt("age");
            Personne p = new Personne(id,age,nom,prenom);
            personnes.add(p);
        }
        return personnes;
    }
}
