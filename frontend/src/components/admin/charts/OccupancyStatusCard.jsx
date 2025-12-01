import React from 'react';
import { Card, Spin, Alert, Progress } from 'antd';
import { useQuery } from 'react-query';
import { getDailyOccupancy } from '../../../services/api';

const OccupancyStatusCard = () => {
  const { data, isLoading, error } = useQuery('dailyOccupancy', getDailyOccupancy, {
    refetchInterval: 30000,
    staleTime: 15000,
  });

  if (isLoading) {
    return (
      <Card title="Lot Occupancy Status">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Spin size="large" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card title="Lot Occupancy Status">
        <Alert
          message="Error Loading Data"
          description="Failed to load occupancy data. Please try again later."
          type="error"
          showIcon
        />
      </Card>
    );
  }

  // Get today's occupancy for each lot
  const today = new Date().toISOString().split('T')[0];
  const occupancyData = data?.lotOccupancyData?.map(lot => {
    const todayData = lot.dailyOccupancy?.find(day => day.date === today);
    return {
      name: lot.lotName,
      occupancy: todayData?.occupiedSlots || 0,
      capacity: todayData?.totalSlots || 0,
      percentage: todayData?.occupancyPercentage || 0
    };
  }).filter(lot => lot.capacity > 0) || [];

  return (
    <Card title="Lot Occupancy Status">
      <div style={{ padding: '16px 0' }}>
        {occupancyData.length === 0 ? (
          <Alert
            message="No Data Available"
            description="No occupancy data available for today."
            type="info"
            showIcon
          />
        ) : (
          occupancyData.map((lot, index) => (
            <div key={index} style={{ marginBottom: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                <span style={{ fontWeight: 500 }}>{lot.name}</span>
                <span>
                  {lot.occupancy}/{lot.capacity} ({lot.percentage.toFixed(1)}%)
                </span>
              </div>
              <Progress
                percent={lot.percentage}
                strokeColor={
                  lot.percentage > 80 ? '#ff4d4f' : 
                  lot.percentage > 60 ? '#faad14' : 
                  '#52c41a'
                }
                showInfo={false}
              />
            </div>
          ))
        )}
      </div>
    </Card>
  );
};

export default OccupancyStatusCard;

