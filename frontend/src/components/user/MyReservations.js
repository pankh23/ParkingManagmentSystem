import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Tag,
  Space,
  Popconfirm,
  message,
  Empty
} from 'antd';
import {
  CalendarOutlined,
  DollarOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import { useQuery } from 'react-query';
import { getAllReservations } from '../../services/api';
import moment from 'moment';

const MyReservations = () => {
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  // Fetch reservations from backend API
  const { data: allReservations = [], isLoading, refetch } = useQuery(
    'user-reservations',
    getAllReservations,
    {
      refetchInterval: 5000, // Refetch every 5 seconds
      onSuccess: (data) => {
        console.log('🔄 User reservations loaded from backend:', data);
      },
      onError: (error) => {
        console.error('❌ Error fetching user reservations:', error);
      }
    }
  );

  // Filter reservations for current user (for demo, show all reservations)
  // In a real app, you'd filter by userId from auth context
  const reservations = allReservations;

  // Listen for new bookings to refresh data
  useEffect(() => {
    const handleBookingCreated = () => {
      console.log('🎉 User: Booking created event received, refreshing data...');
      refetch();
    };

    window.addEventListener('bookingCreated', handleBookingCreated);
    return () => window.removeEventListener('bookingCreated', handleBookingCreated);
  }, [refetch]);

  // Cancel reservation function - call backend API
  const handleCancelReservation = async (reservationId) => {
    try {
      // Call backend API to cancel reservation
      const response = await fetch(`http://localhost:8080/api/reservations/${reservationId}/cancel`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      
      if (response.ok) {
        message.success('Reservation cancelled successfully!');
        // Refresh the data
        refetch();
        setSelectedRowKeys([]);
      } else {
        message.error('Failed to cancel reservation');
      }
    } catch (error) {
      console.error('Error cancelling reservation:', error);
      message.error('Error cancelling reservation');
    }
  };

  // Filter user's reservations
  const userReservations = reservations || [];
  
  // Calculate statistics
  const totalReservations = userReservations.length;
  const activeReservations = userReservations.filter(r => r.status === 'CONFIRMED').length;
  const pendingReservations = userReservations.filter(r => r.status === 'PENDING').length;
  const totalAmount = userReservations.reduce((sum, r) => sum + (r.totalAmount || 0), 0);

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED': return 'green';
      case 'PENDING': return 'orange';
      case 'CANCELLED': return 'red';
      default: return 'default';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'CONFIRMED': return <CheckCircleOutlined />;
      case 'PENDING': return <ClockCircleOutlined />;
      case 'CANCELLED': return <ExclamationCircleOutlined />;
      default: return <ClockCircleOutlined />;
    }
  };

  const handleCancel = (reservationId) => {
    handleCancelReservation(reservationId);
  };

  const handleBulkCancel = () => {
    if (selectedRowKeys.length === 0) {
      message.warning('Please select reservations to cancel');
      return;
    }
    // Cancel multiple reservations
    selectedRowKeys.forEach(id => {
      handleCancelReservation(id);
    });
  };

  const columns = [
    {
      title: 'Reservation ID',
      dataIndex: 'id',
      key: 'id',
      width: 120,
      render: (id) => (
        <div className="flex items-center">
          <div className="w-6 h-6 bg-blue-600 rounded flex items-center justify-center mr-2">
            <CalendarOutlined className="text-white text-sm" />
          </div>
          <span className="font-medium text-gray-800 text-sm">#{String(id).slice(-4)}</span>
        </div>
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => (
        <Tag 
          color={getStatusColor(status)} 
          icon={getStatusIcon(status)}
          className="text-sm font-semibold px-3 py-1 rounded-full"
        >
          {status}
        </Tag>
      ),
    },
    {
      title: 'Vehicle Type',
      key: 'vehicleType',
      width: 120,
      render: (_, record) => {
        const vehicleType = record.vehicle?.type || '4W';
        return (
          <div className="flex items-center">
            <div className={`w-6 h-6 rounded flex items-center justify-center mr-2 ${
              vehicleType === '2W' ? 'bg-orange-500' : 'bg-blue-600'
            }`}>
              <span className="text-sm">{vehicleType === '2W' ? '🏍️' : '🚗'}</span>
            </div>
            <span className="font-medium text-gray-700 text-sm">
              {vehicleType === '2W' ? '2W' : '4W'}
            </span>
          </div>
        );
      },
    },
    {
      title: 'Start Time',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 100,
      render: (startTime) => (
        <div>
          <div className="font-medium text-gray-800 text-sm">
            {moment(startTime).format('HH:mm')}
          </div>
          <div className="text-xs text-gray-500">
            {moment(startTime).format('MMM DD')}
          </div>
        </div>
      ),
    },
    {
      title: 'End Time',
      dataIndex: 'endTime',
      key: 'endTime',
      width: 100,
      render: (endTime) => (
        <div>
          <div className="font-medium text-gray-800 text-sm">
            {moment(endTime).format('HH:mm')}
          </div>
          <div className="text-xs text-gray-500">
            {moment(endTime).format('MMM DD')}
          </div>
        </div>
      ),
    },
    {
      title: 'Duration',
      key: 'duration',
      width: 100,
      render: (_, record) => {
        const duration = moment(record.endTime).diff(moment(record.startTime), 'hours', true);
        return (
          <div className="flex items-center">
            <span className="font-medium text-gray-700 text-sm">
              {duration.toFixed(1)}h
            </span>
          </div>
        );
      },
    },
    {
      title: 'Amount',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 100,
      render: (amount) => (
        <div className="flex items-center">
          <span className="font-semibold text-green-700 text-sm">
            ₹{amount?.toFixed(0) || '0'}
          </span>
        </div>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 120,
      render: (_, record) => (
        <Space>
          {record.status === 'CONFIRMED' && (
            <Popconfirm
              title="Are you sure you want to cancel this reservation?"
              onConfirm={() => handleCancel(record.id)}
              okText="Yes"
              cancelText="No"
            >
              <Button
                type="text"
                danger
                icon={<DeleteOutlined />}
                className="hover:bg-red-50 rounded-lg px-3 py-1 font-semibold text-sm"
              >
                Cancel
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: setSelectedRowKeys,
    getCheckboxProps: (record) => ({
      disabled: record.status === 'CANCELLED',
    }),
  };

  return (
    <div className="h-full bg-gray-50 flex flex-col p-4">
      {/* Header */}
      <div className="mb-4 flex-shrink-0">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h1 className="text-xl font-semibold text-gray-900 mb-1">
            My Reservations
          </h1>
          <p className="text-sm text-gray-600">
            Manage your parking reservations
          </p>
        </div>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-4 gap-4 mb-4 flex-shrink-0">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex flex-col items-center justify-center">
          <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center mb-2">
            <CalendarOutlined className="text-lg text-white" />
          </div>
          <h3 className="text-sm font-medium text-gray-600 mb-1">Total</h3>
          <div className="text-xl font-semibold text-gray-900 mb-1">{totalReservations}</div>
          <div className="text-xs text-gray-500">All Reservations</div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex flex-col items-center justify-center">
          <div className="w-10 h-10 bg-green-600 rounded-full flex items-center justify-center mb-2">
            <CheckCircleOutlined className="text-lg text-white" />
          </div>
          <h3 className="text-sm font-medium text-gray-600 mb-1">Active</h3>
          <div className="text-xl font-semibold text-gray-900 mb-1">{activeReservations}</div>
          <div className="text-xs text-gray-500">Confirmed</div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex flex-col items-center justify-center">
          <div className="w-10 h-10 bg-orange-500 rounded-full flex items-center justify-center mb-2">
            <ClockCircleOutlined className="text-lg text-white" />
          </div>
          <h3 className="text-sm font-medium text-gray-600 mb-1">Pending</h3>
          <div className="text-xl font-semibold text-gray-900 mb-1">{pendingReservations}</div>
          <div className="text-xs text-gray-500">Waiting</div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 flex flex-col items-center justify-center">
          <div className="w-10 h-10 bg-purple-600 rounded-full flex items-center justify-center mb-2">
            <DollarOutlined className="text-lg text-white" />
          </div>
          <h3 className="text-sm font-medium text-gray-600 mb-1">Spent</h3>
          <div className="text-xl font-semibold text-gray-900 mb-1">₹{totalAmount.toFixed(2)}</div>
          <div className="text-xs text-gray-500">Total Amount</div>
        </div>
      </div>

      {/* Reservations Table */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 flex-1 flex flex-col">
        <div className="flex items-center justify-between p-4 border-b border-gray-200 flex-shrink-0">
          <div className="flex items-center">
            <div className="w-6 h-6 bg-blue-600 rounded flex items-center justify-center mr-2">
              <CalendarOutlined className="text-white text-sm" />
            </div>
            <h3 className="text-lg font-semibold text-gray-800">Reservations</h3>
          </div>
          
          <div className="flex items-center space-x-4">
            {selectedRowKeys.length > 0 && (
              <Popconfirm
                title={`Are you sure you want to cancel ${selectedRowKeys.length} selected reservations?`}
                onConfirm={handleBulkCancel}
                okText="Yes"
                cancelText="No"
              >
                <Button
                  danger
                  icon={<DeleteOutlined />}
                  className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg shadow-sm"
                >
                  Cancel Selected ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            )}
          </div>
        </div>

        {userReservations.length > 0 ? (
          <div className="flex-1 overflow-hidden">
            <Table
              columns={columns}
              dataSource={userReservations}
              rowKey="id"
              rowSelection={rowSelection}
              loading={isLoading}
              pagination={{
                pageSize: 5,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} reservations`,
                size: 'default',
              }}
              className="custom-table h-full"
              scroll={{ x: 800 }}
              rowClassName="hover:bg-gray-50 transition-colors duration-200"
              size="default"
            />
          </div>
        ) : (
          <div className="text-center py-8">
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={
                <div>
                  <h3 className="text-lg font-semibold text-gray-600 mb-2">
                    No reservations found
                  </h3>
                  <p className="text-gray-500 mb-4 text-sm">
                    You haven't made any parking reservations yet
                  </p>
                  <Button
                    type="primary"
                    size="large"
                    className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg shadow-sm"
                    icon={<CalendarOutlined />}
                  >
                    Book Slot
                  </Button>
                </div>
              }
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default MyReservations;