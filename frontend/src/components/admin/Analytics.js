import React from 'react';
import {
  Card,
  Row,
  Col,
  Typography,
  Statistic,
  Table,
  Progress
} from 'antd';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line
} from 'recharts';
import {
  DollarOutlined,
  CalendarOutlined,
  HomeOutlined
} from '@ant-design/icons';

const { Title } = Typography;

const Analytics = () => {
  // Mock data - in real app, this would come from API
  const occupancyData = [
    { name: 'Lot A', occupancy: 85, capacity: 100 },
    { name: 'Lot B', occupancy: 60, capacity: 80 },
    { name: 'Lot C', occupancy: 90, capacity: 120 },
    { name: 'Lot D', occupancy: 45, capacity: 60 },
  ];

  const revenueData = [
    { month: 'Jan', revenue: 12000 },
    { month: 'Feb', revenue: 15000 },
    { month: 'Mar', revenue: 18000 },
    { month: 'Apr', revenue: 16000 },
    { month: 'May', revenue: 20000 },
    { month: 'Jun', revenue: 22000 },
  ];

  const vehicleTypeData = [
    { name: '2 Wheeler', value: 65, color: '#1890ff' },
    { name: '4 Wheeler', value: 35, color: '#52c41a' },
  ];

  const peakHoursData = [
    { hour: '8:00', bookings: 12 },
    { hour: '9:00', bookings: 25 },
    { hour: '10:00', bookings: 18 },
    { hour: '11:00', bookings: 15 },
    { hour: '12:00', bookings: 22 },
    { hour: '13:00', bookings: 8 },
    { hour: '14:00', bookings: 10 },
    { hour: '15:00', bookings: 16 },
    { hour: '16:00', bookings: 28 },
    { hour: '17:00', bookings: 35 },
    { hour: '18:00', bookings: 30 },
    { hour: '19:00', bookings: 20 },
  ];

  const topLotsData = [
    { lot: 'Lot A', revenue: 15000, bookings: 120 },
    { lot: 'Lot B', revenue: 12000, bookings: 95 },
    { lot: 'Lot C', revenue: 18000, bookings: 140 },
    { lot: 'Lot D', revenue: 8000, bookings: 60 },
  ];

  const columns = [
    {
      title: 'Parking Lot',
      dataIndex: 'lot',
      key: 'lot',
    },
    {
      title: 'Revenue',
      dataIndex: 'revenue',
      key: 'revenue',
      render: (value) => `₹${value.toLocaleString()}`,
    },
    {
      title: 'Bookings',
      dataIndex: 'bookings',
      key: 'bookings',
    },
    {
      title: 'Avg Revenue/Booking',
      key: 'avgRevenue',
      render: (_, record) => `₹${Math.round(record.revenue / record.bookings)}`,
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>Analytics Dashboard</Title>
        <Title level={4} type="secondary">Comprehensive insights into your parking system performance</Title>
      </div>

      {/* Key Metrics */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Revenue"
              value={125000}
              prefix={<DollarOutlined />}
              precision={2}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Bookings"
              value={1250}
              prefix={<CalendarOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Average Occupancy"
              value={75}
              suffix="%"
              prefix={<HomeOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Growth Rate"
              value={12.5}
              suffix="%"
              prefix={<DollarOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      {/* Charts Row 1 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={12}>
          <Card title="Monthly Revenue Trend">
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={revenueData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => [`₹${value.toLocaleString()}`, 'Revenue']} />
                <Line type="monotone" dataKey="revenue" stroke="#1890ff" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="Vehicle Type Distribution">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={vehicleTypeData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {vehicleTypeData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>

      {/* Charts Row 2 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={12}>
          <Card title="Peak Hours Analysis">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={peakHoursData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="hour" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="bookings" fill="#52c41a" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="Lot Occupancy Status">
            <div style={{ padding: '16px 0' }}>
              {occupancyData.map((lot, index) => (
                <div key={index} style={{ marginBottom: '16px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                    <span>{lot.name}</span>
                    <span>{lot.occupancy}/{lot.capacity} ({Math.round((lot.occupancy/lot.capacity) * 100)}%)</span>
                  </div>
                  <Progress
                    percent={Math.round((lot.occupancy/lot.capacity) * 100)}
                    strokeColor={lot.occupancy > 80 ? '#ff4d4f' : lot.occupancy > 60 ? '#faad14' : '#52c41a'}
                  />
                </div>
              ))}
            </div>
          </Card>
        </Col>
      </Row>

      {/* Top Performing Lots */}
      <Row gutter={[16, 16]}>
        <Col xs={24}>
          <Card title="Top Performing Parking Lots">
            <Table
              columns={columns}
              dataSource={topLotsData}
              rowKey="lot"
              pagination={false}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Analytics;
