import React from 'react';
import { Card, Spin, Alert } from 'antd';
import { useQuery } from 'react-query';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import { getPeakHours } from '../../../services/api';

const PeakHoursChart = () => {
  const { data, isLoading, error } = useQuery('peakHours', getPeakHours, {
    refetchInterval: 30000, // Refetch every 30 seconds
    staleTime: 15000, // Consider data stale after 15 seconds
  });

  if (isLoading) {
    return (
      <Card title="Peak Hours Analysis">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card title="Peak Hours Analysis">
        <Alert
          message="Error Loading Data"
          description="Failed to load peak hours data. Please try again later."
          type="error"
          showIcon
        />
      </Card>
    );
  }

  // Transform API response to chart format
  const chartData = data?.peakHours?.map(item => ({
    hour: item.hour,
    bookings: item.bookingCount || 0
  })) || [];

  return (
    <Card title="Peak Hours Analysis">
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis 
            dataKey="hour" 
            tick={{ fontSize: 12 }}
            angle={-45}
            textAnchor="end"
            height={60}
          />
          <YAxis />
          <Tooltip 
            formatter={(value) => [value, 'Bookings']}
            labelStyle={{ color: '#1890ff' }}
          />
          <Line 
            type="monotone" 
            dataKey="bookings" 
            stroke="#52c41a" 
            strokeWidth={2}
            dot={{ fill: '#52c41a', r: 4 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default PeakHoursChart;

