import React from 'react';
import { Row, Col, Typography } from 'antd';
import SummaryCards from './charts/SummaryCards';
import PeakHoursChart from './charts/PeakHoursChart';
import MonthlyRevenueChart from './charts/MonthlyRevenueChart';
import VehicleTypePieChart from './charts/VehicleTypePieChart';
import OccupancyStatusCard from './charts/OccupancyStatusCard';
import BookingTrendChart from './charts/BookingTrendChart';
import TopLotsTable from './charts/TopLotsTable';

const { Title } = Typography;

const Analytics = () => {
  return (
    <div style={{ padding: '32px', background: '#f8fafc', minHeight: '100vh' }}>
      <div style={{ marginBottom: '32px' }}>
        <Title level={2} style={{ margin: 0, color: '#1e293b', fontWeight: '600' }}>
          Analytics Dashboard
        </Title>
        <Title level={4} type="secondary" style={{ marginTop: '8px', color: '#64748b', fontWeight: '400' }}>
          Comprehensive insights into your parking system performance
        </Title>
      </div>

      {/* Summary Cards */}
      <div style={{ marginBottom: '32px' }}>
        <SummaryCards />
      </div>

      {/* Charts Row 1 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={12}>
          <MonthlyRevenueChart />
        </Col>
        <Col xs={24} lg={12}>
          <VehicleTypePieChart />
        </Col>
      </Row>

      {/* Charts Row 2 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={12}>
          <PeakHoursChart />
        </Col>
        <Col xs={24} lg={12}>
          <OccupancyStatusCard />
        </Col>
      </Row>

      {/* Booking Trend */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24}>
          <BookingTrendChart />
        </Col>
      </Row>

      {/* Top Performing Lots */}
      <Row gutter={[16, 16]}>
        <Col xs={24}>
          <TopLotsTable />
        </Col>
      </Row>
    </div>
  );
};

export default Analytics;
