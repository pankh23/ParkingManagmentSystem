import java.util.Scanner;
import java.util.*;

public class Main {
    private static ParkingLot parkingLot;
    private static Scanner scanner = new Scanner(System.in);
    private static final String ANSI_BOLD = "\033[1m";
    private static final String ANSI_RESET = "\033[0m";
    private static final String ANSI_CYAN = "\033[36m";
    private static final String ANSI_GREEN = "\033[32m";
    private static final String ANSI_YELLOW = "\033[33m";
    private static final String ANSI_PURPLE = "\033[35m";
    private static final String ANSI_BLUE = "\033[34m";

    public static void main(String[] args) {
        System.out.println(ANSI_BOLD + ANSI_CYAN + "\n🚗 🏍️  Welcome to Vehicle Parking Management System! 🏍️ 🚗" + ANSI_RESET);
        initializeParkingLot();
        showMenu();
    }

    private static void initializeParkingLot() {
        System.out.println(ANSI_BOLD + ANSI_YELLOW + "\n=== Parking Lot Initialization ===" + ANSI_RESET);
        System.out.print(ANSI_BLUE + "Enter number of 2-wheeler slots 🏍️ : " + ANSI_RESET);
        int twoWheelerSlots = scanner.nextInt();
        System.out.print(ANSI_BLUE + "Enter number of 4-wheeler slots 🚗 : " + ANSI_RESET);
        int fourWheelerSlots = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        parkingLot = new ParkingLot(twoWheelerSlots, fourWheelerSlots);
        System.out.println(ANSI_GREEN + "✅ Parking lot initialized successfully!" + ANSI_RESET);
    }

    private static void showMenu() {
        while (true) {
            System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n===== 🅿️  Parking Management System 🅿️  =====" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "1. 🚗 Park a Vehicle");
            System.out.println("2. 🚪 Exit a Vehicle");
            System.out.println("3. 🔍 Search Vehicle");
            System.out.println("4. 📊 View Available Slots");
            System.out.println("5. ⏳ View Waitlist");
            System.out.println("6. 📝 View Transaction History");
            System.out.println("7. 📋 View Sorted Vehicles");
            System.out.println("8. 🚪 Exit" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Enter your choice: " + ANSI_RESET);

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    parkVehicle();
                    break;
                case 2:
                    exitVehicle();
                    break;
                case 3:
                    searchVehicle();
                    break;
                case 4:
                    viewAvailableSlots();
                    break;
                case 5:
                    viewWaitlist();
                    break;
                case 6:
                    viewTransactionHistory();
                    break;
                case 7:
                    viewSortedVehicles();
                    break;
                case 8:
                    System.out.println(ANSI_GREEN + "\nThank you for using the Parking Management System! 👋" + ANSI_RESET);
                    return;
                default:
                    System.out.println(ANSI_YELLOW + "❌ Invalid choice! Please try again." + ANSI_RESET);
            }
        }
    }

    private static void parkVehicle() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Park Vehicle ===" + ANSI_RESET);
        System.out.print(ANSI_BLUE + "Enter vehicle number: " + ANSI_RESET);
        String vehicleNumber = scanner.nextLine();
        System.out.print(ANSI_BLUE + "Enter owner name: " + ANSI_RESET);
        String ownerName = scanner.nextLine();
        System.out.print(ANSI_BLUE + "Enter vehicle type (2W/4W): " + ANSI_RESET);
        String vehicleType = scanner.nextLine();

        if (!vehicleType.equals("2W") && !vehicleType.equals("4W")) {
            System.out.println(ANSI_YELLOW + "❌ Invalid vehicle type! Please enter either 2W or 4W." + ANSI_RESET);
            return;
        }

