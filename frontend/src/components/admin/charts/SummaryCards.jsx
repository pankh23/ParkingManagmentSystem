import React from 'react';
import { Card, Row, Col, Statistic, Spin, Alert } from 'antd';
import { useQuery } from 'react-query';
import {
  DollarOutlined,
  CalendarOutlined,
  HomeOutlined,
  RiseOutlined
} from '@ant-design/icons';
import { getAnalyticsSummary } from '../../../services/api';

const SummaryCards = () => {
  const { data, isLoading, error } = useQuery('analyticsSummary', getAnalyticsSummary, {
    refetchInterval: 30000,
    staleTime: 15000,
  });

  if (isLoading) {
    return (
      <Row gutter={[16, 16]}>
        {[1, 2, 3, 4].map(i => (
          <Col xs={24} sm={12} lg={6} key={i}>
            <Card>
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 100 }}>
                <Spin />
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    );
  }

  if (error) {
    return (
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Alert
            message="Error Loading Summary"
            description="Failed to load analytics summary. Please try again later."
            type="error"
            showIcon
          />
        </Col>
      </Row>
    );
  }

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24} sm={12} lg={6}>
        <Card>
          <Statistic
            title="Total Revenue"
            value={data?.totalRevenue || 0}
            prefix={<DollarOutlined />}
            precision={2}
            valueStyle={{ color: '#52c41a' }}
            formatter={(value) => `₹${Number(value).toLocaleString('en-IN')}`}
          />
        </Card>
      </Col>
      <Col xs={24} sm={12} lg={6}>
        <Card>
          <Statistic
            title="Total Bookings"
            value={data?.totalBookings || 0}
            prefix={<CalendarOutlined />}
            valueStyle={{ color: '#1890ff' }}
          />
        </Card>
      </Col>
      <Col xs={24} sm={12} lg={6}>
        <Card>
          <Statistic
            title="Average Occupancy"
            value={data?.averageOccupancy || 0}
            suffix="%"
            prefix={<HomeOutlined />}
            precision={1}
            valueStyle={{ color: '#faad14' }}
          />
        </Card>
      </Col>
      <Col xs={24} sm={12} lg={6}>
        <Card>
          <Statistic
            title="Growth Rate"
            value={data?.growthRatePercent || 0}
            suffix="%"
            prefix={<RiseOutlined />}
            precision={1}
            valueStyle={{ 
              color: (data?.growthRatePercent || 0) >= 0 ? '#52c41a' : '#ff4d4f' 
            }}
          />
        </Card>
      </Col>
    </Row>
  );
};

export default SummaryCards;

