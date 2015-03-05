/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package com.smartwork.client.minaclient;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 *
 */
public class ChatDialog extends JDialog {
    private static final long serialVersionUID = 2009384520250666216L;

    private String content;

    private String to;

    private boolean cancelled = false;

    public ChatDialog(Frame owner) throws HeadlessException {
        super(owner, "Chat", true);

        content = "测试聊天";

        final JTextField toField = new JTextField();
        final JTextField contentField = new JTextField(content);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(new JLabel("To"));
        contentPanel.add(toField);
        contentPanel.add(new JLabel("Content"));
        contentPanel.add(contentField);

        JButton okButton = new JButton();
        okButton.setAction(new AbstractAction("OK") {
            private static final long serialVersionUID = -2292183622613960604L;

            public void actionPerformed(ActionEvent e) {
                to = toField.getText();
                content = contentField.getText();
                ChatDialog.this.dispose();
            }
        });

        JButton cancelButton = new JButton();
        cancelButton.setAction(new AbstractAction("Cancel") {
            private static final long serialVersionUID = 6122393546173723305L;

            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                ChatDialog.this.dispose();
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(okButton);
        buttons.add(cancelButton);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    public boolean isCancelled() {
        return cancelled;
    }

	public String getContent() {
		return content;
	}

	public String getTo() {
		return to;
	}

}
