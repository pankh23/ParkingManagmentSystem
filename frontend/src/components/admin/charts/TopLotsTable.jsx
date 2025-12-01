import React from 'react';
import { Card, Table, Spin, Alert } from 'antd';
import { useQuery } from 'react-query';
import { getTopLots } from '../../../services/api';

const TopLotsTable = () => {
  const { data, isLoading, error } = useQuery('topLots', getTopLots, {
    refetchInterval: 60000,
    staleTime: 30000,
  });

  const columns = [
    {
      title: 'Parking Lot',
      dataIndex: 'lotName',
      key: 'lotName',
    },
    {
      title: 'Revenue',
      dataIndex: 'totalRevenue',
      key: 'totalRevenue',
      render: (value) => `₹${Number(value).toLocaleString('en-IN')}`,
      sorter: (a, b) => a.totalRevenue - b.totalRevenue,
      defaultSortOrder: 'descend',
    },
    {
      title: 'Bookings',
      dataIndex: 'totalBookings',
      key: 'totalBookings',
      sorter: (a, b) => a.totalBookings - b.totalBookings,
    },
    {
      title: 'Avg Revenue/Booking',
      key: 'avgRevenue',
      render: (_, record) => {
        const avg = record.totalBookings > 0 
          ? record.totalRevenue / record.totalBookings 
          : 0;
        return `₹${Math.round(avg).toLocaleString('en-IN')}`;
      },
      sorter: (a, b) => {
        const avgA = a.totalBookings > 0 ? a.totalRevenue / a.totalBookings : 0;
        const avgB = b.totalBookings > 0 ? b.totalRevenue / b.totalBookings : 0;
        return avgA - avgB;
      },
    },
  ];

  if (isLoading) {
    return (
      <Card title="Top Performing Parking Lots">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card title="Top Performing Parking Lots">
        <Alert
          message="Error Loading Data"
          description="Failed to load top lots data. Please try again later."
          type="error"
          showIcon
        />
      </Card>
    );
  }

  const tableData = data?.topLots?.map((lot, index) => ({
    key: lot.lotId || index,
    lotName: lot.lotName,
    totalRevenue: lot.totalRevenue || 0,
    totalBookings: lot.totalBookings || 0,
    averageRevenuePerBooking: lot.averageRevenuePerBooking || 0,
  })) || [];

  return (
    <Card title="Top Performing Parking Lots">
      <Table
        columns={columns}
        dataSource={tableData}
        pagination={false}
        size="middle"
      />
    </Card>
  );
};

export default TopLotsTable;

