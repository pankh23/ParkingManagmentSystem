import React, { useState } from 'react';
import {
  Table,
  Button,
  Input,
  Select,
  Space,
  Card,
  Typography,
  Row,
  Col,
  Statistic,
  Tag,
  Modal
} from 'antd';
import {
  CarOutlined,
  SearchOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { useQuery } from 'react-query';
import { getTransactionHistory, getLatestVehicleStatus } from '../../services/api';

const { Title } = Typography;
const { Option } = Select;

const VehiclesManagement = () => {
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  useQuery(
    'transactionHistory',
    getTransactionHistory
  );

  const { data: latestStatus = [], isLoading: statusLoading } = useQuery(
    'latestVehicleStatus',
    getLatestVehicleStatus
  );

  // Get unique vehicles from transactions
  const vehicles = latestStatus.map(transaction => ({
    id: transaction.vehicle?.id,
    type: transaction.vehicle?.type,
    plateNumber: transaction.vehicle?.plateNumber,
    parked: transaction.vehicle?.parked,
    slot: transaction.slot?.slotNumber,
    parkingLot: transaction.slot?.parkingLot?.name,
    lastAction: transaction.action,
    lastUpdate: transaction.timestamp
  }));

  const filteredVehicles = vehicles.filter(vehicle => {
    const matchesSearch = !searchText || 
      vehicle.id?.toString().includes(searchText) ||
      vehicle.plateNumber?.toLowerCase().includes(searchText.toLowerCase());
    
    const matchesStatus = statusFilter === 'all' || 
      (statusFilter === 'parked' && vehicle.parked) ||
      (statusFilter === 'not_parked' && !vehicle.parked);
    
    return matchesSearch && matchesStatus;
  });

  const handleViewDetails = (vehicle) => {
    setSelectedVehicle(vehicle);
    setShowDetailModal(true);
  };

  const columns = [
    {
      title: 'Vehicle ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (type) => (
        <Tag color={type === '2W' ? 'blue' : 'green'}>
          {type}
        </Tag>
      ),
    },
    {
      title: 'Plate Number',
      dataIndex: 'plateNumber',
      key: 'plateNumber',
    },
    {
      title: 'Status',
      key: 'status',
      render: (_, record) => (
        <Tag color={record.parked ? 'green' : 'default'}>
          {record.parked ? 'Parked' : 'Not Parked'}
        </Tag>
      ),
    },
    {
      title: 'Current Slot',
      dataIndex: 'slot',
      key: 'slot',
      render: (slot, record) => slot ? `${slot} (${record.parkingLot})` : 'N/A',
    },
    {
      title: 'Last Action',
      dataIndex: 'lastAction',
      key: 'lastAction',
      render: (action) => (
        <Tag color={action === 'PARKED' ? 'green' : 'red'}>
          {action}
        </Tag>
      ),
    },
    {
      title: 'Last Update',
      dataIndex: 'lastUpdate',
      key: 'lastUpdate',
      render: (timestamp) => new Date(timestamp).toLocaleString(),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Button
            type="primary"
            icon={<EyeOutlined />}
            size="small"
            onClick={() => handleViewDetails(record)}
          >
            View
          </Button>
        </Space>
      ),
    },
  ];

  const parkedVehicles = vehicles.filter(v => v.parked).length;
  const totalVehicles = vehicles.length;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>Vehicles Management</Title>
        <Title level={4} type="secondary">Monitor and manage all vehicles in the system</Title>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Vehicles"
              value={totalVehicles}
              prefix={<CarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Currently Parked"
              value={parkedVehicles}
              prefix={<CarOutlined style={{ color: '#52c41a' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Available Slots"
              value={0}
              prefix={<CarOutlined style={{ color: '#1890ff' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Wait Queue"
              value={0}
              prefix={<CarOutlined style={{ color: '#faad14' }} />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <div style={{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>All Vehicles</Title>
          <Space>
            <Input
              placeholder="Search by ID or plate number"
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ width: 200 }}
            />
            <Select
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 150 }}
            >
              <Option value="all">All Status</Option>
              <Option value="parked">Parked</Option>
              <Option value="not_parked">Not Parked</Option>
            </Select>
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={filteredVehicles}
          loading={statusLoading}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} vehicles`,
          }}
        />
      </Card>

      <Modal
        title="Vehicle Details"
        open={showDetailModal}
        onCancel={() => setShowDetailModal(false)}
        footer={[
          <Button key="close" onClick={() => setShowDetailModal(false)}>
            Close
          </Button>
        ]}
        width={600}
      >
        {selectedVehicle && (
          <div>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <p><strong>Vehicle ID:</strong> {selectedVehicle.id}</p>
                <p><strong>Type:</strong> {selectedVehicle.type}</p>
                <p><strong>Plate Number:</strong> {selectedVehicle.plateNumber || 'N/A'}</p>
                <p><strong>Status:</strong> 
                  <Tag color={selectedVehicle.parked ? 'green' : 'default'} style={{ marginLeft: 8 }}>
                    {selectedVehicle.parked ? 'Parked' : 'Not Parked'}
                  </Tag>
                </p>
              </Col>
              <Col span={12}>
                <p><strong>Current Slot:</strong> {selectedVehicle.slot || 'N/A'}</p>
                <p><strong>Parking Lot:</strong> {selectedVehicle.parkingLot || 'N/A'}</p>
                <p><strong>Last Action:</strong> {selectedVehicle.lastAction}</p>
                <p><strong>Last Update:</strong> {new Date(selectedVehicle.lastUpdate).toLocaleString()}</p>
              </Col>
            </Row>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default VehiclesManagement;
