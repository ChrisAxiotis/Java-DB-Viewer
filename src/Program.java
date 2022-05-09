import javax.sound.sampled.Port;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Program implements ActionListener {

    JFrame f = new JFrame("DB Connector");//creating instance of JFrame

    JPanel menu_panel = new JPanel();
    JPanel con_panel = new JPanel();
    TextArea textArea;

    JTextField url;
    JTextField port;
    JTextField username;
    JPasswordField  password;


    String connection_status = "Not Connected";



    public void Init(){
        DisplayInit();
        Menu();
    }

    public void DisplayInit(){

        f.setSize(300,300);//400 width and 500 height
        f.setLocationRelativeTo(null);
        f.setVisible(true);//making the frame visible
        f.setLayout(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        menu_panel.setBounds(0,0,300,300);
        menu_panel.setLayout(null);
        con_panel.setBounds(0,0,300,300);
        con_panel.setLayout(null);

        //-------------Menu UI--------------------------
        JLabel l_menu = new JLabel("MENU", SwingConstants.CENTER);
        l_menu.setBounds(0,10,300,15);
        menu_panel.add(l_menu);

        JButton connect_btn = new JButton("Connect");
        connect_btn.setBounds(0,40,300,50);
        connect_btn.setActionCommand("connect");
        connect_btn.addActionListener(this);
        menu_panel.add(connect_btn);

        JButton test_btn = new JButton("Test Connect");
        test_btn.setBounds(0,100,300,50);
        test_btn.setActionCommand("test");
        test_btn.addActionListener(this);
        menu_panel.add(test_btn);

        JButton exit_btn = new JButton("Exit");
        exit_btn.setBounds(0,160,300,50);
        exit_btn.setActionCommand("exit");
        exit_btn.addActionListener(this);
        menu_panel.add(exit_btn);

        JLabel label = new JLabel("Created By Christopher Axiotis", SwingConstants.CENTER);
        label.setBounds(0,230,300,15);
        menu_panel.add(label);


        //-------------Connection UI--------------------------

        //Top Title
        JLabel con_l = new JLabel("Connection", SwingConstants.CENTER);
        con_l.setBounds(0,10,300,15);
        con_panel.add(con_l);


        //database url START
        JLabel db_label = new JLabel("Database URL: ", SwingConstants.RIGHT);
        db_label.setText("Database URL:");
        db_label.setBounds(0,15 + 40,100,20);
        con_panel.add(db_label);

        url = new JTextField("localhost");
        url.setBounds(110,15 + 40,160,20);
        con_panel.add(url);
        //database url END



        //database port START
        JLabel port_l = new JLabel("Port:", SwingConstants.RIGHT);
        port_l.setText("Port:");
        port_l.setBounds(0,50 + 40,100,20);
        con_panel.add(port_l);

        port = new JTextField("3306");
        port.setBounds(110,50 + 40,160,20);
        con_panel.add(port);
        //database port END

        //database username START
        JLabel user_l = new JLabel("Username:", SwingConstants.RIGHT);
        user_l.setText("Username:");
        user_l.setBounds(0,85 + 40,100,20);
        con_panel.add(user_l);

        username = new JTextField("root");
        username.setBounds(110,85 + 40,160,20);
        con_panel.add(username);
        //database username END

        //database password START
        JLabel pass_l = new JLabel("Pass:", SwingConstants.RIGHT);
        pass_l.setText("Password:");
        pass_l.setBounds(0,120 + 40,100,20);
        con_panel.add(pass_l);

        password = new JPasswordField();
        password.setBounds(110,120 + 40,160,20);
        con_panel.add(password);
        //database password END



        JButton con_btn = new JButton("Next");
        con_btn.setBounds(200,225,70,30);
        con_btn.setActionCommand("next");
        con_btn.addActionListener(this);
        con_panel.add(con_btn);

        JButton back_btn = new JButton("Back");
        back_btn.setBounds(10,225,70,30);
        back_btn.setActionCommand("back");
        back_btn.addActionListener(this);
        con_panel.add(back_btn);

        f.add(menu_panel);
        f.add(con_panel);

    }

    public void Menu(){

        f.setContentPane(menu_panel);
        f.revalidate();
        f.setVisible(true);
    }

    public void ConnectionDisplay(){
        f.setContentPane(con_panel);
        f.revalidate();
        f.setVisible(true);
    }

    public void ValidateConnection(){

        boolean can_connect = true;

        if(url.getText().length() < 1){
            infoBox("No URL Provided", "Alert!");
            can_connect = false;
        }


        if(username.getText().length() < 1){
            infoBox("No USERNAME Provided", "Alert!");
            can_connect = false;
        }


        if(port.getText().length() < 1){
            infoBox("No PORT Provided", "Alert!");
            can_connect = false;
        }

        if(can_connect){
            infoBox("Connecting as: " + username.getText(), "Alert!");
            try {
                DisplayData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void DisplayData() throws SQLException {

        Connector c = new Connector(url.getText(), username.getText(),new String(password.getPassword()),port.getText());

        JFrame output = new JFrame(url.getText());
        output.setSize(600,600);//400 width and 500 height
        output.setLocationRelativeTo(null);
        output.setVisible(true);//making the frame visible
        output.setResizable(true);
        //output.setLayout(new GridLayout(1,2));
        output.setLayout(null);



        List<String> schemas = c.GetSchemas();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Schemas");

        for (String str: schemas) {
            DefaultMutableTreeNode schema = new DefaultMutableTreeNode(str);

            List<String> tables = c.GetTable(str);

            for (String s: tables) {
                DefaultMutableTreeNode table = new DefaultMutableTreeNode(s);
                schema.add(table);
            }
            root.add(schema);
        }

        JTree tree = new JTree(root);
        JScrollPane tree_scroll = new JScrollPane(tree);
        tree_scroll.setBounds(0,0,200,output.getHeight());

        output.add(tree_scroll);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();

                if (node == null) return;

                Object nodeInfo = node.getUserObject();
                System.out.println(nodeInfo);

                try {
                    for (int i = 1; i < output.getContentPane().getComponentCount(); i++){
                        Print(output.getComponentCount());
                        output.getContentPane().remove(i);
                    }


                    JTable table = c.GetFromTable(node.getParent().toString(), nodeInfo.toString());

                    JScrollPane table_scroll = new JScrollPane(table);
                    table_scroll.setBounds(210,5,output.getWidth() - 240,output.getHeight() - 50);

                    table_scroll.addComponentListener(new ComponentAdapter(){
                        @Override
                        public void componentResized(ComponentEvent e) {
                            table_scroll.setBounds(210,5,output.getWidth() - 240,output.getHeight() - 50);
                        }
                    });


                    table.revalidate();
                    table.repaint();
                    table_scroll.revalidate();
                    table_scroll.repaint();
                    output.add(table_scroll);
                    output.setVisible(true);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

    }



    public void actionPerformed(ActionEvent e) {
        String actionCommand = ((JButton) e.getSource()).getActionCommand();

        switch (actionCommand) {
            case "connect":
                ConnectionDisplay();
                break;
            case "test":
                infoBox("Test Box Alert", "Alert!");
                break;

            case "exit":
                System.exit(0);
                break;

            case "next":
                ValidateConnection();
                break;

            case "back":
                Menu();
                break;
        }
    }

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }




    public static void Print(Object str){
        System.out.println(str);
    }




}//End Class
