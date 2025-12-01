import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Card, 
  Row, 
  Col, 
  Statistic, 
  Button, 
  Space, 
  Typography, 
  Progress, 
  Tag,
  Alert,
  Spin,
  Empty,
  List,
  Avatar
} from 'antd';
import {
  PlusOutlined,
  CarOutlined,
  HomeOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  DollarOutlined
} from '@ant-design/icons';
import { useQuery } from 'react-query';
import api, { getSlotAvailability } from '../services/api';

const { Title, Text } = Typography;

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [slotAvailability, setSlotAvailability] = useState({});

  // Fetch data with error handling
  const { data: parkingLots = [], isLoading: lotsLoading } = useQuery(
    'parkingLots',
    () => api.get('/parking-lots').then(res => res.data),
    {
      onError: (error) => {
        console.log('Failed to fetch parking lots:', error);
      }
    }
  );

  const { data: systemStatus = 'System running normally', isLoading: statusLoading, error: statusError } = useQuery(
    'systemStatus',
    () => api.get('/parking/status').then(res => res.data),
    {
      onError: (error) => {
        console.log('Failed to fetch system status:', error);
      }
    }
  );

  // Load reservations from localStorage (demo data)
  const [allReservations, setAllReservations] = useState([]);
  const [reservationsLoading, setReservationsLoading] = useState(false);

  // Load demo reservations from localStorage
  useEffect(() => {
    const loadReservations = () => {
      const demoBookings = JSON.parse(localStorage.getItem('demoBookings') || '[]');
      setAllReservations(demoBookings);
    };

    loadReservations();

    // Listen for new bookings
    const handleBookingCreated = () => {
      const demoBookings = JSON.parse(localStorage.getItem('demoBookings') || '[]');
      setAllReservations(demoBookings);
    };

    window.addEventListener('bookingCreated', handleBookingCreated);
    
    return () => {
      window.removeEventListener('bookingCreated', handleBookingCreated);
    };
  }, []);

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
              '2W': lot.totalSlots2W || 0,
              '4W': lot.totalSlots4W || 0
            };
          }
        }
        setSlotAvailability(availability);
      }
    };

    fetchSlotAvailability();
    
    // Refresh slot availability every 15 seconds (reduced frequency)
    const refreshInterval = setInterval(fetchSlotAvailability, 15000);
    
    return () => clearInterval(refreshInterval);
  }, [parkingLots]);

  // Calculate statistics with useMemo to prevent unnecessary recalculations
  const statistics = useMemo(() => {
    const totalSlots = parkingLots.reduce((sum, lot) => sum + (lot.totalSlots2W || 0) + (lot.totalSlots4W || 0), 0);
    const availableSlots = parkingLots.reduce((sum, lot) => {
      const availability = slotAvailability[lot.id];
      if (availability) {
        return sum + (availability['2W'] || 0) + (availability['4W'] || 0);
      }
      return sum + (lot.totalSlots2W || 0) + (lot.totalSlots4W || 0);
    }, 0);
    const occupiedSlots = totalSlots - availableSlots;
    const occupancyRate = totalSlots > 0 ? (occupiedSlots / totalSlots) * 100 : 0;
    const activeLots = parkingLots.filter(lot => lot.status === 'ACTIVE').length;

    const totalReservations = allReservations.length;
    const confirmedReservations = allReservations.filter(r => r.status === 'CONFIRMED').length;
    const cancelledReservations = allReservations.filter(r => r.status === 'CANCELLED').length;
    const totalRevenue = allReservations.reduce((sum, r) => sum + (r.totalAmount || 0), 0);

    return {
      totalSlots,
      availableSlots,
      occupiedSlots,
      occupancyRate,
      activeLots,
      totalReservations,
      confirmedReservations,
      cancelledReservations,
      totalRevenue
    };
  }, [parkingLots, slotAvailability, allReservations]);

  const { totalSlots, availableSlots, occupiedSlots, occupancyRate, activeLots, totalReservations, confirmedReservations, cancelledReservations, totalRevenue } = statistics;

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'green';
      case 'MAINTENANCE': return 'orange';
      case 'INACTIVE': return 'red';
      default: return 'default';
    }
  };

  const QuickActions = () => (
    <Card 
      title={
        <Space>
          <PlusOutlined style={{ color: '#3b82f6' }} />
          <span style={{ color: '#1e293b', fontWeight: '600' }}>Quick Actions</span>
        </Space>
      }
      style={{ height: '100%', border: '1px solid #e2e8f0' }}
    >
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <Button 
          type="primary" 
          icon={<HomeOutlined />} 
          block
          onClick={() => navigate('/admin/parking-lots')}
          style={{ 
            height: '48px', 
            fontSize: '16px',
            background: '#3b82f6',
            borderColor: '#3b82f6',
            fontWeight: '500'
          }}
        >
          Manage Parking Lots
        </Button>
        <Button 
          icon={<CarOutlined />} 
          block
          onClick={() => navigate('/admin/vehicles')}
          style={{ 
            height: '48px', 
            fontSize: '16px',
            color: '#64748b',
            borderColor: '#d1d5db',
            fontWeight: '500'
          }}
        >
          View Vehicles
        </Button>
        <Button 
          icon={<CalendarOutlined />} 
          block
          onClick={() => navigate('/admin/reservations')}
          style={{ 
            height: '48px', 
            fontSize: '16px',
            color: '#64748b',
            borderColor: '#d1d5db',
            fontWeight: '500'
          }}
        >
          Manage Reservations
        </Button>
        <Button 
          icon={<EyeOutlined />} 
          block
          onClick={() => navigate('/admin/analytics')}
          style={{ 
            height: '48px', 
            fontSize: '16px',
            color: '#64748b',
            borderColor: '#d1d5db',
            fontWeight: '500'
          }}
        >
          View Analytics
        </Button>
      </Space>
    </Card>
  );

  const SystemStatus = () => (
    <Card 
      title={
        <Space>
          <CheckCircleOutlined style={{ color: '#10b981' }} />
          <span style={{ color: '#1e293b', fontWeight: '600' }}>System Status</span>
        </Space>
      }
      style={{ border: '1px solid #e2e8f0' }}
    >
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <div style={{ textAlign: 'center' }}>
          <CheckCircleOutlined style={{ fontSize: '48px', color: '#10b981' }} />
          <div style={{ marginTop: '16px' }}>
            <Text strong style={{ fontSize: '18px', color: '#1e293b' }}>All Systems Operational</Text>
            <br />
            <Text type="secondary" style={{ color: '#64748b' }}>{systemStatus}</Text>
          </div>
        </div>
        {statusError && (
          <Alert
            message="Demo Mode"
            description="Running with sample data. Backend connection unavailable."
            type="info"
            showIcon
            style={{ marginTop: '16px' }}
          />
        )}
      </Space>
    </Card>
  );

  const RecentReservations = () => {
    // Get recent reservations (last 5) with useMemo
    const recentReservations = useMemo(() => {
      return allReservations
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5);
    }, [allReservations]);

    const handleRefresh = () => {
      const demoBookings = JSON.parse(localStorage.getItem('demoBookings') || '[]');
      setAllReservations(demoBookings);
    };


    const getStatusColor = (status) => {
      switch (status) {
        case 'CONFIRMED': return 'green';
        case 'CANCELLED': return 'red';
        case 'PENDING': return 'orange';
        case 'COMPLETED': return 'blue';
        default: return 'default';
      }
    };

    const getStatusIcon = (status) => {
      switch (status) {
        case 'CONFIRMED': return <CheckCircleOutlined />;
        case 'CANCELLED': return <ExclamationCircleOutlined />;
        case 'PENDING': return <ClockCircleOutlined />;
        case 'COMPLETED': return <CheckCircleOutlined />;
        default: return <ClockCircleOutlined />;
      }
    };

    return (
      <Card 
        title={
          <Space>
            <ClockCircleOutlined style={{ color: '#3b82f6' }} />
            <span style={{ color: '#1e293b', fontWeight: '600' }}>Recent Reservations</span>
          </Space>
        }
        style={{ border: '1px solid #e2e8f0' }}
        extra={
          <Space>
            <Button 
              type="text" 
              onClick={handleRefresh}
              icon={<ClockCircleOutlined />}
            >
              Refresh
            </Button>
            <Button 
              type="link" 
              onClick={() => navigate('/admin/reservations')}
            >
              View All
            </Button>
          </Space>
        }
      >
        {reservationsLoading ? (
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <Spin />
          </div>
        ) : recentReservations.length === 0 ? (
          <Empty 
            description="No reservations found"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button type="primary" onClick={() => navigate('/admin/reservations')}>
              Manage Reservations
            </Button>
          </Empty>
        ) : (
          <List
            dataSource={recentReservations}
            renderItem={(reservation) => (
              <List.Item
                actions={[
                  <Button type="text" icon={<EyeOutlined />} size="small" />,
                  <Button type="text" icon={<EditOutlined />} size="small" />
                ]}
              >
                <List.Item.Meta
                  avatar={
                    <Avatar 
                      style={{ 
                        backgroundColor: getStatusColor(reservation.status) === 'green' ? '#52c41a' : 
                                        getStatusColor(reservation.status) === 'red' ? '#ff4d4f' :
                                        getStatusColor(reservation.status) === 'orange' ? '#faad14' : '#1890ff'
                      }}
                      icon={getStatusIcon(reservation.status)}
                    />
                  }
                  title={
                    <Space>
                      <Text strong>Reservation #{reservation.id}</Text>
                      <Tag color={getStatusColor(reservation.status)}>
                        {reservation.status}
                      </Tag>
                    </Space>
                  }
                  description={
                    <Space direction="vertical" size="small">
                      <Text type="secondary">
                        User: {reservation.userId} | Vehicle: {reservation.vehicleId}
                      </Text>
                      <Text type="secondary">
                        Amount: ₹{reservation.totalAmount || 0} | 
                        Duration: {reservation.startTime ? new Date(reservation.startTime).toLocaleDateString() : 'N/A'}
                      </Text>
                    </Space>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </Card>
    );
  };

  if (lotsLoading || statusLoading || reservationsLoading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '400px' 
      }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: '32px', background: '#f8fafc', minHeight: '100vh' }}>
      {/* Header */}
      <div style={{ marginBottom: '32px' }}>
        <Title level={2} style={{ margin: 0, color: '#1e293b', fontWeight: '600' }}>
          Dashboard Overview
        </Title>
        <Text type="secondary" style={{ fontSize: '16px', color: '#64748b' }}>
          Monitor your parking management system performance and key metrics.
        </Text>
      </div>

      {/* Statistics Cards */}
      <Row gutter={[24, 24]} style={{ marginBottom: '32px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#f8fafc',
              border: '1px solid #e2e8f0',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Total Slots</span>}
              value={totalSlots}
              prefix={<HomeOutlined style={{ color: '#3b82f6' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#f0fdf4',
              border: '1px solid #bbf7d0',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Available</span>}
              value={availableSlots}
              prefix={<CheckCircleOutlined style={{ color: '#10b981' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#fef2f2',
              border: '1px solid #fecaca',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Occupied</span>}
              value={occupiedSlots}
              prefix={<CarOutlined style={{ color: '#ef4444' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#fefce8',
              border: '1px solid #fde68a',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Active Lots</span>}
              value={activeLots}
              prefix={<CheckCircleOutlined style={{ color: '#eab308' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Reservation Statistics */}
      <Row gutter={[24, 24]} style={{ marginBottom: '32px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#f8fafc',
              border: '1px solid #e2e8f0',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Total Reservations</span>}
              value={totalReservations}
              prefix={<CalendarOutlined style={{ color: '#3b82f6' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#f0fdf4',
              border: '1px solid #bbf7d0',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Confirmed</span>}
              value={confirmedReservations}
              prefix={<CheckCircleOutlined style={{ color: '#10b981' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#fef2f2',
              border: '1px solid #fecaca',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Cancelled</span>}
              value={cancelledReservations}
              prefix={<ExclamationCircleOutlined style={{ color: '#ef4444' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            style={{ 
              background: '#fefce8',
              border: '1px solid #fde68a',
              boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b', fontSize: '14px', fontWeight: '500' }}>Total Revenue</span>}
              value={totalRevenue}
              prefix={<DollarOutlined style={{ color: '#eab308' }} />}
              valueStyle={{ color: '#1e293b', fontSize: '28px', fontWeight: '600' }}
              formatter={(value) => `₹${value}`}
            />
          </Card>
        </Col>
      </Row>

      {/* Occupancy Rate */}
      <Row gutter={[24, 24]} style={{ marginBottom: '32px' }}>
        <Col xs={24} lg={12}>
          <Card title="Occupancy Rate" style={{ height: '100%' }}>
            <div style={{ textAlign: 'center', padding: '20px 0' }}>
              <Progress
                type="circle"
                percent={Math.round(occupancyRate)}
                strokeColor={{
                  '0%': '#3b82f6',
                  '100%': '#1d4ed8',
                }}
                size={120}
                format={(percent) => `${percent}%`}
                strokeWidth={8}
              />
              <div style={{ marginTop: '16px' }}>
                <Text type="secondary">
                  {occupiedSlots} of {totalSlots} slots occupied
                </Text>
              </div>
            </div>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <SystemStatus />
        </Col>
      </Row>

      {/* Main Content */}
      <Row gutter={[24, 24]}>
        <Col xs={24} lg={16}>
          <RecentReservations />
        </Col>
        <Col xs={24} lg={8}>
          <QuickActions />
        </Col>
      </Row>

      {/* Parking Lots Overview */}
      <Row gutter={[24, 24]} style={{ marginTop: '32px' }}>
        <Col span={24}>
          <Card 
            title={
              <Space>
                <HomeOutlined style={{ color: '#3b82f6' }} />
                <span style={{ color: '#1e293b', fontWeight: '600' }}>Parking Lots Overview</span>
              </Space>
            }
            style={{ border: '1px solid #e2e8f0' }}
            extra={
              <Button 
                type="primary" 
                icon={<PlusOutlined />}
                onClick={() => navigate('/admin/parking-lots')}
                style={{
                  background: '#3b82f6',
                  borderColor: '#3b82f6',
                  fontWeight: '500'
                }}
              >
                Add New Lot
              </Button>
            }
          >
            {parkingLots.length === 0 ? (
              <Empty 
                description="No parking lots found"
                image={Empty.PRESENTED_IMAGE_SIMPLE}
              >
                <Button type="primary" onClick={() => navigate('/admin/parking-lots')}>
                  Create First Parking Lot
                </Button>
              </Empty>
            ) : (
              <Row gutter={[16, 16]}>
                {parkingLots.map((lot) => (
                  <Col xs={24} sm={12} lg={8} key={lot.id}>
                    <Card
                      size="small"
                      hoverable
                      style={{ border: '1px solid #e2e8f0' }}
                      actions={[
                        <Button type="text" icon={<EyeOutlined />} style={{ color: '#64748b' }} />,
                        <Button type="text" icon={<EditOutlined />} style={{ color: '#64748b' }} />,
                        <Button type="text" icon={<DeleteOutlined />} danger />,
                      ]}
                    >
                      <div style={{ textAlign: 'center' }}>
                        <HomeOutlined style={{ fontSize: '32px', color: '#3b82f6', marginBottom: '12px' }} />
                        <Title level={5} style={{ margin: '8px 0', color: '#1e293b' }}>{lot.name}</Title>
                        <Text type="secondary" style={{ display: 'block', marginBottom: '12px', color: '#64748b' }}>
                          {lot.location}
                        </Text>
                        <Space direction="vertical" size="small" style={{ width: '100%' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Text style={{ color: '#64748b' }}>Slots:</Text>
                            <Text strong style={{ color: '#1e293b' }}>
                              {(() => {
                                const availability = slotAvailability[lot.id];
                                if (availability) {
                                  const available = (availability['2W'] || 0) + (availability['4W'] || 0);
                                  const total = (lot.totalSlots2W || 0) + (lot.totalSlots4W || 0);
                                  return `${available}/${total}`;
                                }
                                const total = (lot.totalSlots2W || 0) + (lot.totalSlots4W || 0);
                                return `${total}/${total}`; // Fallback to total slots if no real-time data
                              })()}
                            </Text>
                          </div>
                          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Text style={{ color: '#64748b' }}>Rate:</Text>
                            <Text strong style={{ color: '#1e293b' }}>${lot.hourlyRate}/hr</Text>
                          </div>
                          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Text style={{ color: '#64748b' }}>Status:</Text>
                            <Tag color={getStatusColor(lot.status)}>{lot.status}</Tag>
                          </div>
                        </Space>
                      </div>
                    </Card>
                  </Col>
                ))}
              </Row>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default AdminDashboard;