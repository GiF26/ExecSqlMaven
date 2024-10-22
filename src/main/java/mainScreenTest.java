import org.mozilla.universalchardet.UniversalDetector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class mainScreenTest extends JFrame {
    private JLabel llbTitle;
    private JPanel formData;
    private JTextField txtPath;
    private JTextField txtServer;
    private JTextField txtDatabase;
    private JTextField txtUser;
    private JButton btnExecute;
    private JLabel lblServer;
    private JLabel lblDatabase;
    private JPanel formMain;
    private JLabel labUser;
    private JLabel lblPassWord;
    private JPasswordField txtPasswordField;
    private JButton btnPath;
    private JTextField txtPorta;
    private JLabel lblPorta;
    private JLabel lblInfo;
    private String server;
    private String port;
    private String dataBase;
    private String user;
    private String password;
    private Connection conexao;
    private File caminho;

    // Método principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new mainScreenTest().setVisible(true);
            }
        });
    }

    //Construtor
    public mainScreenTest(){
        super.setContentPane(formMain);
        setSize(800, 600);  // Defina o tamanho que preferir
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// Centralizar a janela na tela
        pack();// Ajusta o layout da janela de acordo com os componentes

        btnPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPath.setText(String.valueOf(selecionarDiretorioEExecutarSQL()));
                txtPath.getText();
            }
        });

        // Adiciona ação ao botão de execução
        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server = txtServer.getText();
                port = txtPorta.getText();
                dataBase = txtDatabase.getText();
                user = txtUser.getText();
                password = txtPasswordField.getText();

                try {
                    ConectionConf conf = new ConectionConf(server, dataBase, user, password, port);
                    conexao = conf.conectar(server, dataBase, user, password, port);
                    executarSQLDoDiretorio(caminho);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    // Método para permitir ao usuário selecionar o diretório
    File selecionarDiretorioEExecutarSQL() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            caminho = fileChooser.getSelectedFile();
        } else {
            System.out.println("Nenhum diretório selecionado");
            lblInfo.setText("Nenhum diretório selecionado.");
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
                    System.out.println("Executando arquivos do diretório...");
                    status(arquivoSQL);
                    try {
                        System.out.println("Arquivo " + arquivoSQL.getName() + " executado com sucesso.\n");
                        executarComando(sql);
                    } catch (SQLException ex) {
                        System.out.println("Erro ao executar " + arquivoSQL.getName() + ": " + ex.getMessage() + "\n");
                        lblInfo.setText("Arquivo " + arquivoSQL.getName() + " executado com sucesso.\n");
//                        lblInfo.setText("Erro ao executar " + arquivoSQL.getName() + ": " + ex.getMessage() + "\n");
//                        lblInfo.getText();
                    }
                }
                System.out.println("Execução completa.");
                lblInfo.setText("Execução completa.");
            } else {
                lblInfo.setText("Nenhum arquivo .sql encontrado no diretório.");
                System.out.println("Nenhum arquivo .sql encontrado no diretório.");
            }
        } catch (Exception ex) {
            lblInfo.setText("Erro ao executar SQL: " + ex.getMessage() + "\n");
            lblInfo.setText("Erro ao executar SQL.");
        }
    }

    private String lerConteudoDoArquivo(Path caminhoDoArquivo) throws IOException {
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
    public void executarComando(String sql) throws SQLException {
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void status(File arq){
        lblInfo.setText("Executando arquivos do diretório...");
        lblInfo.setText("Arquivo " + arq.getName() + " executado com sucesso.\n");
    }
}
