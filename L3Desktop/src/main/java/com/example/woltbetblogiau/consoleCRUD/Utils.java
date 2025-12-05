package com.example.woltbetblogiau.consoleCRUD;

import com.example.woltbetblogiau.model.User;

import java.io.*;

public class Utils {
    public static void writeUserToFile(User user) {
        ObjectOutputStream out = null;
        try(var file = new FileOutputStream("o.txt")) {
            out = new ObjectOutputStream(new BufferedOutputStream(file));
            out.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
//       ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(newFileInputStream("o.txt")));
//        Object o2 = in.readObject();
//        in.close();
    }

    public static void writeWoltToFile(Wolt wolt){
        ObjectOutputStream out = null;
        try{
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("wolt.txt")));
            out.writeObject(wolt);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Wolt readWoltFromFile(){
        ObjectInputStream in = null;
        Wolt wolt = null;
        try{
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("wolt.txt")));
            wolt = (Wolt) in.readObject();
        }catch (IOException|ClassNotFoundException e){
            e.printStackTrace();
        }
        return wolt;
    }
}
