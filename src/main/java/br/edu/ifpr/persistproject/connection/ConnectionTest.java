package br.edu.ifpr.persistproject.connection;

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

        //teste 2

        SellerRepository repository = new SellerRepository();

        Seller seller = new Seller();
        seller.setName("Maria");

        //default, ISO_LOCAL_DATE
        //if the date is not in ISO 8601 format, it must be formatted
        seller.setBirthDate(LocalDate.parse("2016-08-16"));
        seller.setBaseSalary(5000.0);

        repository.insert(seller);

        List<Seller> sellers = repository.getSellers();

        sellers.forEach( s -> System.out.println(s) );
        
    }
}
