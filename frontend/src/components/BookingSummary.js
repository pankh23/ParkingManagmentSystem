import React from 'react';
import { CheckCircleOutlined, EnvironmentOutlined } from '@ant-design/icons';

const BookingSummary = ({ selectedLot, selectedVehicleType, vehicleNumber, price, timeRange }) => {
  const formatTime = (time) => {
    if (!time || !time.format) return '';
    return time.format('MMM DD, YYYY HH:mm');
  };

  const calculateDuration = () => {
    if (!timeRange || timeRange.length !== 2) return 0;
    const [start, end] = timeRange;
    if (!start || !end) return 0;
    return end.diff(start, 'hours', true);
  };

  const duration = calculateDuration();

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-green-600 rounded-xl flex items-center justify-center mx-auto mb-4">
          <CheckCircleOutlined className="text-white text-2xl" />
        </div>
        <h3 className="text-2xl font-bold text-gray-800 mb-2">Booking Summary</h3>
        <p className="text-gray-600">Review your reservation details</p>
      </div>

      {selectedLot ? (
        <div className="space-y-6">
          {/* Parking Lot Details */}
          <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-4">
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <h4 className="text-xl font-bold text-gray-800 mb-2">{selectedLot.name}</h4>
                <div className="flex items-center text-gray-600 mb-3">
                  <EnvironmentOutlined className="text-sm mr-2" />
                  <span className="text-sm font-medium">{selectedLot.location}</span>
                </div>
              </div>
              <div className="text-right">
                <div className="text-xs text-gray-500 mb-1 font-medium">Starting from</div>
                <div className="text-2xl font-bold text-blue-600">
                  ₹{Math.min(selectedLot.pricePerHour2W, selectedLot.pricePerHour4W)}/hr
                </div>
              </div>
            </div>
            
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-white rounded-lg p-3 border border-orange-200">
                <div className="flex items-center mb-2">
                  <span className="text-lg mr-2">🏍️</span>
                  <span className="text-sm font-bold text-gray-700">2W</span>
                </div>
                <div className="text-xs text-gray-600 mb-1">
                  {selectedLot.availableSlots2W} slots
                </div>
                <div className="text-lg font-bold text-gray-800">
                  ₹{selectedLot.pricePerHour2W}/hr
                </div>
              </div>
              
              <div className="bg-white rounded-lg p-3 border border-blue-200">
                <div className="flex items-center mb-2">
                  <span className="text-lg mr-2">🚗</span>
                  <span className="text-sm font-bold text-gray-700">4W</span>
                </div>
                <div className="text-xs text-gray-600 mb-1">
                  {selectedLot.availableSlots4W} slots
                </div>
                <div className="text-lg font-bold text-gray-800">
                  ₹{selectedLot.pricePerHour4W}/hr
                </div>
              </div>
            </div>
          </div>

          {/* Vehicle Details and Duration */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white rounded-lg p-4 border border-gray-200">
              <h5 className="text-lg font-bold text-gray-800 mb-2">Vehicle Details</h5>
              <div className="space-y-3">
                <div className="flex items-center">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center mr-3 ${
                    selectedVehicleType === '2W' ? 'bg-orange-500' : 'bg-blue-600'
                  }`}>
                    <span className="text-xl">{selectedVehicleType === '2W' ? '🏍️' : '🚗'}</span>
                  </div>
                  <div>
                    <div className="font-bold text-gray-800">
                      {selectedVehicleType === '2W' ? '2 Wheeler' : '4 Wheeler'}
                    </div>
                    <div className="text-sm text-gray-600">
                      {selectedVehicleType === '2W' ? 'Motorcycle, Scooter' : 'Car, SUV, Van'}
                    </div>
                  </div>
                </div>
                {vehicleNumber && (
                  <div className="bg-gray-50 rounded-lg p-3 border border-gray-200">
                    <div className="text-sm font-medium text-gray-600 mb-1">Vehicle Number</div>
                    <div className="text-lg font-bold text-gray-800">{vehicleNumber}</div>
                  </div>
                )}
              </div>
            </div>

            <div className="bg-white rounded-lg p-4 border border-gray-200">
              <h5 className="text-lg font-bold text-gray-800 mb-2">Duration</h5>
              <div className="text-3xl font-bold text-blue-600 mb-1">
                {duration.toFixed(1)}h
              </div>
              <div className="text-sm text-gray-600">
                {timeRange && timeRange.length === 2 ? (
                  <>
                    {formatTime(timeRange[0])} - {formatTime(timeRange[1])}
                  </>
                ) : (
                  'Select time range'
                )}
              </div>
            </div>
          </div>

          {/* Pricing Breakdown */}
          <div className="bg-green-50 border border-green-200 rounded-lg p-4">
            <div className="flex justify-between items-center mb-4">
              <h5 className="text-xl font-bold text-gray-800">Total Amount</h5>
              <div className="text-right">
                <div className="text-3xl font-bold text-green-600">
                  ₹{price.toFixed(2)}
                </div>
                {duration > 0 && (
                  <div className="text-sm text-gray-600">
                    ₹{selectedLot ? (selectedVehicleType === '2W' ? selectedLot.pricePerHour2W : selectedLot.pricePerHour4W) : 0}/hr × {duration.toFixed(1)}h
                  </div>
                )}
              </div>
            </div>
            
            <div className="bg-white rounded-lg p-3 border border-green-200">
              <div className="flex items-center justify-center">
                <CheckCircleOutlined className="text-green-500 text-lg mr-2" />
                <span className="text-sm font-bold text-green-700">
                  Secure payment processing included
                </span>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="text-center py-12">
          <div className="w-20 h-20 bg-gray-100 text-gray-400 rounded-xl flex items-center justify-center mx-auto mb-6">
            <EnvironmentOutlined className="text-4xl" />
          </div>
          <h4 className="text-lg font-semibold text-gray-600 mb-3">
            Select a parking lot
          </h4>
          <p className="text-gray-500">
            Choose your preferred location to see pricing details
          </p>
        </div>
      )}
    </div>
  );
};

export default BookingSummary;
