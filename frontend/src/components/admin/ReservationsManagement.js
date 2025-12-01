import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Select,
  Space,
  Card,
  Typography,
  Row,
  Col,
  Statistic,
  Tag,
  Modal,
  message
} from 'antd';
import {
  CalendarOutlined,
  EyeOutlined,
  CloseOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';
import { useQuery } from 'react-query';
import { getAllReservations } from '../../services/api';
import moment from 'moment';

const { Title } = Typography;
const { Option } = Select;

const ReservationsManagement = () => {
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  // Fetch reservations from backend API
  const { data: reservations = [], isLoading: backendLoading, refetch: refetchReservations } = useQuery(
    'admin-reservations',
    getAllReservations,
    {
      refetchInterval: 5000, // Refetch every 5 seconds
      onSuccess: (data) => {
        console.log('🔄 Admin reservations loaded from backend:', data);
      },
      onError: (error) => {
        console.error('❌ Error fetching admin reservations:', error);
      }
    }
  );

  // Listen for new bookings to refresh data
  useEffect(() => {
    const handleBookingCreated = (event) => {
      console.log('🎉 Admin: Booking created event received, refreshing data...');
      refetchReservations();
    };

    // Add event listener
    window.addEventListener('bookingCreated', handleBookingCreated);
    
    
    return () => {
      window.removeEventListener('bookingCreated', handleBookingCreated);
    };
  }, [refetchReservations]);

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
        refetchReservations();
      } else {
        message.error('Failed to cancel reservation');
      }
    } catch (error) {
      console.error('Error cancelling reservation:', error);
      message.error('Error cancelling reservation');
    }
  };

  const filteredReservations = reservations.filter(reservation => {
    if (statusFilter === 'all') return true;
    return reservation.status === statusFilter;
  });

  const handleViewDetails = (reservation) => {
    setSelectedReservation(reservation);
    setShowDetailModal(true);
  };

  const handleCancel = (reservationId) => {
    handleCancelReservation(reservationId);
  };

  const handleClearData = () => {
    // This function is no longer needed since we're using backend API
    message.info('Data is managed by the backend API');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return 'green';
      case 'CANCELLED':
        return 'red';
      case 'COMPLETED':
        return 'blue';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return <CheckCircleOutlined />;
      case 'CANCELLED':
        return <CloseOutlined />;
      case 'COMPLETED':
        return <CheckCircleOutlined />;
      default:
        return <ClockCircleOutlined />;
    }
  };

  const columns = [
    {
      title: 'Reservation ID',
      dataIndex: 'id',
      key: 'id',
      width: 120,
    },
    {
      title: 'User',
      key: 'user',
      render: (_, record) => {
        return record.user?.username || record.userId || 'N/A';
      },
    },
    {
      title: 'Vehicle',
      key: 'vehicle',
      render: (_, record) => {
        const vehiclePlate = record.vehicle?.plateNumber || 'N/A';
        const vehicleType = record.vehicle?.type === '2W' ? '2 Wheeler' : '4 Wheeler';
        return `${vehiclePlate} (${vehicleType})`;
      },
    },
    {
      title: 'Parking Lot',
      key: 'parkingLot',
      render: (_, record) => {
        return record.slot?.parkingLot?.name || `Lot ${record.lotId}` || 'N/A';
      },
    },
    {
      title: 'Slot',
      key: 'slot',
      render: (_, record) => {
        return record.slot?.slotNumber || 'N/A';
      },
    },
    {
      title: 'Start Time',
      key: 'startTime',
      render: (_, record) => moment(record.startTime).format('MMM DD, HH:mm'),
    },
    {
      title: 'End Time',
      key: 'endTime',
      render: (_, record) => moment(record.endTime).format('MMM DD, HH:mm'),
    },
    {
      title: 'Amount',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amount) => `₹${amount || 0}`,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={getStatusColor(status)} icon={getStatusIcon(status)}>
          {status}
        </Tag>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Button
            type="primary"
            icon={<EyeOutlined />}
            size="small"
            className="admin-action-button view-button"
            onClick={() => handleViewDetails(record)}
          >
            View
          </Button>
          {(record.status === 'PENDING' || record.status === 'CONFIRMED') && (
            <Button
              type="primary"
              danger
              icon={<CloseOutlined />}
              size="small"
              className="admin-action-button delete-button"
              onClick={() => handleCancel(record.id)}
            >
              Cancel
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const totalReservations = reservations.length;
  const confirmedReservations = reservations.filter(r => r.status === 'CONFIRMED').length;
  const totalRevenue = reservations.reduce((sum, r) => sum + (r.totalAmount || 0), 0);

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>Reservations Management</Title>
        <Title level={4} type="secondary">Monitor and manage all parking reservations</Title>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Reservations"
              value={totalReservations}
              prefix={<CalendarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Confirmed"
              value={confirmedReservations}
              prefix={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Revenue"
              value={totalRevenue}
              prefix="₹"
              precision={2}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <div style={{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>
            All Reservations ({reservations.length})
            {backendLoading && <span style={{ color: '#1890ff', fontSize: '14px', marginLeft: '8px' }}>🔄 Loading...</span>}
          </Title>
          <Space>
            <Button 
              type="text" 
              onClick={handleClearData}
              icon={<CloseOutlined />}
              danger
            >
              Clear All Data
            </Button>
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 150 }}
            >
              <Option value="all">All Status</Option>
              <Option value="CONFIRMED">Confirmed</Option>
              <Option value="CANCELLED">Cancelled</Option>
              <Option value="COMPLETED">Completed</Option>
            </Select>
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={filteredReservations}
          loading={backendLoading}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} reservations`,
          }}
        />
      </Card>

      <Modal
        title="Reservation Details"
        open={showDetailModal}
        onCancel={() => setShowDetailModal(false)}
        footer={[
          <Button key="close" onClick={() => setShowDetailModal(false)}>
            Close
          </Button>
        ]}
        width={700}
      >
        {selectedReservation && (
          <div>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <p><strong>Reservation ID:</strong> {selectedReservation.id}</p>
                <p><strong>User:</strong> {selectedReservation.user?.username || selectedReservation.userId || 'N/A'}</p>
                <p><strong>Vehicle:</strong> {selectedReservation.vehicle?.plateNumber || 'N/A'}</p>
                <p><strong>Parking Lot:</strong> {selectedReservation.slot?.parkingLot?.name || `Lot ${selectedReservation.lotId}` || 'N/A'}</p>
                <p><strong>Location:</strong> {selectedReservation.slot?.parkingLot?.location || 'N/A'}</p>
              </Col>
              <Col span={12}>
                <p><strong>Slot Number:</strong> {selectedReservation.slot?.slotNumber || 'N/A'}</p>
                <p><strong>Vehicle Type:</strong> {selectedReservation.vehicle?.type === '2W' ? '2 Wheeler' : '4 Wheeler'}</p>
                <p><strong>Status:</strong> 
                  <Tag color={getStatusColor(selectedReservation.status)} style={{ marginLeft: 8 }}>
                    {selectedReservation.status}
                  </Tag>
                </p>
                <p><strong>Total Amount:</strong> ₹{selectedReservation.totalAmount || 0}</p>
                <p><strong>Created At:</strong> {moment(selectedReservation.createdAt).format('MMMM DD, YYYY HH:mm')}</p>
              </Col>
            </Row>
            
            <div style={{ marginTop: '16px', padding: '16px', background: '#f5f5f5', borderRadius: '6px' }}>
              <p><strong>Start Time:</strong> {moment(selectedReservation.startTime).format('MMMM DD, YYYY [at] HH:mm')}</p>
              <p><strong>End Time:</strong> {moment(selectedReservation.endTime).format('MMMM DD, YYYY [at] HH:mm')}</p>
              <p><strong>Duration:</strong> {moment(selectedReservation.endTime).diff(moment(selectedReservation.startTime), 'hours')} hours</p>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default ReservationsManagement;
