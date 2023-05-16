package br.edu.ifpr.persistproject.connection;

import br.edu.ifpr.persistproject.exception.DatabaseIntegrityException;

import java.sql.*;

public class ConnectionFactory {

    private static Connection conn;

    /*
    * Padrão de Projeto Singleton
    * Garante que exista apenas uma referência para o objeto "conn" em memória.
    * Em algumas literaturas é considerado um anti-padrão (pesquisa) */
    public static Connection getConnection(){

        try {

            if(conn == null){
                Class.forName("com.mysql.jdbc.Driver"); //nessário para conexões em aplicações web
                conn = DriverManager.getConnection("jdbc:mysql://localhost/ifpr_store", "root", "bancodedados");
            }

            return conn;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * Encerra o recurso statement.
    * Objetos do tipo Statment e PreparedStatement podem guardar muitas informações de consulta sql.
    * Manter esses objetos em memória é custoso. Por isso é recomendado encerrá-los após seu uso.
    * Pesquisa: por que optamos pela abordagem de usar um método estático, para encerrar esse recurso?*/
    public static void statementClose(Statement statement){
        try {
            statement.close();
        } catch (SQLException e) {
            throw new DatabaseIntegrityException(e.getMessage());
        }
    }

    /*
     * Encerra o recurso resultSet.
     * Objetos do tipo ResultSet guardam em memória todas as linhas de uma tabela retornada de uma consulta.
     * Imagine que em uma determinada consulta ao banco de dados, sejam retornados mil registros.
     * Imagine também que a cada atualização de página (F5), uma nova consulta é realizada e um novo resultSet
     * é instânciado e guardado em memória (com mil registros). Ainda, imagine que 100 acessem esse método
     * e que cada um deles atualize essa página 5 vezes. Conseguiu perceber o problema?
     * Assim como Statment, manter esses objetos em memória é custoso. Por isso é recomendado
     * encerrá-los após seu uso.*/
    public static void resultSetClose(ResultSet resultSet){
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new DatabaseIntegrityException(e.getMessage());
        }
    }

}
