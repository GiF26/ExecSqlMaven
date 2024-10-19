import org.mozilla.universalchardet.UniversalDetector;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ExecutarSQL {

    private Connection conexao;

    // Construtor que recebe a conexão com o banco de dados
    public ExecutarSQL(Connection conexao) {
        this.conexao = conexao;
    }

    // Método para permitir ao usuário selecionar o diretório
    String selecionarDiretorioEExecutarSQL() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        String caminho = "";

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            caminho = String.valueOf(fileChooser.getSelectedFile());
            System.out.println("Executando arquivos do diretório...");
//            lblInfo.setText("Executando arquivos do diretório...");
//            executarSQLDoDiretorio(selectedDirectory);
        } else {
            System.out.println("Nenhum diretório selecionado");
//            lblInfo.setText("Nenhum diretório selecionado.");
        }

        return caminho;
    }

    // Método para listar e executar todos os arquivos SQL no diretório
    void executarSQLDoDiretorio(File diretorio) {
        try {

            File[] arquivos = diretorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));
            if (arquivos != null && arquivos.length > 0) {
                for (File arquivoSQL : arquivos) {
                    String sql = lerConteudoDoArquivo(arquivoSQL.toPath());
                    try {
                        System.out.println("Arquivo " + arquivoSQL.getName() + " executado com sucesso.\n");
                        executarComando(sql);
//                        lblInfo.setText("Arquivo " + arquivoSQL.getName() + " executado com sucesso.\n");
//                        lblInfo.getText();
                    } catch (SQLException ex) {
                        System.out.println("Erro ao executar " + arquivoSQL.getName() + ": " + ex.getMessage() + "\n");
//                        lblInfo.setText("Erro ao executar " + arquivoSQL.getName() + ": " + ex.getMessage() + "\n");
//                        lblInfo.getText();
                    }
                }
                System.out.println("Execução completa.");
//                lblInfo.setText("Execução completa.");
//                lblInfo.getText();
            } else {
//                lblInfo.setText("Nenhum arquivo .sql encontrado no diretório.");
//                lblInfo.getText();
                System.out.println("Nenhum arquivo .sql encontrado no diretório.");
            }
        } catch (Exception ex) {
//            lblInfo.setText("Erro ao executar SQL: " + ex.getMessage() + "\n");
//            lblInfo.getText();
//            lblInfo.setText("Erro ao executar SQL.");
//            lblInfo.getText();
        }
    }

    private String lerConteudoDoArquivo(Path caminhoDoArquivo) throws IOException {
        byte[] bytes = Files.readAllBytes(caminhoDoArquivo);
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();

        // Se o charset não for detectado, use UTF-8 por padrão
        Charset charset = (encoding != null) ? Charset.forName(encoding) : StandardCharsets.UTF_8;
        List<String> linhas = Files.readAllLines(caminhoDoArquivo, charset);
        return String.join("\n", linhas);
    }


    // Método para executar um comando SQL
    public void executarComando(String sql) throws SQLException, SQLException {
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }

}

