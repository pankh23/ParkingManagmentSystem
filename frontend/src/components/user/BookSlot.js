import React, { useState, useEffect, useCallback } from 'react';
import { Button, message, Modal } from 'antd';
import { CreditCardOutlined } from '@ant-design/icons';
import { useAuth } from '../../context/AuthContext';
import { createReservation } from '../../services/api';
import { useQueryClient } from 'react-query';
import BookingForm from '../BookingForm';
import BookingSummary from '../BookingSummary';

const BookSlot = () => {
  const [selectedLot, setSelectedLot] = useState(null);
  const [selectedVehicleType, setSelectedVehicleType] = useState(null);
  const [vehicleNumber, setVehicleNumber] = useState('');
  const [timeRange, setTimeRange] = useState(null);
  const [price, setPrice] = useState(0);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const { user } = useAuth();
  const queryClient = useQueryClient();

  // Demo payment flow - no backend mutation needed

  const calculatePrice = useCallback(() => {
    console.log('Calculating price with:', { selectedLot, selectedVehicleType, timeRange });
    if (selectedLot && selectedVehicleType && timeRange && timeRange.length === 2 && timeRange[0] && timeRange[1]) {
      const rate = selectedVehicleType === '2W' ? selectedLot.pricePerHour2W : selectedLot.pricePerHour4W;
      const startTime = timeRange[0];
      const endTime = timeRange[1];
      const durationInHours = endTime.diff(startTime, 'hours', true);
      const calculatedPrice = durationInHours * rate;
      console.log('Price calculation:', { rate, durationInHours, calculatedPrice });
      setPrice(calculatedPrice);
    } else {
      console.log('Price calculation skipped - missing required data');
      setPrice(0);
    }
  }, [selectedLot, selectedVehicleType, timeRange]);

  // Recalculate price whenever dependencies change
  useEffect(() => {
    calculatePrice();
  }, [calculatePrice]);

  // Debug: Log price changes
  useEffect(() => {
    console.log('Price state changed to:', price);
  }, [price]);

  const handleFormChange = (changedValues, allValues) => {
    // Update state with form values - add null checks and use allValues as fallback
    const values = changedValues || allValues || {};
    console.log('Form change received:', { changedValues, allValues, values });
    
    if (values.vehicleNumber !== undefined) {
      setVehicleNumber(values.vehicleNumber);
    }
    if (values.timeRange !== undefined) {
      setTimeRange(values.timeRange);
    }
    if (values.selectedLot !== undefined) {
      console.log('Setting selectedLot:', values.selectedLot);
      setSelectedLot(values.selectedLot);
    }
    if (values.slotType !== undefined) {
      console.log('Setting selectedVehicleType:', values.slotType);
      setSelectedVehicleType(values.slotType);
    }
    // Price will be recalculated automatically via useEffect
  };

  const handleSubmit = (values) => {
    console.log('BookSlot: handleSubmit called with user:', user);
    if (!user) {
      console.log('BookSlot: No user found, creating demo user for testing');
      // Create a demo user for testing
      const demoUser = {
        id: 1,
        username: 'demo-user',
        role: 'USER',
        email: 'demo@example.com'
      };
      console.log('BookSlot: Using demo user:', demoUser);
      setShowPaymentModal(true);
      return;
    }
    console.log('BookSlot: User found, showing payment modal');
    setShowPaymentModal(true);
  };


  const handlePayment = async () => {
    if (!selectedLot || !selectedVehicleType || !vehicleNumber || !timeRange || timeRange.length !== 2) {
      message.error('Please complete all required fields including vehicle number and time range');
      return;
    }
    
    console.log('Starting payment process...', { selectedLot, selectedVehicleType, vehicleNumber, timeRange, price, user });
    
    setShowPaymentModal(false);
    message.loading('Processing payment...', 2);
    
    try {
      // Create booking object for backend API
      const currentUser = user || { id: 1, username: 'demo-user' };
      const startTime = timeRange[0].toDate();
      const endTime = timeRange[1].toDate();
      
      
      // Create reservation data for backend
      // Format time as local timezone string (YYYY-MM-DDTHH:mm:ss)
      const formatLocalTime = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
      };
      
      const reservationData = {
        userId: currentUser.id,
        vehicleId: 1, // Demo vehicle ID
        lotId: selectedLot.id,
        slotType: selectedVehicleType,
        vehicleNumber: vehicleNumber,
        startTime: formatLocalTime(startTime),
        endTime: formatLocalTime(endTime),
        totalAmount: price
      };
      
      console.log('Creating reservation with data:', reservationData);
      
      // Call backend API
      const reservation = await createReservation(reservationData);
      console.log('Reservation created successfully:', reservation);
      
      // Store in localStorage for both admin and user portals (consistent data structure)
      const booking = {
        id: reservation.id,
        userId: currentUser.id,
        lotId: selectedLot.id,
        lotName: selectedLot.name,
        lotLocation: selectedLot.location,
        slotType: selectedVehicleType,
        vehicleNumber: vehicleNumber,
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        totalAmount: price,
        status: 'CONFIRMED',
        createdAt: new Date().toISOString(),
        vehicleId: 1,
        vehiclePlate: vehicleNumber,
        userName: currentUser.username
      };
      
      const existingBookings = JSON.parse(localStorage.getItem('demoBookings') || '[]');
      existingBookings.push(booking);
      localStorage.setItem('demoBookings', JSON.stringify(existingBookings));
      
      // Invalidate queries to refresh admin panel
      queryClient.invalidateQueries(['reservations']);
      queryClient.invalidateQueries(['parkingLots']);
      
      // Success message
      message.success('🎉 Payment successful! Your parking slot has been booked successfully!');
      
      // Reset form
      setSelectedLot(null);
      setSelectedVehicleType(null);
      setPrice(0);
      
      // Show booking details
      message.info(`Booking confirmed for ${selectedLot?.name} - ${selectedVehicleType === '2W' ? '2 Wheeler' : '4 Wheeler'} slot`, 5);
      
      // Refresh slot availability data
      queryClient.invalidateQueries('parkingLots');
      
      // Trigger a custom event to notify other components
      console.log('Dispatching bookingCreated event with detail:', booking);
      const event = new CustomEvent('bookingCreated', { detail: booking });
      window.dispatchEvent(event);
      console.log('Event dispatched successfully');
      
    } catch (error) {
      console.error('Error creating reservation:', error);
      
      // Check if it's a "no slots available" error
      if (error.response && error.response.data && 
          error.response.data.includes('No available') && 
          error.response.data.includes('slots are currently full')) {
        message.error(error.response.data);
      } else {
        message.error('Failed to create reservation. Please try again.');
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Main Content */}
      <div className="max-w-7xl mx-auto p-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Booking Form */}
          <div className="lg:col-span-1">
            <BookingForm
              onFormChange={handleFormChange}
              selectedLot={selectedLot}
              setSelectedLot={setSelectedLot}
              selectedVehicleType={selectedVehicleType}
              setSelectedVehicleType={setSelectedVehicleType}
            />
            
            {/* Submit Button */}
            <div className="mt-6">
              <Button
                type="primary"
                size="large"
                onClick={handleSubmit}
                loading={false}
                className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg px-5 py-2 shadow-md h-12 text-lg"
                icon={<CreditCardOutlined />}
              >
                Proceed to Payment
              </Button>
            </div>
          </div>

          {/* Booking Summary */}
          <div className="lg:col-span-1">
            <BookingSummary
              selectedLot={selectedLot}
              selectedVehicleType={selectedVehicleType}
              vehicleNumber={vehicleNumber}
              price={price}
              timeRange={timeRange}
            />
          </div>
        </div>
      </div>

      {/* Payment Modal */}
      <Modal
        title={
          <div className="flex items-center">
            <div className="w-12 h-12 bg-blue-600 rounded-lg flex items-center justify-center mr-4">
              <CreditCardOutlined className="text-white text-xl" />
            </div>
            <span className="text-2xl font-bold text-gray-800">Payment Confirmation</span>
          </div>
        }
        open={showPaymentModal}
        onCancel={() => setShowPaymentModal(false)}
        footer={[
          <Button 
            key="cancel" 
            onClick={() => setShowPaymentModal(false)}
            size="large"
            className="bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg px-5 py-2 h-12 font-semibold"
          >
            Cancel
          </Button>,
          <Button 
            key="pay" 
            type="primary" 
            onClick={handlePayment}
            loading={false}
            size="large"
            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg px-5 py-2 shadow-md h-12"
          >
            Confirm Payment - ₹{price.toFixed(2)}
          </Button>
        ]}
        width={700}
        className="!top-8"
        styles={{
          body: { padding: '32px' }
        }}
      >
        <div className="bg-gray-50 rounded-lg p-6 mb-6">
          <div className="flex items-start justify-between mb-4">
            <div className="flex-1">
              <h4 className="text-xl font-bold text-gray-800 mb-2">{selectedLot?.name}</h4>
              <div className="flex items-center text-gray-600">
                <span className="text-sm font-medium">{selectedLot?.location}</span>
              </div>
            </div>
            <div className="text-right">
              <div className="text-sm text-gray-500 mb-1 font-medium">Total Amount</div>
              <div className="text-3xl font-bold text-green-600">₹{price.toFixed(2)}</div>
            </div>
          </div>
        </div>
        
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3 text-yellow-700 flex items-center gap-2">
          <span className="text-yellow-600 text-xl">⚠️</span>
          <span className="font-bold">
            This is a demo payment. No actual payment will be processed.
          </span>
        </div>
      </Modal>
    </div>
  );
};

export default BookSlot;