import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from 'antd';
import {
  CarOutlined,
  CalendarOutlined,
  DollarOutlined,
  PlusOutlined,
  EyeOutlined,
  EnvironmentOutlined,
  StarOutlined
} from '@ant-design/icons';
import { useAuth } from '../context/AuthContext';
import { useQuery } from 'react-query';
import { getActiveParkingLots, getUserReservations } from '../services/api';
import moment from 'moment';

const UserDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  // Fetch parking lots from API
  const { data: parkingLots = [] } = useQuery(
    'parkingLots',
    getActiveParkingLots
  );

  // Fetch user-specific reservations from API
  const { data: userReservations = [], isLoading: isLoadingReservations, refetch: refetchReservations } = useQuery(
    ['userReservations', user?.id],
    () => {
      if (!user?.id) {
        return Promise.resolve([]);
      }
      return getUserReservations(user.id);
    },
    {
      enabled: !!user?.id, // Only fetch when user is available
      refetchInterval: 5000, // Refetch every 5 seconds to get latest data
      onSuccess: (data) => {
        console.log('✅ User reservations loaded:', data);
      },
      onError: (error) => {
        console.error('❌ Error fetching user reservations:', error);
      }
    }
  );

  // Listen for new bookings to refresh data
  useEffect(() => {
    const handleBookingCreated = () => {
      console.log('🎉 Booking created event received, refreshing reservations...');
      refetchReservations();
    };

    window.addEventListener('bookingCreated', handleBookingCreated);
    return () => window.removeEventListener('bookingCreated', handleBookingCreated);
  }, [refetchReservations]);
  // Process user's reservations
  const activeReservations = userReservations.filter(r => {
    const status = r.status?.toString() || r.status;
    const statusString = typeof status === 'string' ? status : status?.name() || '';
    return statusString === 'CONFIRMED';
  });
  const totalAmount = userReservations.reduce((sum, r) => sum + (r.totalAmount || 0), 0);

  // Filter available parking lots
  const availableLots = parkingLots.filter(lot => 
    lot.isActive && (lot.availableSlots2W > 0 || lot.availableSlots4W > 0)
  );


  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="bg-white rounded-xl shadow-md p-6">
            <h1 className="text-2xl font-bold text-gray-800 mb-2">
              Welcome back, {user?.username}! 👋
            </h1>
            <p className="text-lg text-gray-600">
              Manage your parking reservations and find available slots
            </p>
          </div>
        </div>

        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-xl shadow-md p-6 flex flex-col items-center justify-center">
            <div className="w-16 h-16 bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center mb-4 shadow-lg">
              <CarOutlined className="text-3xl text-white" />
            </div>
            <h3 className="text-lg font-bold text-gray-700 mb-2">Active Reservations</h3>
            <div className="text-3xl font-bold text-blue-600 mb-1">{activeReservations.length}</div>
            <div className="text-sm text-gray-500">Currently parked</div>
          </div>

          <div className="bg-white rounded-xl shadow-md p-6 flex flex-col items-center justify-center">
            <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-green-600 rounded-2xl flex items-center justify-center mb-4 shadow-lg">
              <DollarOutlined className="text-3xl text-white" />
            </div>
            <h3 className="text-lg font-bold text-gray-700 mb-2">Total Spent</h3>
            <div className="text-3xl font-bold text-green-600 mb-1">₹{totalAmount.toFixed(2)}</div>
            <div className="text-sm text-gray-500">All time spending</div>
          </div>

          <div className="bg-white rounded-xl shadow-md p-6 flex flex-col items-center justify-center">
            <div className="w-16 h-16 bg-gradient-to-r from-purple-500 to-purple-600 rounded-2xl flex items-center justify-center mb-4 shadow-lg">
              <CalendarOutlined className="text-3xl text-white" />
            </div>
            <h3 className="text-lg font-bold text-gray-700 mb-2">Total Reservations</h3>
            <div className="text-3xl font-bold text-purple-600 mb-1">{userReservations.length}</div>
            <div className="text-sm text-gray-500">Booking history</div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Available Parking Lots */}
          <div className="bg-white rounded-xl shadow-md p-6">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center">
                <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl flex items-center justify-center mr-4 shadow-lg">
                  <EnvironmentOutlined className="text-white text-xl" />
                </div>
                <h3 className="text-2xl font-bold text-gray-800">Available Parking Lots</h3>
              </div>
              <Button
                type="primary"
                onClick={() => navigate('/user/book-slot')}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow-md"
                icon={<PlusOutlined />}
              >
                Book Slot
              </Button>
            </div>

            <div className="space-y-4">
              {availableLots.slice(0, 3).map((lot) => (
                <div 
                  key={lot.id} 
                  className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-all duration-200 bg-gray-50"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <div className="flex items-center mb-2">
                        <h4 className="text-lg font-bold text-gray-800 mr-3">{lot.name}</h4>
                        <div className="flex items-center bg-yellow-100 px-2 py-1 rounded-full">
                          <StarOutlined className="text-yellow-500 text-xs mr-1" />
                          <span className="text-xs font-semibold text-yellow-700">4.8</span>
                        </div>
                      </div>
                      <div className="flex items-center text-gray-600 mb-3">
                        <EnvironmentOutlined className="text-sm mr-2" />
                        <span className="text-sm font-medium">{lot.location}</span>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-xs text-gray-500 mb-1 font-medium">Starting from</div>
                      <div className="text-xl font-bold text-blue-600">
                        ₹{Math.min(lot.pricePerHour2W, lot.pricePerHour4W)}/hr
                      </div>
                    </div>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-3">
                    <div className="bg-orange-50 rounded-lg p-3 border border-orange-200">
                      <div className="flex items-center mb-1">
                        <span className="text-sm mr-2">🏍️</span>
                        <span className="text-xs font-bold text-gray-700">2W</span>
                      </div>
                      <div className="text-xs text-gray-600 mb-1">
                        {lot.availableSlots2W} slots
                      </div>
                      <div className="text-sm font-bold text-gray-800">
                        ₹{lot.pricePerHour2W}/hr
                      </div>
                    </div>
                    
                    <div className="bg-blue-50 rounded-lg p-3 border border-blue-200">
                      <div className="flex items-center mb-1">
                        <span className="text-sm mr-2">🚗</span>
                        <span className="text-xs font-bold text-gray-700">4W</span>
                      </div>
                      <div className="text-xs text-gray-600 mb-1">
                        {lot.availableSlots4W} slots
                      </div>
                      <div className="text-sm font-bold text-gray-800">
                        ₹{lot.pricePerHour4W}/hr
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {availableLots.length > 3 && (
              <div className="text-center mt-4">
                <Button
                  type="link"
                  onClick={() => navigate('/user/book-slot')}
                  className="text-blue-600 hover:text-blue-700 font-semibold"
                >
                  View all {availableLots.length} parking lots →
                </Button>
              </div>
            )}
          </div>

          {/* Recent Reservations */}
          <div className="bg-white rounded-xl shadow-md p-6">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center">
                <div className="w-12 h-12 bg-gradient-to-r from-green-500 to-green-600 rounded-xl flex items-center justify-center mr-4 shadow-lg">
                  <CalendarOutlined className="text-white text-xl" />
                </div>
                <h3 className="text-2xl font-bold text-gray-800">Recent Reservations</h3>
              </div>
              <Button
                type="link"
                onClick={() => navigate('/user/my-reservations')}
                className="text-blue-600 hover:text-blue-700 font-semibold"
                icon={<EyeOutlined />}
              >
                View All
              </Button>
            </div>

            {isLoadingReservations ? (
              <div className="text-center py-12">
                <div className="text-gray-500">Loading reservations...</div>
              </div>
            ) : userReservations.length > 0 ? (
              <div className="space-y-3">
                {userReservations.slice(0, 5).map((reservation) => {
                  // Handle both string and enum status values
                  const status = reservation.status?.toString() || reservation.status;
                  const statusString = typeof status === 'string' ? status : status?.name() || 'PENDING';
                  
                  return (
                    <div 
                      key={reservation.id} 
                      className="border border-gray-200 rounded-lg p-4 bg-gray-50 hover:shadow-md transition-all duration-200"
                    >
                      <div className="flex items-start justify-between mb-2">
                        <div className="flex-1">
                          <div className="flex items-center mb-2">
                            <h4 className="text-lg font-bold text-gray-800 mr-3">
                              Reservation #{reservation.id}
                            </h4>
                            <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                              statusString === 'CONFIRMED' ? 'bg-green-100 text-green-700' :
                              statusString === 'PENDING' ? 'bg-orange-100 text-orange-700' :
                              statusString === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                              statusString === 'COMPLETED' ? 'bg-blue-100 text-blue-700' :
                              'bg-gray-100 text-gray-700'
                            }`}>
                              {statusString}
                            </span>
                          </div>
                          <div className="text-sm text-gray-600 mb-1">
                            {moment(reservation.startTime).format('MMM DD, YYYY')} - {moment(reservation.endTime).format('MMM DD, YYYY')}
                          </div>
                          <div className="text-sm text-gray-500">
                            {moment(reservation.startTime).format('HH:mm')} - {moment(reservation.endTime).format('HH:mm')}
                          </div>
                        </div>
                        <div className="text-right">
                          <div className="text-xl font-bold text-green-600">
                            ₹{reservation.totalAmount?.toFixed(2) || '0.00'}
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            ) : (
              <div className="text-center py-12">
                <div className="w-16 h-16 bg-gradient-to-r from-gray-200 to-gray-300 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
                  <CalendarOutlined className="text-3xl text-gray-400" />
                </div>
                <h4 className="text-xl font-bold text-gray-600 mb-2">
                  No reservations yet
                </h4>
                <p className="text-gray-500 mb-4">
                  Start by booking your first parking slot
                </p>
                <Button
                  type="primary"
                  onClick={() => navigate('/user/book-slot')}
                  className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow-md"
                  icon={<PlusOutlined />}
                >
                  Book Your First Slot
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserDashboard;