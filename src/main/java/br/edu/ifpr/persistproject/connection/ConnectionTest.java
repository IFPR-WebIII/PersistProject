package br.edu.ifpr.persistproject.connection;

import br.edu.ifpr.persistproject.model.Department;
import br.edu.ifpr.persistproject.model.Seller;
import br.edu.ifpr.persistproject.repository.SellerRepository;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ConnectionTest {

    public static void main(String[] args) {

        SellerRepository repository = new SellerRepository();

        Department department = new Department();
        department.setId(2);

        List<Seller> sellers = repository.findByDepartment(department);

        for(Seller s: sellers){
            System.out.println(s);
        }

    }
}
