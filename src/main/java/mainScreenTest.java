import org.mozilla.universalchardet.UniversalDetector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

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
    private ExecutarSQL objExec = new ExecutarSQL();
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
        setSize(1500, 600);  // Defina o tamanho que preferir
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);// Centralizar a janela na tela
        pack();// Ajusta o layout da janela de acordo com os componentes

        btnPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objExec.selecionarDiretorioEExecutarSQL();
                status();
                txtPath.setText(String.valueOf(objExec.getCaminho()));
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
                    executarSQLDoDiretorio(objExec.getCaminho());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void status(){
        lblInfo.setText(objExec.getRetornoLbl());
    }

    // Método para listar e executar todos os arquivos SQL no diretório
    void executarSQLDoDiretorio(File diretorio) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() {
                try {
                    File[] arquivos = diretorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));

                    if (arquivos != null && arquivos.length > 0) {
                        for (File arquivoSQL : arquivos) {
                            String sql = objExec.lerConteudoDoArquivo(arquivoSQL.toPath());
                            publish("Executando arquivo " + arquivoSQL.getName() + "...");
                            try {
                                objExec.executarComando(sql, conexao);
                                publish("Arquivo " + arquivoSQL.getName() + " executado com sucesso.\n");
                            } catch (SQLException ex) {
                                publish("Erro ao executar " + arquivoSQL.getName() + ": " + ex.getMessage() + "\n");
                            }
                        }
                        publish("Execução completa.");
                    } else {
                        publish("Nenhum arquivo .sql encontrado no diretório.");
                    }
                } catch (Exception ex) {
                    publish("Erro ao executar SQL: " + ex.getMessage() + "\n");
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String mensagem : chunks) {
                    lblInfo.setText(mensagem);  // Atualiza a label com cada mensagem publicada
                }
            }

            @Override
            protected void done() {
                lblInfo.setText("Processo finalizado.");
            }
        };

        worker.execute();  // Inicia o SwingWorker
    }
}
