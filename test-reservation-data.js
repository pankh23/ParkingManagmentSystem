// Test script to create a reservation with consistent data structure
const testReservation = {
  id: 1759058303813,
  userId: 1,
  lotId: 1,
  lotName: 'Lot 1',
  lotLocation: 'Main Location',
  slotType: '4W',
  vehicleNumber: 'QOP-253',
  startTime: '2025-10-09T19:45:00.000Z', // Oct 09, 19:45
  endTime: '2025-10-10T05:15:00.000Z',   // Oct 10, 05:15
  totalAmount: 0,
  status: 'CONFIRMED',
  createdAt: new Date().toISOString(),
  vehicleId: 1,
  vehiclePlate: 'QOP-253',
  userName: 'demo-user-1'
};

// Store in localStorage
localStorage.setItem('demoBookings', JSON.stringify([testReservation]));

console.log('Test reservation created:', testReservation);
console.log('Stored in localStorage:', JSON.parse(localStorage.getItem('demoBookings') || '[]'));
