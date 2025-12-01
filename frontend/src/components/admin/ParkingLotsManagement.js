import React, { useState } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Space,
  Popconfirm,
  message,
  Card,
  Typography,
  Row,
  Col,
  Tag,
  Statistic
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  HomeOutlined,
  CarOutlined
} from '@ant-design/icons';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import {
  getParkingLots,
  createParkingLot,
  updateParkingLot,
  deleteParkingLot
} from '../../services/api';

const { Title } = Typography;

const ParkingLotsManagement = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingLot, setEditingLot] = useState(null);
  const [form] = Form.useForm();
  const queryClient = useQueryClient();

  const { data: parkingLots = [], isLoading } = useQuery('parkingLots', getParkingLots);

  const createMutation = useMutation(createParkingLot, {
    onSuccess: () => {
      queryClient.invalidateQueries('parkingLots');
      message.success('Parking lot created successfully!');
      setIsModalVisible(false);
      form.resetFields();
    },
    onError: () => {
      message.error('Failed to create parking lot');
    }
  });

  const updateMutation = useMutation(
    ({ id, data }) => updateParkingLot(id, data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('parkingLots');
        message.success('Parking lot updated successfully!');
        setIsModalVisible(false);
        setEditingLot(null);
        form.resetFields();
      },
      onError: () => {
        message.error('Failed to update parking lot');
      }
    }
  );

  const deleteMutation = useMutation(deleteParkingLot, {
    onSuccess: () => {
      queryClient.invalidateQueries('parkingLots');
      message.success('Parking lot deleted successfully!');
    },
    onError: () => {
      message.error('Failed to delete parking lot');
    }
  });

  const handleCreate = () => {
    setEditingLot(null);
    setIsModalVisible(true);
    form.resetFields();
  };

  const handleEdit = (record) => {
    setEditingLot(record);
    setIsModalVisible(true);
    form.setFieldsValue(record);
  };

  const handleDelete = (id) => {
    deleteMutation.mutate(id);
  };

  const handleModalOk = () => {
    form.validateFields().then((values) => {
      if (editingLot) {
        updateMutation.mutate({ id: editingLot.id, data: values });
      } else {
        createMutation.mutate(values);
      }
    });
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
    setEditingLot(null);
    form.resetFields();
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Location',
      dataIndex: 'location',
      key: 'location',
    },
    {
      title: '2W Slots',
      dataIndex: 'totalSlots2W',
      key: 'totalSlots2W',
      render: (value) => (
        <Tag icon={<CarOutlined />} color="blue">
          {value}
        </Tag>
      ),
    },
    {
      title: '4W Slots',
      dataIndex: 'totalSlots4W',
      key: 'totalSlots4W',
      render: (value) => (
        <Tag icon={<CarOutlined />} color="green">
          {value}
        </Tag>
      ),
    },
    {
      title: '2W Price/Hour',
      dataIndex: 'pricePerHour2W',
      key: 'pricePerHour2W',
      render: (value) => `₹${value}`,
    },
    {
      title: '4W Price/Hour',
      dataIndex: 'pricePerHour4W',
      key: 'pricePerHour4W',
      render: (value) => `₹${value}`,
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (isActive) => (
        <Tag color={isActive ? 'green' : 'red'}>
          {isActive ? 'Active' : 'Inactive'}
        </Tag>
      ),
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
            className="admin-action-button status-button"
            onClick={() => {
              Modal.info({
                title: 'Parking Lot Status',
                content: <div>Loading status...</div>,
                width: 600,
              });
            }}
          >
            Status
          </Button>
          <Button
            type="default"
            icon={<EditOutlined />}
            size="small"
            className="admin-action-button edit-button"
            onClick={() => handleEdit(record)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Are you sure you want to delete this parking lot?"
            onConfirm={() => handleDelete(record.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button
              type="primary"
              danger
              icon={<DeleteOutlined />}
              size="small"
              className="admin-action-button delete-button"
            >
              Delete
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const totalSlots = parkingLots.reduce((sum, lot) => sum + (lot.totalSlots2W || 0) + (lot.totalSlots4W || 0), 0);
  const activeLots = parkingLots.filter(lot => lot.isActive).length;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>Parking Lots Management</Title>
        <Title level={4} type="secondary">Manage your parking lots, slots, and pricing</Title>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Lots"
              value={parkingLots.length}
              prefix={<HomeOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Active Lots"
              value={activeLots}
              prefix={<HomeOutlined style={{ color: '#52c41a' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Slots"
              value={totalSlots}
              prefix={<CarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Average Price"
              value={0}
              prefix="₹"
              precision={2}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        <div style={{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>All Parking Lots</Title>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreate}
            className="admin-add-button"
            style={{
              backgroundColor: '#1890ff',
              borderColor: '#1890ff',
              color: '#ffffff',
              fontWeight: '500',
              boxShadow: '0 2px 4px rgba(24, 144, 255, 0.2)',
              opacity: 1,
              visibility: 'visible'
            }}
          >
            Add New Lot
          </Button>
        </div>

        <Table
          columns={columns}
          dataSource={parkingLots}
          loading={isLoading}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `${range[0]}-${range[1]} of ${total} lots`,
          }}
        />
      </Card>

      <Modal
        title={editingLot ? 'Edit Parking Lot' : 'Create New Parking Lot'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        width={600}
        confirmLoading={createMutation.isLoading || updateMutation.isLoading}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            pricePerHour2W: 10.0,
            pricePerHour4W: 20.0,
          }}
        >
          <Form.Item
            name="name"
            label="Parking Lot Name"
            rules={[{ required: true, message: 'Please enter parking lot name!' }]}
          >
            <Input placeholder="Enter parking lot name" />
          </Form.Item>

          <Form.Item
            name="location"
            label="Location"
            rules={[{ required: true, message: 'Please enter location!' }]}
          >
            <Input placeholder="Enter location" />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="totalSlots2W"
                label="2W Slots"
                rules={[{ required: true, message: 'Please enter number of 2W slots!' }]}
              >
                <InputNumber
                  min={0}
                  style={{ width: '100%' }}
                  placeholder="Number of 2W slots"
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="totalSlots4W"
                label="4W Slots"
                rules={[{ required: true, message: 'Please enter number of 4W slots!' }]}
              >
                <InputNumber
                  min={0}
                  style={{ width: '100%' }}
                  placeholder="Number of 4W slots"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="pricePerHour2W"
                label="2W Price/Hour (₹)"
                rules={[{ required: true, message: 'Please enter 2W price!' }]}
              >
                <InputNumber
                  min={0}
                  step={0.1}
                  style={{ width: '100%' }}
                  placeholder="Price per hour for 2W"
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="pricePerHour4W"
                label="4W Price/Hour (₹)"
                rules={[{ required: true, message: 'Please enter 4W price!' }]}
              >
                <InputNumber
                  min={0}
                  step={0.1}
                  style={{ width: '100%' }}
                  placeholder="Price per hour for 4W"
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default ParkingLotsManagement;
