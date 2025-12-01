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
import { getMonthlyRevenue } from '../../../services/api';

const MonthlyRevenueChart = () => {
  const { data, isLoading, error } = useQuery('monthlyRevenue', getMonthlyRevenue, {
    refetchInterval: 60000, // Refetch every minute
    staleTime: 30000,
  });

  if (isLoading) {
    return (
      <Card title="Monthly Revenue Trend">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card title="Monthly Revenue Trend">
        <Alert
          message="Error Loading Data"
          description="Failed to load monthly revenue data. Please try again later."
          type="error"
          showIcon
        />
      </Card>
    );
  }

  // Transform API response to chart format
  const chartData = data?.monthlyRevenue?.map(item => ({
    month: item.month,
    revenue: item.revenue || 0
  })) || [];

  return (
    <Card title="Monthly Revenue Trend">
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip 
            formatter={(value) => [`₹${value.toLocaleString('en-IN')}`, 'Revenue']}
            labelStyle={{ color: '#1890ff' }}
          />
          <Line 
            type="monotone" 
            dataKey="revenue" 
            stroke="#1890ff" 
            strokeWidth={2}
            dot={{ fill: '#1890ff', r: 4 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default MonthlyRevenueChart;

