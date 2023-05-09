package br.edu.ifpr.persistproject.repository;

import br.edu.ifpr.persistproject.connection.ConnectionFactory;
import br.edu.ifpr.persistproject.model.Seller;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SellerRepository {

    private Connection conn;

    public SellerRepository(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        conn = connectionFactory.getConnection();
    }

    public List<Seller> getSellers(){

        List<Seller> sellers = new ArrayList<>();

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM seller");

            while (resultSet.next()){
                Seller seller = new Seller();

                seller.setId(resultSet.getInt("Id"));
                seller.setName(resultSet.getString("Name"));
                seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
                seller.setBirthDate(resultSet.getDate("BirthDate").toLocalDate());

                sellers.add(seller);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            ConnectionFactory.resultSetClose(resultSet);
            ConnectionFactory.statementClose(statement);
        }

        return sellers;

    }

    public Seller insert(Seller seller){

        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("INSERT INTO seller (" +
                    "Name, " +
                    "Email, " +
                    "BirthDate, " +
                    "BaseSalary, " +
                    "DepartmentId) " +
                    "VALUES (?, ?, ?, ?, ?);", PreparedStatement.RETURN_GENERATED_KEYS);

            statement.setString(1, seller.getName());
            statement.setString(2, "jefferson.chaves@ifpr.edu.br");
            //statement.setDate(3, new Date(dateFormat.parse("26/04/1989").getTime()));
            statement.setDate(3, Date.valueOf(seller.getBirthDate()));
            statement.setDouble(4, seller.getBaseSalary());
            statement.setInt(5, 2);

            Integer rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0){

                ResultSet keys = statement.getGeneratedKeys();
                keys.next();
                Integer id = keys.getInt(1);

                seller.setId(id);


                System.out.println("Done! " + rowsAffected + " rows affected");
                System.out.println("Id generated: " + id);
            }


        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            ConnectionFactory.statementClose(statement);
        }

        return seller;


    }

}
