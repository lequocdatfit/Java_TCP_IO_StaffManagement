package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int PORT = 3000;
    private ServerSocket ss;
    private ArrayList<SocketServerThread> listSocketThreads;
    private static ExecutorService pool = Executors.newFixedThreadPool(100);
    private ArrayList<Staff> listStaff;
    private ArrayList<User> listUser;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    public Server() {
        listUser = new ArrayList<>();
        listStaff = new ArrayList<>();
        listSocketThreads = new ArrayList<>();

        startServer();
        readUserFile();
        readStaffFile();
    }

    public ArrayList<User> getListUser() {
        return listUser;
    }

    public ArrayList<Staff> getListEmployee() {
        return listStaff;
    }

    public void readStaffFile() {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream("STAFF.DAT")));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] employee = line.split("-");
                String ID = employee[0];
                String fullName = employee[1];
                Date birthDay = format.parse(employee[2]);
                Date startDate = format.parse(employee[3]);
                double salary = Double.parseDouble(employee[4]);
                String position = employee[5];
                Staff e = new Staff(ID, fullName, birthDay, startDate, salary, position);
                listStaff.add(e);
            }
            br.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void readUserFile() {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream("USER.DAT")));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] user = line.split(" ");
                User u = new User(user[0], user[1]);
                listUser.add(u);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEmployeeToFile(Staff em) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("STAFF.DAT", true)));
            bw.write(em.getID() + "-" + em.getFullName() + "-" + format.format(em.getBirthDay()) + "-" + format.format(em.getStartDate()) +
                    "-" + em.getSalary() + "-" + em.getPosition());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ss = new ServerSocket(PORT);
                    System.out.println("Server waiting on port: "+ PORT);
                    while (true) {
                        Socket s = ss.accept();
                        SocketServerThread socketThread = new SocketServerThread(Server.this, s, listSocketThreads);
                        listSocketThreads.add(socketThread);
                        pool.execute(socketThread);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    public static void main(String[] args) {
        Server s = new Server();
    }


    public void createNewEmployee(Staff em) {
        listStaff.add(em);
        addEmployeeToFile(em);
    };

    public boolean deleteEmployee(String id) {
        for (Staff e : listStaff) {
            if(e.getID().equals(id)) {
                listStaff.remove(e);
                reWriteToFile(listStaff);
                return true;
            }
        }
        return false;
    }

    public void reWriteToFile(ArrayList<Staff> listStaff) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("STAFF.DAT", false)));
            for (Staff em : listStaff) {
                bw.write(em.getID() + "-" + em.getFullName() + "-" + format.format(em.getBirthDay()) + "-" + format.format(em.getStartDate()) +
                        "-" + em.getSalary() + "-" + em.getPosition());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
