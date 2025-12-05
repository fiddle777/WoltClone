package com.example.woltbetblogiau.consoleCRUD;

import com.example.woltbetblogiau.model.Driver;
import com.example.woltbetblogiau.model.Review;
import com.example.woltbetblogiau.model.User;
import com.example.woltbetblogiau.model.VehicleType;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.time.LocalDate;
import java.util.Scanner;

public class MenuControl {
    public static void generateUserMenu(Scanner scanner, Wolt wolt) {
        var cmd = 0;

        while (cmd != 6) {
            System.out.println("""
                    Choose and option:
                    1 - create
                    2 - view all users
                    3 - view user
                    4 - update
                    5 - delete user
                    6 - return to main menu
                    """);
            cmd = scanner.nextInt();
            scanner.nextLine();

            switch (cmd) {
                case 1:
                    System.out.println("Which type of user to create? U/BS/D/R");
                    var userType = scanner.nextLine();
                    if(userType.equals("U")){
                        System.out.println("Enter User data (User class):username;password;name;surname;phoneNum");
                        var input = scanner.nextLine();
                        String[] info = input.split(";");
                        User user = new User(info[0], info[1], info[2], info[3], info[4]);
                        wolt.getAllSystemUsers().add(user);
                    } else if (userType.equals("D")) {
                        System.out.println("Enter User data (User class):username;password;name;surname;phoneNum;address; licence; bdate;vehicle");
                        var input = scanner.nextLine();
                        String[] info = input.split(";");
                        Driver driver = new Driver(info[0], info[1], info[2], info[3], info[4], info[5], info[6], LocalDate.parse(info[7]), VehicleType.valueOf(info[8]));
                    }
                    break;
                case 2:
                    for (User u : wolt.getAllSystemUsers()) {
                        System.out.println(u);
                    }
                    break;
                case 3:
                    System.out.println("enter username");
                    var usernameForPrint = scanner.nextLine();
                    for (User u : wolt.getAllSystemUsers()) {
                        if (u.getLogin().equals(usernameForPrint)){
                            System.out.println(u);
                        }
                    }
                    break;
                case 4:
                    System.out.println("enter username");
                    var username = scanner.nextLine();
                    for (User u : wolt.getAllSystemUsers()) {
                        if (u.getLogin().equals(username)){
                            System.out.println("What to update? name;surname;");
                            String[] info = scanner.nextLine().split(";");
                            u.setName(info[0]);
                            u.setSurname(info[1]);
                        }
                    }
                    break;
                case 5:
                    break;
                case 6:
                    break;
                default:
                    System.out.println();
            }
        }
    }
}
