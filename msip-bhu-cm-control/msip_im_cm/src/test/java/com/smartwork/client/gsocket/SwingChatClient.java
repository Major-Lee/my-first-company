package com.smartwork.client.gsocket;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.smartwork.client.gsocket.SwingChatClientHandler.Callback;


/**
 * Simple chat client based on Swing & MINA that implements the chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SwingChatClient extends JFrame implements Callback {
    private static final long serialVersionUID = 1538675161745436968L;

    private JTextField inputText;
    
    private JTextField toField;
    private JTextField cidField;

    private JButton loginButton;

    private JButton quitButton;

    private JButton closeButton;

    private JTextField serverField;

    private JTextField nameField;

    private JTextArea area;
    JCheckBox context = new JCheckBox();
    JCheckBox check = new JCheckBox();
    //private JScrollBar scroll;
    private JScrollPane scroll = null;//new JScrollPane(txaDisplay);
    private ChatClientSupport client;
    Socket conn = null;
    //private NioSocketConnector connector;

    public SwingChatClient() {
        super("Chat Client based on Socket");

       /* NetworkConfig config = new NetworkConfig();
		config.setConnectTimeout(20);
		SyncMissianProxyFactory factory = new SyncMissianProxyFactory(new CommonSocketPool(config));*/
		//factory.getSocket(host, port);
		//connector = new NioSocketConnector();

        loginButton = new JButton(new LoginAction());
        loginButton.setText("Connect");
        quitButton = new JButton(new LogoutAction());
        quitButton.setText("Disconnect");
        closeButton = new JButton(new QuitAction());
        closeButton.setText("Quit");
        inputText = new JTextField(30);
        inputText.setAction(new BroadcastAction());
        
        toField = new JTextField(15);
        toField.setText("user2");
        cidField = new JTextField(15);
        cidField.setText("20140707154303rcbP200008-200007.zip");
        check.setSelected(true);
        area = new JTextArea(10, 50);
        //area.setLineWrap(true);
        area.setEditable(false);
        area.setAutoscrolls(true);
        scroll = new JScrollPane(area);
        /*scroll = new JScrollBar();
        scroll.add(area);*/
        scroll.setHorizontalScrollBarPolicy(
        		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		/*scroll.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); */
        nameField = new JTextField(10);
        nameField.setEditable(false);
        serverField = new JTextField(10);
        serverField.setEditable(false);

        JPanel h = new JPanel();
        h.setLayout(new BoxLayout(h, BoxLayout.LINE_AXIS));
        h.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel nameLabel = new JLabel("Name: ");
        JLabel serverLabel = new JLabel("Server: ");
        h.add(nameLabel);
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(nameField);
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(Box.createHorizontalGlue());
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(serverLabel);
        h.add(Box.createRigidArea(new Dimension(10, 0)));
        h.add(serverField);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
        left.add(scroll);
        /*left.add(Box.createRigidArea(new Dimension(10, 0)));
        left.add(Box.createHorizontalGlue());
        JLabel toUserLabel = new JLabel("To: ");
        left.add(toUserLabel);
        //left.add(Box.createRigidArea(new Dimension(10, 0)));
        left.add(toText);*/
        
        
        JPanel inner_1 = new JPanel();
        inner_1.setLayout(new BoxLayout(inner_1, BoxLayout.LINE_AXIS));
        inner_1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel toUserLabel = new JLabel("To: ");
        JLabel contextLabel = new JLabel("Contextid: ");
        JLabel checkLabel = new JLabel("type: ");
        inner_1.add(toUserLabel);
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        inner_1.add(toField);
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        inner_1.add(Box.createHorizontalGlue());
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        inner_1.add(contextLabel);
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        inner_1.add(cidField);
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        inner_1.add(Box.createHorizontalGlue());
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        inner_1.add(checkLabel);
        inner_1.add(Box.createRigidArea(new Dimension(10, 0)));
        
        check.setText("文本");
        context.setText("Context");
        inner_1.add(context);
        inner_1.add(check);
        left.add(inner_1);
        
        left.add(Box.createRigidArea(new Dimension(0, 5)));
        left.add(Box.createHorizontalGlue());
        
        
        left.add(inputText);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
        right.add(loginButton);
        right.add(Box.createRigidArea(new Dimension(0, 5)));
        right.add(quitButton);
        right.add(Box.createHorizontalGlue());
        right.add(Box.createRigidArea(new Dimension(0, 25)));
        right.add(closeButton);

        p.add(left);
        p.add(Box.createRigidArea(new Dimension(10, 0)));
        p.add(right);

        getContentPane().add(h, BorderLayout.NORTH);
        getContentPane().add(p);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*client.quit();
                connector.dispose(); */
                dispose();
            }
        });
        setLoggedOut();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public class LoginAction extends AbstractAction {
        private static final long serialVersionUID = 3596719854773863244L;

        public void actionPerformed(ActionEvent e) {

            ConnectDialog dialog = new ConnectDialog(SwingChatClient.this);
            dialog.pack();
            dialog.setVisible(true);

            if (dialog.isCancelled()) {
                return;
            }

            InetSocketAddress address = parseSocketAddress(dialog
                    .getServerAddress());
            String name = dialog.getUsername();
            String atoken = dialog.getAtoken();
            //handler = new SwingChatClientHandler(SwingChatClient.this);
            client = new ChatClientSupport(name,atoken,SwingChatClient.this);
            nameField.setText(name);
            serverField.setText(dialog.getServerAddress());

            if (!client.connect(address, dialog.isUseSsl())) {
                JOptionPane.showMessageDialog(SwingChatClient.this,
                        "Could not connect to " + dialog.getServerAddress()
                                + ". ");
            }
        }
    }

    private class LogoutAction extends AbstractAction {
        private static final long serialVersionUID = 1655297424639924560L;

        public void actionPerformed(ActionEvent e) {
            try {
                client.quit();
                setLoggedOut();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(SwingChatClient.this,
                        "Session could not be closed.");
            }
            SwingChatClient.this.dispose();
        }
    }

    private class BroadcastAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = -6276019615521905411L;

        public void actionPerformed(ActionEvent e) {
        	String to = toField.getText();
        	String cid = cidField.getText();
        	boolean iscontext = context.isSelected();
        	boolean selected = check.isSelected();
        	System.out.println("~~~~~~~~~~~:"+cid);
        	client.send(to, cid,iscontext, selected, inputText.getText());
            //client.broadcast(inputText.getText());
            inputText.setText("");
        }
    }

    private class QuitAction extends AbstractAction {
        private static final long serialVersionUID = -6389802816912005370L;

        public void actionPerformed(ActionEvent e) {
            if (client != null) {
                client.quit();
            }
            SwingChatClient.this.dispose();
        }
    }

    private void setLoggedOut() {
        inputText.setEnabled(false);
        quitButton.setEnabled(false);
        loginButton.setEnabled(true);
    }

    private void setLoggedIn() {
        area.setText("");
        inputText.setEnabled(true);
        quitButton.setEnabled(true);
        loginButton.setEnabled(false);
    }

    private void append(String text) {
        area.append(text);
        area.setCaretPosition(area.getText().length());//滾動到底端
    }

    private void notifyError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private InetSocketAddress parseSocketAddress(String s) {
        s = s.trim();
        int colonIndex = s.indexOf(":");
        if (colonIndex > 0) {
            String host = s.substring(0, colonIndex);
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(host, port);
        } else {
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(port);
        }
    }

    private int parsePort(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Illegal port number: " + s);
        }
    }

    public void connected() {
        //client.login();
    }

    public void disconnected() {
        append("Connection closed.\n");
        setLoggedOut();
    }

    public void error(String message) {
        notifyError(message + "\n");
    }

    public void loggedIn() {
        setLoggedIn();
        append("You have joined the chat session.\n");
    }

    public void loggedOut() {
        append("You have left the chat session.\n");
        setLoggedOut();
    }

    public void messageReceived(String message) {
        append(message + "\n");
    }

    public static void main(String[] args) {
        SwingChatClient client = new SwingChatClient();
        client.pack();
        client.setVisible(true);
    }
}
