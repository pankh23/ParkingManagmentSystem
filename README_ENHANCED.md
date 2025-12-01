# Enhanced APC Parking Management System

## Overview

The Enhanced APC Parking Management System is a comprehensive, enterprise-grade parking management solution built with Java Spring Framework and Hibernate. This system has been scaled from a basic console application to a full-featured platform supporting multiple parking lots, reservations, payments, and real-time notifications.

## 🚀 Key Features

### Core Features
- **Multi-Lot Management**: Support for multiple parking lots with configurable slots
- **Reservation System**: Pre-booking with time-based slot allocation
- **Payment Integration**: Razorpay payment gateway integration
- **Real-time Notifications**: Email notifications for all events
- **Concurrency Handling**: Optimistic locking and thread-safe operations
- **Role-based Access**: Admin and User roles with different capabilities
- **Analytics Dashboard**: System monitoring and reporting
- **Auto-expiry**: Automatic cleanup of expired reservations

### Advanced Features
- **Wait Queue Management**: Intelligent queue system for oversubscribed lots
- **Transaction Logging**: Complete audit trail of all operations
- **Scheduled Tasks**: Automated maintenance and monitoring
- **Database Persistence**: PostgreSQL with Hibernate ORM
- **Spring Framework**: Full dependency injection and AOP support

## 🏗️ Architecture

### Technology Stack
- **Java 14**: Core programming language
- **Spring Framework 5.3.23**: Dependency injection, AOP, scheduling
- **Hibernate 5.6.15**: Object-relational mapping
- **PostgreSQL 42.7.3**: Database
- **Maven**: Build and dependency management
- **Razorpay**: Payment gateway integration
- **Spring Mail**: Email notifications

### Project Structure
```
src/main/java/com/apc/parking/
├── model/                    # Entity classes
│   ├── ParkingLot.java      # Parking lot entity
│   ├── Slot.java            # Parking slot entity (enhanced)
│   ├── Vehicle.java         # Vehicle entity
│   ├── User.java            # User entity
│   ├── Transaction.java     # Transaction entity
│   ├── WaitQueue.java       # Wait queue entity
│   ├── Reservation.java     # Reservation entity
│   └── Payment.java         # Payment entity
├── repository/              # Data access layer
│   ├── ParkingLotDao.java
│   ├── SlotDao.java
│   ├── VehicleDao.java
│   ├── UserDao.java
│   ├── TransactionDao.java
│   ├── WaitQueueDao.java
│   ├── ReservationDao.java
│   └── PaymentDao.java
├── service/                 # Business logic layer
│   ├── ParkingService.java  # Core parking operations
│   ├── ParkingLotService.java
│   ├── ReservationService.java
│   ├── PaymentService.java
│   ├── NotificationService.java
│   ├── ConcurrencyService.java
│   └── SchedulerService.java
├── ParkingApp.java          # Original console app
└── EnhancedParkingApp.java  # Enhanced console app
```

## 📊 Database Schema

### Core Tables
- **parking_lots**: Parking lot information
- **slots**: Individual parking slots
- **vehicles**: Vehicle information
- **users**: User accounts
- **transactions**: Parking activity log
- **wait_queue**: Queue management
- **reservations**: Booking information
- **payments**: Payment records

### Key Relationships
- ParkingLot → Slots (One-to-Many)
- Slot → Vehicle (One-to-One)
- User → Vehicles (One-to-Many)
- Reservation → Slot, User, Vehicle (Many-to-One)
- Payment → Reservation (One-to-One)

## 🔧 Installation & Setup

### Prerequisites
- Java 14 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ParkingManagmentSystem
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE parkingdb;
   CREATE USER parkinguser WITH PASSWORD 'parkingpass';
   GRANT ALL PRIVILEGES ON DATABASE parkingdb TO parkinguser;
   ```

3. **Configure Database**
   - Update `src/main/resources/application.properties`
   - Modify database connection details if needed

4. **Build and Run**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.apc.parking.EnhancedParkingApp"
   ```

## 🎯 Usage Guide

### Admin Workflow

#### 1. Manage Parking Lots
- **Add New Lot**: Create parking lots with configurable slots
- **Update Lot**: Modify pricing and details
- **Delete Lot**: Deactivate parking lots
- **View Status**: Monitor occupancy and availability

