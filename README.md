# Vehicle Parking Management System 🚗

A comprehensive parking management system that handles both user and admin interfaces for efficient parking management.

## Features 🌟

### User Features 👤
1. **Find Available Parking Slots**
   - Check real-time availability for 2-wheelers and 4-wheelers
   - View suggested parking areas
   - Get queue status if no slots are available

2. **Locate Vehicle**
   - Find your parked vehicle easily
   - Get detailed directions to your parking spot
   - View current parking duration

3. **View Parking History**
   - Access complete parking history
   - View transaction details
   - Check payment status

4. **Queue Status**
   - Check current queue size
   - View estimated wait time
   - See vehicles ahead in queue

### Admin Features 👨‍💼
1. **Park Vehicle**
   - Add new vehicles to the parking lot
   - Automatic waitlist management
   - Support for 2-wheelers and 4-wheelers

2. **Exit Vehicle**
   - Process vehicle exits
   - Calculate parking charges
   - Generate transaction records

3. **Search Functionality**
   - Search by vehicle number
   - Search by owner name
   - View detailed vehicle information

4. **View Reports**
   - Available slots
   - Current waitlist
   - Transaction history
   - Sorted vehicle lists

## Technical Details 💻

### System Requirements
- Java Runtime Environment (JRE) 8 or higher
- Terminal with ANSI color support

### Parking Rates
- 2-Wheeler: ₹20 per hour
- 4-Wheeler: ₹40 per hour
- Minimum charge: 1 hour

### Data Structures Used
- Lists for parking slots and transactions
- Maps for vehicle and owner tracking
- Queue for waitlist management

## How to Use 🚀

1. **Compile the Program**
   ```bash
   javac *.java
   ```

2. **Run the Program**
   ```bash
   java Main
   ```

3. **Initialize Parking Lot**
   - Enter number of 2-wheeler slots
   - Enter number of 4-wheeler slots

4. **Select Role**
   - User Interface
   - Admin Interface

### User Interface Options
1. Find Available Parking Slots
2. Locate My Vehicle
3. View My Parking History
4. Check Queue Status
5. Back to Main Menu

### Admin Interface Options
1. Park a Vehicle
2. Exit a Vehicle
3. Search Vehicle
4. View Available Slots
5. View Waitlist
6. View Transaction History
7. View Sorted Vehicles
8. Exit

## Error Handling ⚠️

The system includes comprehensive error handling for:
- Invalid inputs
- Null values
- Empty strings
- Negative slot numbers
- Duplicate vehicle entries
- Missing vehicles
- Invalid vehicle types

## Security Features 🔒

- Input validation
- Thread-safe collections
- Protected data structures
- Transaction integrity

## Future Enhancements 🎯

1. **Planned Features**
   - Online booking system
   - Mobile app integration
   - Payment gateway integration
   - Real-time notifications
   - Automated parking guidance

2. **Technical Improvements**
   - Database integration
   - Web interface
   - API development
   - Cloud deployment

## Contributing 🤝

Feel free to contribute to this project by:
1. Forking the repository
2. Creating a feature branch
3. Submitting a pull request

## License 📄

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact 📧

For any queries or suggestions, please open an issue in the repository. 