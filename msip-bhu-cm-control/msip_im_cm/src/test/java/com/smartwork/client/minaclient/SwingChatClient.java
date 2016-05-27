package com.smartwork.client.minaclient;



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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.mina.core.service.IoHandlerAdapter;

import com.smartwork.client.minaclient.SwingChatClientHandler.Callback;



/**
 * Simple chat client based on Swing & MINA that implements the chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SwingChatClient extends JFrame implements Callback {
    private static final long serialVersionUID = 1538675161745436968L;

    private JTextField inputText;

    private JButton loginButton;

    private JButton quitButton;

    private JButton closeButton;

    private JTextField serverField;

    private JTextField nameField;

    private JTextArea area;

    private JScrollBar scroll;

    private ChatClientSupport client;
    
    private IoHandlerAdapter handler;
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
        area = new JTextArea(10, 50);
        area.setLineWrap(true);
        area.setEditable(false);
        scroll = new JScrollBar();
        scroll.add(area);
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
        left.add(area);
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

            handler = new SwingChatClientHandler(SwingChatClient.this);
            client = new ChatClientSupport(name, handler, SwingChatClient.this);
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
        }
    }

    private class BroadcastAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = -6276019615521905411L;

        public void actionPerformed(ActionEvent e) {
            client.broadcast(inputText.getText());
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

	@Override
	public void append(String message) {
		area.append(message);
	}
}
