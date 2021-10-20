package com.company;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientReadThread implements Runnable{
    private ObjectInputStream in;
    private Socket s;
    private ClientFrm frm;
    public ClientReadThread(ClientFrm frm, Socket s) {
        this.frm = frm;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
            Message msg = null;
            while (true) {
                msg = (Message) in.readObject();
                switch (msg.getType()) {
                    case "RETURN_NUMBERS_USER":
                        System.out.println("Count: " + msg.getPayload());
                        break;
                    case "RETURN_ALL_STAFF":
                        System.out.println("List staff return!");
                        frm.updateStaffTable((ArrayList<Staff>) msg.getPayload());
                        break;
                    case "RETURN_STAFF":
                        System.out.println("Staff" + msg.getPayload());
                        break;
                    case "DELETE_STAFF_SUCCESS":
                        System.out.println("DELETED");
                        break;
                    case "ADD_STAFF_SUCCESS":
                        System.out.println("ADDED" + msg.getPayload());
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
