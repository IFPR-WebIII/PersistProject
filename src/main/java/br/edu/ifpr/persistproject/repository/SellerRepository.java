package br.edu.ifpr.persistproject.repository;

import br.edu.ifpr.persistproject.connection.ConnectionFactory;
import br.edu.ifpr.persistproject.exception.DatabaseIntegrityException;
import br.edu.ifpr.persistproject.model.Department;
import br.edu.ifpr.persistproject.model.Seller;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

        java.sql.Date date = Date.valueOf(seller.getBirthDate());


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

                //adds the data from the database to the existing object
                seller.setId(id);

                System.out.println("Done! " + rowsAffected + " rows affected");
                System.out.println("Id generated: " + id);
            }


        } catch (SQLException e) {
            throw new DatabaseIntegrityException(e.getMessage());
        } finally {

            //close resources to avoid memory overflow
            ConnectionFactory.statementClose(statement);
        }

        //returns the object already informed
        // with the Id generated by the database
        return seller;

    }

    public void updateSalary(Double bonus, Integer departmentId){

        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("UPDATE seller SET BaseSalary = BaseSalary + ? WHERE DepartmentId = ?");
            statement.setDouble(1, bonus);
            statement.setInt(2, departmentId);

            Integer rowsAffected = statement.executeUpdate();

            System.out.println("Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionFactory.statementClose(statement);
        }

    }

    public void delete(Integer id) {

        PreparedStatement statement = null;

        try {

            statement = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
            statement.setInt(1, id);

            Integer rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionFactory.statementClose(statement);
        }

    }

    public Seller findById(Integer id){


        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Seller seller = null;
        Department department = null;

        try {
            statement = conn.prepareStatement("" +
                    "SELECT seller.*,department.Name as DepName " +
                    "FROM seller " +
                    "INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "WHERE seller.Id = ?");

            statement.setInt(1, id);

            resultSet = statement.executeQuery();

            while (resultSet.next()){
                department = instantiateDepartment(resultSet);
                seller = instantiateSeller(resultSet, department);
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            ConnectionFactory.statementClose(statement);
            ConnectionFactory.resultSetClose(resultSet);
        }

        return seller;

    }

    public List<Seller> findByDepartment(Department department){

        List<Seller> sellersList = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            statement = conn.prepareStatement("" +
                    "SELECT seller.*,department.Name as DepName " +
                    "FROM seller INNER JOIN department " +
                    "ON seller.DepartmentId = department.Id " +
                    "WHERE DepartmentId = ? " +
                    "ORDER BY Name");

            statement.setInt(1, department.getId());

            resultSet = statement.executeQuery();

            while(resultSet.next()){

                Department dep = instantiateDepartment(resultSet);
                Seller seller = instantiateSeller(resultSet, dep);

                sellersList.add(seller);
            }

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionFactory.statementClose(statement);
            ConnectionFactory.resultSetClose(resultSet);
        }

        return sellersList;

    }

    public Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
        Seller seller = new Seller();
        seller.setId(resultSet.getInt("id"));
        seller.setName(resultSet.getString("Name"));
        seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
        seller.setBirthDate(resultSet.getDate("BirthDate").toLocalDate());
        seller.setDepartment(department);

        return seller;
    }

    public Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department department = new Department();
        department.setId(resultSet.getInt("DepartmentId"));
        department.setName(resultSet.getString("DepName"));

        return department;
    }

}
