package com.company;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWriteThread implements Runnable{
    private ObjectOutputStream out;
    private Socket s;
    private ClientFrm frm;
    private Message msg;

    public ClientWriteThread(ClientFrm frm, Socket s, Message msg, ObjectOutputStream out) {
        this.frm = frm;
        this.msg = msg;
        this.s = s;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
