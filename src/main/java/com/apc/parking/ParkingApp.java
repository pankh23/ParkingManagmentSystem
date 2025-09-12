package com.apc.parking;

import com.apc.parking.model.User;
import com.apc.parking.model.Vehicle;
import com.apc.parking.service.ParkingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class ParkingApp {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ParkingService service = context.getBean(ParkingService.class);

        Scanner sc = new Scanner(System.in);

        System.out.println("Login as (1) Admin (2) User:");
        int role = sc.nextInt();
        sc.nextLine(); // consume newline

        if (role == 1) {
            // Admin menu
            System.out.println("Enter total 2W slots:");
            int twoW = sc.nextInt();
            System.out.println("Enter total 4W slots:");
            int fourW = sc.nextInt();
            service.initializeSlots(twoW, fourW);

            while (true) {
                System.out.println("\nAdmin Options:");
                System.out.println("1. Park Vehicle");
                System.out.println("2. Exit Vehicle");
                System.out.println("3. View Wait Queue");
                System.out.println("4. View Transaction History");
                System.out.println("5. Exit");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        Vehicle v = new Vehicle();
                        System.out.println("Enter vehicle ID:");
                        v.setId(sc.nextLong());
                        sc.nextLine();
                        System.out.println("Enter type (2W/4W):");
                        v.setType(sc.nextLine());
                        v.setParked(false);
                        System.out.println(service.parkVehicle(v));
                    }
                    case 2 -> {
                        System.out.println("Enter vehicle ID to exit:");
                        long vid = sc.nextLong();
                        sc.nextLine();
                        System.out.println(service.exitVehicle(vid));
                    }
                    case 3 -> {
                        System.out.println("Wait Queue for 2W: " + service.getWaitQueue("2W").size());
                        System.out.println("Wait Queue for 4W: " + service.getWaitQueue("4W").size());
                    }
                    case 4 -> {
                        System.out.println("Transaction History:");
                        service.getTransactionHistory().forEach(tx -> {
                            System.out.println("Vehicle: " + tx.getVehicle().getId() +
                                    " Slot: " + tx.getSlotId() +
                                    " Action: " + tx.getAction());
                        });
                    }
                    case 5 -> System.exit(0);
                    default -> System.out.println("Invalid option");
                }
            }
        } else {
            // User menu
            while (true) {
                System.out.println("\nUser Options:");
                System.out.println("1. Search Vehicle");
                System.out.println("2. Search Available Slots");
                System.out.println("3. View My Transactions");
                System.out.println("4. Exit");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.println("Enter vehicle ID:");
                        long vid = sc.nextLong();
                        sc.nextLine();
                        System.out.println(service.searchVehicle(vid));
                    }
                    case 2 -> {
                        System.out.println("Enter slot type (2W/4W):");
                        String type = sc.nextLine();
                        System.out.println("Available slots: " + service.searchAvailableSlots(type).size());
                    }
                    case 3 -> {
                        System.out.println("Enter your user ID:");
                        long uid = sc.nextLong();
                        sc.nextLine();
                        service.getUserTransactionHistory(uid).forEach(tx -> {
                            System.out.println("Vehicle: " + tx.getVehicle().getId() +
                                    " Slot: " + tx.getSlotId() +
                                    " Action: " + tx.getAction());
                        });
                    }
                    case 4 -> System.exit(0);
                    default -> System.out.println("Invalid option");
                }
            }
        }
    }
}