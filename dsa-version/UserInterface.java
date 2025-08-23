import java.util.Scanner;
import java.util.*;

public class UserInterface {
    private ParkingLot parkingLot;
    private Scanner scanner;
    private static final String ANSI_BOLD = "\033[1m";
    private static final String ANSI_RESET = "\033[0m";
    private static final String ANSI_CYAN = "\033[36m";
    private static final String ANSI_GREEN = "\033[32m";
    private static final String ANSI_YELLOW = "\033[33m";
    private static final String ANSI_PURPLE = "\033[35m";
    private static final String ANSI_BLUE = "\033[34m";

    public UserInterface(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.scanner = new Scanner(System.in);
    }

    public void showUserMenu() {
        while (true) {
            System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n===== 👤 User Interface 👤 =====" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "1. 🔍 Find Available Parking Slots");
            System.out.println("2. 🚗 Locate My Vehicle");
            System.out.println("3. 📝 View My Parking History");
            System.out.println("4. ⏳ Check Queue Status");
            System.out.println("5. 🚪 Back to Main Menu" + ANSI_RESET);
            System.out.print(ANSI_YELLOW + "Enter your choice: " + ANSI_RESET);

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    findAvailableSlots();
                    break;
                case 2:
                    locateVehicle();
                    break;
                case 3:
                    viewParkingHistory();
                    break;
                case 4:
                    checkQueueStatus();
                    break;
                case 5:
                    return;
                default:
                    System.out.println(ANSI_YELLOW + "❌ Invalid choice! Please try again." + ANSI_RESET);
            }
        }
    }

    private void findAvailableSlots() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Available Parking Slots ===" + ANSI_RESET);
        System.out.print(ANSI_BLUE + "Enter vehicle type (2W/4W): " + ANSI_RESET);
        String vehicleType = scanner.nextLine();

        if (!vehicleType.equals("2W") && !vehicleType.equals("4W")) {
            System.out.println(ANSI_YELLOW + "❌ Invalid vehicle type! Please enter either 2W or 4W." + ANSI_RESET);
            return;
        }

        int availableSlots = vehicleType.equals("2W") ? 
            parkingLot.getAvailableTwoWheelerSlots() : 
            parkingLot.getAvailableFourWheelerSlots();

        if (availableSlots > 0) {
            System.out.println(ANSI_GREEN + "✅ Available " + vehicleType + " slots: " + availableSlots + 
                (vehicleType.equals("2W") ? " 🏍️" : " 🚗") + ANSI_RESET);
            
            // Show suggested parking areas
            System.out.println(ANSI_CYAN + "\nSuggested parking areas:");
            System.out.println("1. Near Entrance (Quick Access)");
            System.out.println("2. Covered Parking (Weather Protection)");
            System.out.println("3. Electric Vehicle Charging Area" + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "⚠️ No available slots. Current queue size: " + 
                parkingLot.getWaitlist().size() + ANSI_RESET);
        }
    }

    private void locateVehicle() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Locate Vehicle ===" + ANSI_RESET);
        System.out.print(ANSI_BLUE + "Enter vehicle number: " + ANSI_RESET);
        String vehicleNumber = scanner.nextLine();

        Vehicle vehicle = parkingLot.searchByVehicleNumber(vehicleNumber);
        if (vehicle != null) {
            System.out.println(ANSI_GREEN + "✅ Vehicle found!");
            System.out.println("Entry Time: " + vehicle.getEntryTime());
            System.out.println("Current Parking Duration: " + vehicle.getParkingDuration() + " minutes" + ANSI_RESET);
            
            String parkingSlot = vehicle.getParkingSlot();
            if (parkingSlot != null) {
                System.out.println(ANSI_GREEN + "Location: " + parkingSlot + ANSI_RESET);
                
                // Show directions to vehicle
                System.out.println(ANSI_CYAN + "\nDirections to your vehicle:");
                System.out.println("1. Enter through main gate");
                System.out.println("2. Turn " + (parkingSlot.startsWith("A") ? "left" : "right"));
                System.out.println("3. Look for section " + parkingSlot.substring(0, 1));
                System.out.println("4. Your vehicle is in slot " + parkingSlot + ANSI_RESET);
            } else {
                System.out.println(ANSI_YELLOW + "\n⚠️ Your vehicle is in the waitlist. Please wait for a slot to become available." + ANSI_RESET);
                
                // Calculate queue position
                int position = 0;
                for (Vehicle v : parkingLot.getWaitlist()) {
                    position++;
                    if (v.getVehicleNumber().equals(vehicleNumber)) {
                        break;
                    }
                }
                if (position > 0) {
                    System.out.println(ANSI_CYAN + "Current queue position: " + position + ANSI_RESET);
                }
            }
        } else {
            System.out.println(ANSI_YELLOW + "❌ Vehicle not found in the parking lot!" + ANSI_RESET);
        }
    }

    private void viewParkingHistory() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Parking History ===" + ANSI_RESET);
        System.out.print(ANSI_BLUE + "Enter vehicle number: " + ANSI_RESET);
        String vehicleNumber = scanner.nextLine();

        List<Transaction> transactions = parkingLot.getTransactionHistory()
            .stream()
            .filter(t -> t.getVehicleNumber().equals(vehicleNumber))
            .toList();

        if (transactions.isEmpty()) {
            System.out.println(ANSI_YELLOW + "❌ No parking history found for this vehicle!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_GREEN + "📋 Parking History for " + vehicleNumber + ":");
            transactions.forEach(t -> {
                System.out.println("\nTransaction ID: " + t.getTransactionId());
                System.out.println("Entry Time: " + t.getEntryTime());
                System.out.println("Exit Time: " + t.getExitTime());
                System.out.println("Duration: " + t.getDuration() + " minutes");
                System.out.println("Charges: Rs. " + t.getCharges());
                System.out.println("Payment Status: " + t.getPaymentStatus());
            });
        }
    }

    private void checkQueueStatus() {
        System.out.println(ANSI_BOLD + ANSI_PURPLE + "\n=== Queue Status ===" + ANSI_RESET);
        Queue<Vehicle> waitlist = parkingLot.getWaitlist();
        int queueSize = waitlist.size();

        if (queueSize == 0) {
            System.out.println(ANSI_GREEN + "✅ No vehicles in the queue!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_YELLOW + "⏳ Current queue size: " + queueSize);
            System.out.println("Estimated wait time: " + (queueSize * 5) + " minutes" + ANSI_RESET);
            
            System.out.println(ANSI_CYAN + "\nVehicles in queue:");
            int position = 1;
            for (Vehicle vehicle : waitlist) {
                System.out.println(position + ". " + vehicle.getVehicleNumber() + 
                    (vehicle.getVehicleType().equals("2W") ? " 🏍️" : " 🚗"));
                position++;
            }
        }
    }
} 