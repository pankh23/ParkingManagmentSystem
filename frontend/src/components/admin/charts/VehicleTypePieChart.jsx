import React from 'react';
import { Card, Spin, Alert } from 'antd';
import { useQuery } from 'react-query';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip,
  Legend
} from 'recharts';
import { getVehicleTypeDistribution } from '../../../services/api';

const VehicleTypePieChart = () => {
  const { data, isLoading, error } = useQuery('vehicleTypeDistribution', getVehicleTypeDistribution, {
    refetchInterval: 60000,
    staleTime: 30000,
  });

  if (isLoading) {
    return (
      <Card title="Vehicle Type Distribution">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card title="Vehicle Type Distribution">
        <Alert
          message="Error Loading Data"
          description="Failed to load vehicle type distribution data. Please try again later."
          type="error"
          showIcon
        />
      </Card>
    );
  }

  // Transform API response to chart format
  const chartData = [
    { 
      name: '2 Wheeler', 
      value: data?.twoWheelerCount || 0,
      percentage: data?.twoWheelerPercentage || 0,
      color: '#1890ff'
    },
    { 
      name: '4 Wheeler', 
      value: data?.fourWheelerCount || 0,
      percentage: data?.fourWheelerPercentage || 0,
      color: '#52c41a'
    }
  ];

  const COLORS = ['#1890ff', '#52c41a'];

  return (
    <Card title="Vehicle Type Distribution">
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percentage }) => `${name} ${percentage.toFixed(1)}%`}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip 
            formatter={(value, name) => [value, name]}
          />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default VehicleTypePieChart;

