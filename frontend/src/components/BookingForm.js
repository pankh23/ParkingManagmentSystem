import React, { useState, useRef, useEffect, memo } from 'react';
import { Form, DatePicker } from 'antd';
import {
  CalendarOutlined,
  EnvironmentOutlined,
  CarOutlined,
  ClockCircleOutlined,
  InfoCircleOutlined,
  DownOutlined,
  StarOutlined
} from '@ant-design/icons';
import { useQuery } from 'react-query';
import { getActiveParkingLots, getSlotAvailability } from '../services/api';
import moment from 'moment';

const { RangePicker } = DatePicker;

const BookingForm = ({ onFormChange, selectedLot, setSelectedLot, selectedVehicleType, setSelectedVehicleType }) => {
  const [form] = Form.useForm();
  const [showLotDropdown, setShowLotDropdown] = useState(false);
  const [showVehicleDropdown, setShowVehicleDropdown] = useState(false);
  const [slotAvailability, setSlotAvailability] = useState({});
  const [pickerOpen, setPickerOpen] = useState(false);
  const lotDropdownRef = useRef(null);
  const vehicleDropdownRef = useRef(null);
  const pickerRef = useRef(null);

  // Add close button to calendar popup
  useEffect(() => {
    if (pickerOpen) {
      const addCloseButton = () => {
        const dropdown = document.querySelector('.ant-picker-dropdown');
        if (dropdown && !dropdown.querySelector('.calendar-close-btn')) {
          const closeButton = document.createElement('button');
          closeButton.className = 'calendar-close-btn';
          closeButton.innerHTML = '✕';
          closeButton.style.cssText = `
            position: absolute;
            top: 6px;
            right: 6px;
            background: #ff4d4f;
            color: white;
            border: none;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            cursor: pointer;
            font-size: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1001;
            transition: all 0.2s ease;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
          `;
          
          closeButton.onmouseenter = () => {
            closeButton.style.background = '#ff7875';
            closeButton.style.transform = 'scale(1.1)';
          };
          
          closeButton.onmouseleave = () => {
            closeButton.style.background = '#ff4d4f';
            closeButton.style.transform = 'scale(1)';
          };
          
          closeButton.onclick = (e) => {
            e.stopPropagation();
            console.log('Calendar close button clicked');
            setPickerOpen(false);
          };
          
          dropdown.style.position = 'relative';
          dropdown.appendChild(closeButton);
        }
      };
      
      // Add close button after a short delay to ensure dropdown is rendered
      setTimeout(addCloseButton, 100);
    }
  }, [pickerOpen]);

  // Fetch parking lots
  const { data: parkingLots = [] } = useQuery(
    'parkingLots',
    getActiveParkingLots
  );

  // Fetch slot availability for each parking lot
  useEffect(() => {
    const fetchSlotAvailability = async () => {
      if (parkingLots.length > 0) {
        const availability = {};
        for (const lot of parkingLots) {
          try {
            const data = await getSlotAvailability(lot.id);
            availability[lot.id] = data;
          } catch (error) {
            console.error(`Failed to fetch slot availability for lot ${lot.id}:`, error);
            // Fallback to default values
            availability[lot.id] = {
              '2W': lot.availableSlots2W || 0,
              '4W': lot.availableSlots4W || 0
            };
          }
        }
        setSlotAvailability(availability);
      }
    };

    fetchSlotAvailability();
  }, [parkingLots]);

  // Handle ResizeObserver errors specifically for this component
  useEffect(() => {
    const handleError = (event) => {
      if (event.error && event.error.message && 
          event.error.message.includes('ResizeObserver loop completed with undelivered notifications')) {
        event.preventDefault();
        return false;
      }
    };

    window.addEventListener('error', handleError);
    return () => window.removeEventListener('error', handleError);
  }, []);

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (lotDropdownRef.current && !lotDropdownRef.current.contains(event.target)) {
        setShowLotDropdown(false);
      }
      if (vehicleDropdownRef.current && !vehicleDropdownRef.current.contains(event.target)) {
        setShowVehicleDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLotSelect = (lot) => {
    setSelectedLot(lot);
    setShowLotDropdown(false);
    form.setFieldsValue({ lotId: lot.id });
    onFormChange({ lotId: lot.id, selectedLot: lot }, form.getFieldsValue());
  };

  const handleVehicleTypeSelect = (type) => {
    setSelectedVehicleType(type);
    setShowVehicleDropdown(false);
    form.setFieldsValue({ slotType: type });
    onFormChange({ slotType: type }, form.getFieldsValue());
  };

  const disabledDate = (current) => {
    return current && current < moment().startOf('day');
  };

  const disabledTime = (current) => {
    if (current && current.isSame(moment(), 'day')) {
      return {
        disabledHours: () => {
          const hours = [];
          for (let i = 0; i < moment().hour(); i++) {
            hours.push(i);
          }
          return hours;
        }
      };
    }
    return {};
  };

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-blue-600 rounded-xl flex items-center justify-center mx-auto mb-4">
          <CalendarOutlined className="text-3xl text-white" />
        </div>
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Book Your Parking Slot</h2>
        <p className="text-gray-600">Reserve your parking slot in advance</p>
      </div>

      <Form
        form={form}
        layout="vertical"
        onValuesChange={onFormChange}
        size="large"
        className="space-y-6"
      >
        {/* Parking Lot Selection */}
        <div>
          <label className="block text-gray-700 font-medium mb-3">
            <EnvironmentOutlined className="mr-2" />
            Select Parking Lot
          </label>
          
          <Form.Item
            name="lotId"
            rules={[{ required: true, message: 'Please select a parking lot!' }]}
            className="mb-0"
          >
            <div className="relative" ref={lotDropdownRef}>
              <button
                type="button"
                onClick={() => setShowLotDropdown(!showLotDropdown)}
                className="w-full h-12 px-4 text-left bg-white border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center justify-between shadow-sm"
              >
                <div className="flex items-center">
                  {selectedLot ? (
                    <div className="flex items-center">
                      <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center mr-3">
                        <EnvironmentOutlined className="text-white text-sm" />
                      </div>
                      <div>
                        <div className="font-bold text-gray-800">{selectedLot.name}</div>
                        <div className="text-sm text-gray-600">{selectedLot.location}</div>
                      </div>
                    </div>
                  ) : (
                    <span className="text-gray-500">Choose your preferred parking lot</span>
                  )}
                </div>
                <DownOutlined className={`text-gray-400 transition-transform duration-200 ${showLotDropdown ? 'rotate-180' : ''}`} />
              </button>

              {showLotDropdown && (
                <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-lg shadow-lg border border-gray-200 z-50 max-h-80 overflow-y-auto">
                  {parkingLots.map((lot) => (
                    <div
                      key={lot.id}
                      onClick={() => handleLotSelect(lot)}
                      className="p-4 hover:bg-blue-50 cursor-pointer border-b border-gray-100 last:border-b-0 transition-colors duration-200"
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
                          <div className="text-lg font-bold text-blue-600">
                            ₹{Math.min(lot.pricePerHour2W, lot.pricePerHour4W)}/hr
                          </div>
                        </div>
                      </div>
                      
                      <div className="grid grid-cols-2 gap-3">
                        <div className={`rounded-lg p-3 border ${
                          (() => {
                            const availability = slotAvailability[lot.id];
                            const available = availability ? availability['2W'] || 0 : lot.availableSlots2W || 0;
                            return available > 0 ? 'bg-orange-50 border-orange-200' : 'bg-gray-100 border-gray-300';
                          })()
                        }`}>
                          <div className="flex items-center mb-1">
                            <span className="text-lg mr-2">🏍️</span>
                            <span className={`text-sm font-bold ${
                              (() => {
                                const availability = slotAvailability[lot.id];
                                const available = availability ? availability['2W'] || 0 : lot.availableSlots2W || 0;
                                return available > 0 ? 'text-gray-700' : 'text-gray-400';
                              })()
                            }`}>2W</span>
                          </div>
                          <div className={`text-xs mb-1 ${
                            (() => {
                              const availability = slotAvailability[lot.id];
                              const available = availability ? availability['2W'] || 0 : lot.availableSlots2W || 0;
                              return available > 0 ? 'text-gray-600' : 'text-red-500 font-semibold';
                            })()
                          }`}>
                            {(() => {
                              const availability = slotAvailability[lot.id];
                              const available = availability ? availability['2W'] || 0 : lot.availableSlots2W || 0;
                              return available > 0 ? `${available} slots` : 'No slots available';
                            })()}
                          </div>
                          <div className={`text-sm font-bold ${
                            (() => {
                              const availability = slotAvailability[lot.id];
                              const available = availability ? availability['2W'] || 0 : lot.availableSlots2W || 0;
                              return available > 0 ? 'text-gray-800' : 'text-gray-400';
                            })()
                          }`}>
                            ₹{lot.pricePerHour2W}/hr
                          </div>
                        </div>
                        
                        <div className={`rounded-lg p-3 border ${
                          (() => {
                            const availability = slotAvailability[lot.id];
                            const available = availability ? availability['4W'] || 0 : lot.availableSlots4W || 0;
                            return available > 0 ? 'bg-blue-50 border-blue-200' : 'bg-gray-100 border-gray-300';
                          })()
                        }`}>
                          <div className="flex items-center mb-1">
                            <span className="text-lg mr-2">🚗</span>
                            <span className={`text-sm font-bold ${
                              (() => {
                                const availability = slotAvailability[lot.id];
                                const available = availability ? availability['4W'] || 0 : lot.availableSlots4W || 0;
                                return available > 0 ? 'text-gray-700' : 'text-gray-400';
                              })()
                            }`}>4W</span>
                          </div>
                          <div className={`text-xs mb-1 ${
                            (() => {
                              const availability = slotAvailability[lot.id];
                              const available = availability ? availability['4W'] || 0 : lot.availableSlots4W || 0;
                              return available > 0 ? 'text-gray-600' : 'text-red-500 font-semibold';
                            })()
                          }`}>
                            {(() => {
                              const availability = slotAvailability[lot.id];
                              const available = availability ? availability['4W'] || 0 : lot.availableSlots4W || 0;
                              return available > 0 ? `${available} slots` : 'No slots available';
                            })()}
                          </div>
                          <div className={`text-sm font-bold ${
                            (() => {
                              const availability = slotAvailability[lot.id];
                              const available = availability ? availability['4W'] || 0 : lot.availableSlots4W || 0;
                              return available > 0 ? 'text-gray-800' : 'text-gray-400';
                            })()
                          }`}>
                            ₹{lot.pricePerHour4W}/hr
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </Form.Item>
        </div>

        {/* Vehicle Number Input */}
        <div>
          <label className="block text-gray-700 font-medium mb-3">
            <CarOutlined className="mr-2" />
            Vehicle Number
          </label>
          
          <Form.Item
            name="vehicleNumber"
            rules={[
              { required: true, message: 'Please enter your vehicle number!' },
              { min: 5, message: 'Vehicle number must be at least 5 characters!' }
            ]}
            className="mb-0"
          >
            <input
              type="text"
              placeholder="Enter your vehicle number (e.g., ABC-1234)"
              className="w-full h-12 px-4 text-left bg-white border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-all duration-200 shadow-sm"
            />
          </Form.Item>
        </div>

        {/* Vehicle Type Selection */}
        <div>
          <label className="block text-gray-700 font-medium mb-3">
            <CarOutlined className="mr-2" />
            Vehicle Type
          </label>
          
          <Form.Item
            name="slotType"
            rules={[{ required: true, message: 'Please select vehicle type!' }]}
            className="mb-0"
          >
            <div className="relative" ref={vehicleDropdownRef}>
              <button
                type="button"
                onClick={() => setShowVehicleDropdown(!showVehicleDropdown)}
                className="w-full h-12 px-4 text-left bg-white border border-gray-300 rounded-lg hover:border-blue-500 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-all duration-200 flex items-center justify-between shadow-sm"
              >
                <div className="flex items-center">
                  {selectedVehicleType ? (
                    <div className="flex items-center">
                      <div className={`w-8 h-8 rounded-lg flex items-center justify-center mr-3 ${
                        selectedVehicleType === '2W' ? 'bg-orange-500' : 'bg-blue-600'
                      }`}>
                        <span className="text-lg">{selectedVehicleType === '2W' ? '🏍️' : '🚗'}</span>
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
                  ) : (
                    <span className="text-gray-500">Select your vehicle type</span>
                  )}
                </div>
                <DownOutlined className={`text-gray-400 transition-transform duration-200 ${showVehicleDropdown ? 'rotate-180' : ''}`} />
              </button>

              {showVehicleDropdown && (
                <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
                  <div
                    onClick={() => handleVehicleTypeSelect('2W')}
                    className="flex items-center p-4 hover:bg-orange-50 cursor-pointer transition-colors duration-200"
                  >
                    <div className="w-12 h-12 bg-orange-500 rounded-lg flex items-center justify-center mr-4">
                      <span className="text-2xl">🏍️</span>
                    </div>
                    <div>
                      <div className="font-bold text-gray-800 text-lg">2 Wheeler</div>
                      <div className="text-gray-600">Motorcycle, Scooter</div>
                    </div>
                  </div>
                  <div
                    onClick={() => handleVehicleTypeSelect('4W')}
                    className="flex items-center p-4 hover:bg-blue-50 cursor-pointer transition-colors duration-200"
                  >
                    <div className="w-12 h-12 bg-blue-600 rounded-lg flex items-center justify-center mr-4">
                      <span className="text-2xl">🚗</span>
                    </div>
                    <div>
                      <div className="font-bold text-gray-800 text-lg">4 Wheeler</div>
                      <div className="text-gray-600">Car, SUV, Van</div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </Form.Item>
        </div>

        {/* Date and Time Selection */}
        <div>
          <label className="block text-gray-700 font-medium mb-3">
            <ClockCircleOutlined className="mr-2" />
            Parking Duration
          </label>
          
          <Form.Item
            name="timeRange"
            rules={[{ required: true, message: 'Please select parking duration!' }]}
            className="mb-0"
          >
            <RangePicker
              ref={pickerRef}
              showTime={{
                format: 'HH:mm',
                minuteStep: 15,
                use12Hours: false
              }}
              format="YYYY-MM-DD HH:mm"
              disabledDate={disabledDate}
              disabledTime={disabledTime}
              className="w-full h-12 rounded-lg border border-gray-300 hover:border-blue-500 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 transition-all duration-200 shadow-sm"
              placeholder={['Start Date & Time', 'End Date & Time']}
              getPopupContainer={() => document.body}
              open={pickerOpen}
              onOpenChange={(open) => {
                console.log('Date picker open state changed:', open);
                setPickerOpen(open);
              }}
              onChange={(dates) => {
                console.log('Date picker onChange:', dates);
                if (dates && dates.length === 2 && dates[0] && dates[1]) {
                  onFormChange({ timeRange: dates }, form.getFieldsValue());
                  
                  // Close picker after both dates are selected
                  console.log('Both dates selected, closing picker');
                  setTimeout(() => {
                    setPickerOpen(false);
                  }, 100);
                } else {
                  onFormChange({ timeRange: null }, form.getFieldsValue());
                }
              }}
              onCalendarChange={(dates) => {
                console.log('Date picker onCalendarChange:', dates);
                // Close picker when both dates are selected in calendar
                if (dates && dates.length === 2 && dates[0] && dates[1]) {
                  console.log('Both dates selected in calendar, closing picker');
                  setTimeout(() => {
                    setPickerOpen(false);
                  }, 100);
                }
              }}
            />
          </Form.Item>
        </div>

        {/* Helpful Tip */}
        <div className="bg-blue-50 border border-blue-200 text-blue-700 rounded-lg p-4">
          <div className="flex items-start">
            <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center mr-3 mt-1">
              <InfoCircleOutlined className="text-white text-sm" />
            </div>
            <div>
              <span className="font-bold">Pro Tip:</span>
              <span className="ml-2">
                Select your start and end time to see the total cost. Time slots are available in 15-minute intervals.
              </span>
            </div>
          </div>
        </div>
      </Form>
    </div>
  );
};

export default memo(BookingForm);
