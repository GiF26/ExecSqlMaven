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

    private String retornoLbl;
    private File caminho;
    private Connection con;

    public File getCaminho() {
        return caminho;
    }

    public void setCaminho(File caminho) {
        this.caminho = caminho;
    }

    public String getRetornoLbl() {
        return retornoLbl;
    }

    public void setRetornoLbl(String retornoLbl) {
        retornoLbl = retornoLbl;
    }

    public ExecutarSQL() {
    }

    // Método para permitir ao usuário selecionar o diretório
    public File selecionarDiretorioEExecutarSQL() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            caminho = fileChooser.getSelectedFile();
        } else {
            System.out.println("Nenhum diretório selecionado");
            setRetornoLbl("Nenhum diretório selecionado.");
        }
        setCaminho(caminho);
        System.out.println(getCaminho());
        return caminho;
    }

    String lerConteudoDoArquivo(Path caminhoDoArquivo) throws IOException {
        UniversalDetector detector = new UniversalDetector(null);

        byte[] bytes = Files.readAllBytes(caminhoDoArquivo);

        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();

        // Se o charset não for detectado, use UTF-8 por padrão
        Charset charset = (encoding != null) ? Charset.forName(encoding) : StandardCharsets.UTF_8;
        List<String> linhas = Files.readAllLines(caminhoDoArquivo, charset);

        for(int i = 0; i < linhas.size(); i++){
            if(linhas.get(i).contains("GO")){
                linhas.remove(i);
            }
        }

        return String.join("\n", linhas);
    }

    // Método para executar um comando SQL
    public void executarComando(String sql, Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }
}

