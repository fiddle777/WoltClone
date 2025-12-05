package com.example.woltbetblogiau.consoleCRUD;

import java.util.Scanner;

public class StartNoGui {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        System.out.print("Choose an option:\n"+
//                "u - work with users\n"+
//                "o - work with orders\n");
        Wolt wolt = Utils.readWoltFromFile();
        if(wolt==null){
            wolt = new Wolt();
        }
        var cmd = "";
        while (!cmd.equals("q")) {
            System.out.println("""
                    Choose and option:
                    u - work with users
                    o - with orders
                    w - write to file
                    q -quit
                    """);

            cmd = scanner.nextLine();

            switch (cmd) {
                case "u":
                    MenuControl.generateUserMenu(scanner, wolt);
                    break;
                case "o":
                    break;
                case "w":
                    break;
                case "q":
                    Utils.writeWoltToFile(wolt);
                    System.out.println("YOU HAVE BEEN TERMINATED.");
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }
}
