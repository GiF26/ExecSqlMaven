import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
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
    private ConectionConf con;
    private String server;
    private String port;
    private String dataBase;
    private String user;
    private String password;
    private Connection conexao;
    private String caminho;

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
                selecionarDiretorioEExecutarSQL();
                txtPath.setText(caminho);
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
                    executarSQLDoDiretorio(new File(txtPath.getText()));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    // Método para permitir ao usuário selecionar o diretório
    private void selecionarDiretorioEExecutarSQL() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            caminho = String.valueOf(fileChooser.getSelectedFile());
            lblInfo.setText("Executando arquivos do diretório...");
//            executarSQLDoDiretorio(selectedDirectory);
        } else {
            lblInfo.setText("Nenhum diretório selecionado.");
        }
    }

    // Método para listar e executar todos os arquivos SQL no diretório
    private void executarSQLDoDiretorio(File diretorio) {
        try {
            ExecutarSQL executorSQL = new ExecutarSQL(conexao);

            File[] arquivos = diretorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));
            if (arquivos != null && arquivos.length > 0) {
                for (File arquivoSQL : arquivos) {
                    String sql = lerConteudoDoArquivo(arquivoSQL.toPath());
                    try {
                        executorSQL.executarComando(sql);
                        lblInfo.setText("Arquivo " + arquivoSQL.getName() + " executado com sucesso.\n");
                        lblInfo.getText();
                    } catch (SQLException ex) {
                        lblInfo.setText("Erro ao executar " + arquivoSQL.getName() + ": " + ex.getMessage() + "\n");
                        lblInfo.getText();
                    }
                }
                lblInfo.setText("Execução completa.");
                lblInfo.getText();
            } else {
                lblInfo.setText("Nenhum arquivo .sql encontrado no diretório.");
                lblInfo.getText();
            }
        } catch (Exception ex) {
            lblInfo.setText("Erro ao executar SQL: " + ex.getMessage() + "\n");
            lblInfo.getText();
            lblInfo.setText("Erro ao executar SQL.");
            lblInfo.getText();
        }
    }

    // Método para ler o conteúdo de um arquivo SQL
    private String lerConteudoDoArquivo(Path caminhoDoArquivo) throws IOException {
        List<String> linhas = Files.readAllLines(caminhoDoArquivo);
        System.out.println("leu");
        return String.join("\n", linhas);
    }

}
