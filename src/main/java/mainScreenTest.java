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
    private ConectionConf con;
    private String server;
    private String port;
    private String dataBase;
    private String user;
    private String password;
    private Connection conexao;
    private ExecutarSQL exec = new ExecutarSQL(conexao);
//    private String caminho;

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
                txtPath.setText(exec.selecionarDiretorioEExecutarSQL());
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
                    exec = new ExecutarSQL(conexao);
                    exec.executarSQLDoDiretorio(new File(txtPath.getText()));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