        Vehicle vehicle = new Vehicle(vehicleNumber, ownerName, vehicleType);
        if (parkingLot.parkVehicle(vehicle)) {
            System.out.println(ANSI_GREEN + "✅ Vehicle parked successfully! " + (vehicleType.equals("2W") ? "🏍️" : "🚗") + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "⚠️ Vehicle added to waitlist as no slots are available." + ANSI_RESET);
        }
    }

    private static void exitVehicle() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Exit Vehicle ===" + ANSI_RESET);
        System.out.print(ANSI_BLUE + "Enter vehicle number: " + ANSI_RESET);
        String vehicleNumber = scanner.nextLine();

        Vehicle vehicle = parkingLot.exitVehicle(vehicleNumber);
        if (vehicle != null) {
            System.out.println(ANSI_GREEN + "✅ Vehicle exited successfully!");
            System.out.println("💰 Parking charges: Rs. " + vehicle.getParkingCharges() + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "❌ Vehicle not found!" + ANSI_RESET);
        }
    }

    private static void searchVehicle() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Search Vehicle ===" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Search by:");
        System.out.println("1. 🔢 Vehicle Number");
        System.out.println("2. 👤 Owner Name" + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "Enter your choice: " + ANSI_RESET);
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.print(ANSI_BLUE + "Enter vehicle number: " + ANSI_RESET);
            String vehicleNumber = scanner.nextLine();
            Vehicle vehicle = parkingLot.searchByVehicleNumber(vehicleNumber);
            if (vehicle != null) {
                System.out.println(ANSI_GREEN + "✅ Vehicle found:");
                System.out.println(vehicle + ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW + "❌ Vehicle not found!" + ANSI_RESET);
            }
        } else if (choice == 2) {
            System.out.print(ANSI_BLUE + "Enter owner name: " + ANSI_RESET);
            String ownerName = scanner.nextLine();
            var vehicles = parkingLot.searchByOwnerName(ownerName);
            if (!vehicles.isEmpty()) {
                System.out.println(ANSI_GREEN + "✅ Vehicles found:");
                vehicles.forEach(System.out::println);
            } else {
                System.out.println(ANSI_YELLOW + "❌ No vehicles found for this owner!" + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_YELLOW + "❌ Invalid choice!" + ANSI_RESET);
        }
    }

    private static void viewAvailableSlots() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Available Slots ===" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Available 2-wheeler slots 🏍️ : " + parkingLot.getAvailableTwoWheelerSlots());
        System.out.println("Available 4-wheeler slots 🚗 : " + parkingLot.getAvailableFourWheelerSlots() + ANSI_RESET);
    }

    private static void viewWaitlist() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Waitlist ===" + ANSI_RESET);
        Queue<Vehicle> waitlist = parkingLot.getWaitlist();
        if (waitlist.isEmpty()) {
            System.out.println(ANSI_GREEN + "✅ No vehicles in waitlist." + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "⏳ Vehicles in waitlist:");
            waitlist.forEach(vehicle -> System.out.println(ANSI_CYAN + "• " + vehicle.getVehicleNumber() + 
                (vehicle.getVehicleType().equals("2W") ? " 🏍️" : " 🚗") + ANSI_RESET));
        }
    }

    private static void viewTransactionHistory() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Transaction History ===" + ANSI_RESET);
        var transactions = parkingLot.getTransactionHistory();
        if (transactions.isEmpty()) {
            System.out.println(ANSI_YELLOW + "📝 No transactions found." + ANSI_RESET);
        } else {
            System.out.println(ANSI_CYAN + "📋 Transaction History:");
            transactions.forEach(t -> System.out.println(ANSI_GREEN + "• " + t + ANSI_RESET));
        }
    }

    private static void viewSortedVehicles() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Sort Vehicles ===" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Sort by:");
        System.out.println("1. ⏰ Entry Time");
        System.out.println("2. 💰 Parking Charges" + ANSI_RESET);
        System.out.print(ANSI_YELLOW + "Enter your choice: " + ANSI_RESET);
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            var vehicles = parkingLot.getSortedVehiclesByTime();
            if (vehicles.isEmpty()) {
                System.out.println(ANSI_YELLOW + "❌ No vehicles found." + ANSI_RESET);
            } else {
                System.out.println(ANSI_GREEN + "📋 Vehicles sorted by entry time:");
                vehicles.forEach(v -> System.out.println("• " + v + 
                    (v.getVehicleType().equals("2W") ? " 🏍️" : " 🚗")));
            }
        } else if (choice == 2) {
            var vehicles = parkingLot.getSortedVehiclesByCharges();
            if (vehicles.isEmpty()) {
                System.out.println(ANSI_YELLOW + "❌ No vehicles found." + ANSI_RESET);
            } else {
                System.out.println(ANSI_GREEN + "📋 Vehicles sorted by parking charges:");
                vehicles.forEach(v -> System.out.println("• " + v + 
                    (v.getVehicleType().equals("2W") ? " 🏍️" : " 🚗")));
            }
        } else {
            System.out.println(ANSI_YELLOW + "❌ Invalid choice!" + ANSI_RESET);
        }
    }
} 