#### 2. Vehicle Management
- **Park Vehicle**: Assign vehicles to available slots
- **Exit Vehicle**: Process departures and free slots
- **Override Reservations**: Emergency slot management

#### 3. System Monitoring
- **View Analytics**: Occupancy rates, revenue, peak hours
- **Transaction History**: Complete audit trail
- **Wait Queue**: Monitor queue status
- **System Status**: Real-time system health

### User Workflow

#### 1. Reservation Management
- **Search & Book**: Find and book available slots
- **View Status**: Check reservation details
- **Cancel/Modify**: Manage existing reservations

#### 2. Vehicle Operations
- **Search Vehicle**: Find vehicle information
- **Check Availability**: View available slots
- **View History**: Transaction history

## 💳 Payment Integration

### Razorpay Setup
1. Create Razorpay account
2. Get API keys
3. Update `application.properties`:
   ```properties
   razorpay.key_id=your_key_id
   razorpay.key_secret=your_key_secret
   ```

### Payment Flow
1. User creates reservation
2. System generates payment link
3. User completes payment
4. Reservation confirmed
5. Notification sent

## 📧 Notification System

### Email Notifications
- Reservation created
- Payment confirmation
- Reservation reminders
- Cancellation notices
- Expiry alerts

### Configuration
Update email settings in `application.properties`:
```properties
app.email.enabled=true
mail.username=your_email@gmail.com
mail.password=your_app_password
```

## 🔄 Scheduled Tasks

### Auto-expiry (Every 5 minutes)
- Check for expired reservations
- Update reservation status
- Send expiry notifications

### Reminders (Every hour)
- Send upcoming reservation reminders
- System health checks

### Daily Cleanup (Midnight)
- Archive old transactions
- Generate reports
- System maintenance

## 🛡️ Concurrency & Performance

### Optimistic Locking
- Version-based conflict detection
- Automatic retry mechanism
- Thread-safe operations

### Performance Features
- Connection pooling
- Lazy loading
- Caching support (Redis ready)
- Batch operations

## 📈 Monitoring & Analytics

### System Metrics
- Slot occupancy rates
- Revenue tracking
- Peak hour analysis
- User activity patterns

### Health Monitoring
- Database connectivity
- Service availability
- Lock contention
- Error rates

## 🔧 Configuration

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/parkingdb
spring.datasource.username=parkinguser
spring.datasource.password=parkingpass

# Razorpay
razorpay.key_id=your_key_id
razorpay.key_secret=your_key_secret

# Email
app.email.enabled=true
mail.username=your_email@gmail.com
mail.password=your_app_password
```

## 🚀 Future Enhancements

### Planned Features
- **REST API**: RESTful endpoints for frontend integration
- **Web Dashboard**: React-based admin panel
- **Mobile App**: iOS/Android applications
- **Real-time Updates**: WebSocket integration
- **Advanced Analytics**: Machine learning insights
- **Multi-tenant**: Support for multiple organizations

### Scalability Improvements
- **Microservices**: Service decomposition
- **Caching**: Redis integration
- **Load Balancing**: Horizontal scaling
- **Message Queues**: Asynchronous processing

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check PostgreSQL service
   - Verify connection details
   - Ensure database exists

2. **Payment Integration Issues**
   - Verify Razorpay credentials
   - Check network connectivity
   - Review payment logs

3. **Email Notifications Not Working**
   - Check email configuration
   - Verify SMTP settings
   - Review firewall settings

### Logs
- Application logs: `logs/application.log`
- Database logs: Check PostgreSQL logs
- Payment logs: Console output

## 📝 API Documentation

### Service Methods

#### ParkingLotService
- `createParkingLot()`: Create new parking lot
- `updateParkingLot()`: Update lot details
- `deleteParkingLot()`: Deactivate lot
- `getParkingLotStatus()`: Get occupancy status

#### ReservationService
- `createReservation()`: Create new reservation
- `confirmReservation()`: Confirm payment
- `cancelReservation()`: Cancel booking
- `getUserReservations()`: Get user bookings

#### PaymentService
- `createPayment()`: Generate payment link
- `verifyPayment()`: Verify payment status
- `processRefund()`: Handle refunds

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

### Code Standards
- Follow Java naming conventions
- Add Javadoc comments
- Write unit tests
- Maintain code coverage

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

---

**Version**: 2.0.0  
**Last Updated**: December 2024  
**Maintainer**: APC Development Team
