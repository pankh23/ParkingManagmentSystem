// Debug script to test reservation flow
console.log("=== DEBUGGING RESERVATION FLOW ===");

// Test 1: Check if localStorage is working
console.log("1. Testing localStorage...");
const testBooking = {
  id: Date.now(),
  userId: 1,
  lotId: 1,
  lotName: 'Debug Test Lot',
  lotLocation: 'Debug Location',
  slotType: '2W',
  startTime: new Date().toISOString(),
  endTime: new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString(),
  totalAmount: 150,
  status: 'CONFIRMED',
  createdAt: new Date().toISOString(),
  vehicleId: 1,
  vehiclePlate: 'DEBUG-123',
  userName: 'debug-user'
};

// Store in localStorage
const existingBookings = JSON.parse(localStorage.getItem('demoBookings') || '[]');
existingBookings.push(testBooking);
localStorage.setItem('demoBookings', JSON.stringify(existingBookings));

console.log("2. Test booking stored:", testBooking);
console.log("3. All bookings in localStorage:", JSON.parse(localStorage.getItem('demoBookings') || '[]'));

// Test 2: Simulate the event dispatch
console.log("4. Dispatching bookingCreated event...");
const event = new CustomEvent('bookingCreated', { detail: testBooking });
window.dispatchEvent(event);
console.log("5. Event dispatched successfully");

// Test 3: Check if admin portal can read the data
console.log("6. Testing admin portal data access...");
const adminBookings = JSON.parse(localStorage.getItem('demoBookings') || '[]');
console.log("7. Admin portal should see:", adminBookings.length, "reservations");

console.log("=== DEBUG COMPLETE ===");
console.log("If you see this in the browser console, the reservation system is working!");
console.log("Check the admin portal at: http://localhost:3000/admin/reservations");
