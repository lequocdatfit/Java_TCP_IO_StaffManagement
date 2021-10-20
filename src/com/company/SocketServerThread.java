package com.company;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SocketServerThread implements Runnable {
    private Socket s;
    private ArrayList<SocketServerThread> listSocketThreads;
    private Server server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SocketServerThread(Server server, Socket s, ArrayList<SocketServerThread> socketList) {
        this.server = server;
        this.s = s;
        this.listSocketThreads = socketList;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
            out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));

            Message msg = null;
            while (true) {
                msg = (Message) in.readObject();
                String payload = (String) msg.getPayload();
                String[] syntax = payload.split("---");
                switch (msg.getType()) {
                    case "GET_ALL_USERS":
                        if(syntax[2].equals("getAll")) {
                            String username = syntax[0];
                            String password = syntax[1];
                            for (User u : server.getListUser()) {
                                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                                    Message sendMsg = new Message("RETURN_ALL_STAFF", server.getListEmployee());
                                    out.writeObject(sendMsg);
                                    out.flush();
                                } else {
                                    Message sendMsg = new Message("UNAUTHORIZED");
                                    out.writeObject(sendMsg);
                                    out.flush();
                                }
                            }
                        }
                        break;
                    case "GET_NUMBERS_STAFF":
                        if (syntax[2].equals("count")) {
                            String username = syntax[0];
                            String password = syntax[1];
                            String count = syntax[2];
                            for (User u : server.getListUser()) {
                                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                                    Message sendMsg = new Message("RETURN_NUMBERS_USER", server.getListEmployee().size());
                                    out.writeObject(sendMsg);
                                    out.flush();
                                } else {
                                    Message sendMsg = new Message("UNAUTHORIZED");
                                    out.writeObject(sendMsg);
                                    out.flush();
                                }
                            }
                        }
                        break;
                    case "GET_STAFF":
                        if (syntax[2].equals("detail")) {
                            String username = syntax[0];
                            String password = syntax[1];
                            String detail = syntax[2];
                            String staffid = syntax[3];
                            for (User u : server.getListUser()) {
                                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                                    Staff matchedStaff = null;
                                    for (Staff e : server.getListEmployee()) {
                                        if (e.getID().equals(staffid)) {
                                            matchedStaff = e;
                                            break;
                                        }
                                    }
                                    if (matchedStaff != null) {
                                        Message sendMsg = new Message("RETURN_STAFF", matchedStaff);
                                        out.writeObject(sendMsg);
                                        out.flush();
                                    } else {
                                        Message sendMsg = new Message("INVALID_STAFFID");
                                        out.writeObject(sendMsg);
                                        out.flush();
                                    }
                                } else {
                                    Message sendMsg = new Message("UNAUTHORIZED");
                                    out.writeObject(sendMsg);
                                    out.flush();
                                }
                            }
                        }
                        break;
                    case "ADD_STAFF":
                        if (syntax[2].equals("add")) {
                            String username = syntax[0];
                            String password = syntax[1];
                            String add = syntax[2];
                            String staffid = syntax[3];
                            String staffName = syntax[4];
                            Date dateofbirth = null;
                            Date startDate = null;
                            double salary = 0;
                            String position = syntax[8];
                            boolean isOk = true;
                            try {
                                 dateofbirth = new SimpleDateFormat("dd/MM/yyyy").parse(syntax[5]);
                                 startDate = new SimpleDateFormat("dd/MM/yyyy").parse(syntax[6]);
                                 salary = Double.parseDouble(syntax[7]);
                            } catch (ParseException exc) {
                                isOk = false;
                            }

                            for (User u : server.getListUser()) {
                                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                                    if(isOk) {
                                        Staff e = new Staff(staffid, staffName, dateofbirth, startDate, salary, position);
                                        server.createNewEmployee(e);
                                        Message sendmsg = new Message("ADD_STAFF_SUCCESS", e);
                                        out.writeObject(sendmsg);
                                        out.flush();
                                    } else {
                                        Message sendmsg = new Message("ERROR_ADD_STAFF");
                                        out.writeObject(sendmsg);
                                        out.flush();
                                    }

                                } else {
                                    Message sendMsg = new Message("UNAUTHORIZED");
                                    out.writeObject(sendMsg);
                                    out.flush();
                                }
                            }

                        }
                        break;
                    case "DELETE_STAFF":
                        if(syntax[2].equals("delete")) {
                            String username = syntax[0];
                            String password = syntax[1];
                            String staffid = syntax[3];
                            for (User u : server.getListUser()) {
                                if(u.getUsername().equals(username) && u.getPassword().equals(password)) {
                                    boolean status = server.deleteEmployee(staffid);
                                    if(status == true) {
                                        Message sendMsg = new Message("DELETE_STAFF_SUCCESS");
                                        out.writeObject(sendMsg);
                                        out.flush();
                                    } else {
                                        Message sendMsg = new Message("ERROR_DELETE_STAFF");
                                        out.writeObject(sendMsg);
                                        out.flush();
                                    }
                                } else {
                                    Message sendMsg = new Message("UNAUTHORIZED");
                                    out.writeObject(sendMsg);
                                    out.flush();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
