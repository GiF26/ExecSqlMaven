import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ExecutarSQL {

    private Connection conexao;

    // Construtor que recebe a conexão com o banco de dados
    public ExecutarSQL(Connection conexao) {
        this.conexao = conexao;
    }

    // Método para executar um comando SQL
    public void executarComando(String sql) throws SQLException, SQLException {
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }

}

