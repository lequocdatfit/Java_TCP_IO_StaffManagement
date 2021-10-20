package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientFrm extends JFrame {
    private JTextField txtID;
    private JTextField txtName;
    private JTextField txtBirthDay;
    private JTextField txtStartDate;
    private JTextField txtSalary;
    private JTextField txtPosition;
    private JTable table1;
    private JButton btnAdd;
    private JButton btnDelete;
    private JScrollPane tblStaff;
    private JPanel mainPanel;
    private final int PORT = 3000;
    private ObjectOutputStream out;
    private Socket s;
    private DefaultTableModel model;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayList<Staff> list;

    public ClientFrm(String title) {
        super(title);
        setLocationRelativeTo(null);
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        list = new ArrayList<>();

        connectToServer();
        getAllUsers();
        model = (DefaultTableModel) table1.getModel();
        model.setColumnIdentifiers(new Object[] {
                "ID", "Name", "Birth day", "Start date", "Salary", "Position"
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String ID = txtID.getText();
                String fullName = txtName.getText();
                Date birthDay = null;
                Date startDay = null;
                double salary = 0;
                String position = txtPosition.getText();
                boolean isOk = true;
                try {
                    salary = Double.parseDouble(txtSalary.getText());
                    birthDay = format.parse(txtBirthDay.getText());
                    startDay = format.parse(txtStartDate.getText());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    isOk = false;
                }
                if(isOk) {
                    Staff s = new Staff(ID, fullName, birthDay, startDay, salary, position);
                    addStaff(s);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = table1.getSelectedRow();
                if(index != -1) {
                    Staff e = list.get(index);
                    deleteStaff(e.getID());
                } else {
                    JOptionPane.showMessageDialog(ClientFrm.this, "Hay chon 1 staff!");
                }
            }
        });

    }

    private void getAllUsers() {
        Message msg2 = new Message("GET_ALL_USERS");
        msg2.setPayload("lequocdat---123456---getAll");
        try {
            out.writeObject(msg2);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateStaffTable(ArrayList<Staff> newList) {
        list = newList;
        model.setRowCount(0);
        for(Staff s : list) {
            model.addRow(new Object[] {
                    s.getID(), s.getFullName(), s.getBirthDay(), s.getStartDate(), s.getSalary(), s.getPosition()
            });
        }
    }

    public void getStaffInfo(String staffId) {
        Message msg = new Message("GET_STAFF");
        msg.setPayload("lequocdat---123456---detail---" + staffId);
        ClientWriteThread writeThread = new ClientWriteThread(this, s, msg, out);
        Thread t = new Thread(writeThread);
        t.start();
    }

    public void addStaff(Staff e) {
        Message msg = new Message("ADD_STAFF");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        msg.setPayload("lequocdat---123456---add---" + e.getID() + "---" + e.getFullName() + "---" +
                format.format(e.getBirthDay()) + "---" + format.format(e.getStartDate()) + "---" + e.getSalary()
                + "---" + e.getPosition());
        ClientWriteThread writeThread = new ClientWriteThread(this, s, msg, out);
        Thread t = new Thread(writeThread);
        t.start();
    }

    public void deleteStaff(String staffId) {
        Message msg = new Message("DELETE_STAFF");
        msg.setPayload("lequocdat---123456---delete---" + staffId);
        ClientWriteThread writeThread = new ClientWriteThread(this, s, msg, out);
        Thread t = new Thread(writeThread);
        t.start();
    }

    public void connectToServer() {
        try {
            s = new Socket("localhost", PORT);
            out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
            ClientReadThread readThread = new ClientReadThread(this, s);
            Thread t1 = new Thread(readThread);
            t1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientFrm frm = new ClientFrm("Client");
        frm.setVisible(true);
    }
}