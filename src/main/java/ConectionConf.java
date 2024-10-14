import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionConf {
    private String server;
    private String dataBase;
    private String user;
    private String password;
    private String port;

    public ConectionConf(String server, String dataBase, String user, String password, String port) throws SQLException {
        this.server = server;
        this.dataBase = dataBase;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    // Método que retorna a conexão com o banco de dados
    public Connection conectar(String server, String dataBase, String user, String password, String port) throws SQLException {
        String url = "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + dataBase;

        try {
            // Registrar o driver JDBC
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Estabelecer conexão
            return DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}