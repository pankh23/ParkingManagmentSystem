import React from 'react';
import { Card, Spin, Alert } from 'antd';
import { useQuery } from 'react-query';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import { getBookingTrend } from '../../../services/api';

const BookingTrendChart = () => {
  const { data, isLoading, error } = useQuery('bookingTrend', getBookingTrend, {
    refetchInterval: 60000,
    staleTime: 30000,
  });

  if (isLoading) {
    return (
      <Card title="Booking Trend (Last 30 Days)">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card title="Booking Trend (Last 30 Days)">
        <Alert
          message="Error Loading Data"
          description="Failed to load booking trend data. Please try again later."
          type="error"
          showIcon
        />
      </Card>
    );
  }

  // Transform API response to chart format
  const chartData = data?.dailyBookings?.map(item => ({
    date: new Date(item.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
    bookings: item.bookingCount || 0
  })) || [];

  return (
    <Card title="Booking Trend (Last 30 Days)">
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis 
            dataKey="date" 
            tick={{ fontSize: 12 }}
            angle={-45}
            textAnchor="end"
            height={80}
          />
          <YAxis />
          <Tooltip 
            formatter={(value) => [value, 'Bookings']}
            labelStyle={{ color: '#1890ff' }}
          />
          <Bar dataKey="bookings" fill="#52c41a" />
        </BarChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default BookingTrendChart;

