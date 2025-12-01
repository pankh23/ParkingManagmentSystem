import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { CarOutlined, UserOutlined, LogoutOutlined, DashboardOutlined, CalendarOutlined, CarFilled } from '@ant-design/icons';
import { Dropdown, Avatar } from 'antd';
import { useAuth } from '../context/AuthContext';

const Sidebar = ({ isCollapsed, onMenuToggle }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userMenuItems = [
    {
      key: 'user-info',
      label: (
        <div className="flex items-center p-2">
          <Avatar size="small" className="mr-3 bg-blue-600">
            <UserOutlined />
          </Avatar>
          <div>
            <div className="font-semibold text-gray-800">{user?.username}</div>
            <div className="text-xs text-gray-500 capitalize">{user?.role}</div>
          </div>
        </div>
      ),
      disabled: true,
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      label: (
        <div className="flex items-center px-3 py-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors duration-200">
          <LogoutOutlined className="mr-3" />
          <span className="font-semibold">Logout</span>
        </div>
      ),
      onClick: handleLogout,
    },
  ];

  const menuItems = [
    {
      key: '/user',
      icon: <DashboardOutlined className="text-lg" />,
      label: 'Dashboard',
      path: '/user'
    },
    {
      key: '/user/book-slot',
      icon: <CalendarOutlined className="text-lg" />,
      label: 'Book Slot',
      path: '/user/book-slot'
    },
    {
      key: '/user/my-reservations',
      icon: <CarFilled className="text-lg" />,
      label: 'My Reservations',
      path: '/user/my-reservations'
    }
  ];

  const isActive = (path) => {
    if (path === '/user') {
      // For dashboard, only match exact path
      return location.pathname === path;
    }
    // For other paths, match exact path or sub-paths
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <aside className={`hidden lg:block bg-white shadow-md border-r border-gray-200 h-screen transition-all duration-300 ${
      isCollapsed ? 'w-20' : 'w-64'
    }`}>
      <div className="h-full flex flex-col">
        {/* Logo Section */}
        <div className="p-6 border-b border-gray-200">
          <div className="flex items-center">
            <div className="w-12 h-12 bg-blue-600 rounded-lg flex items-center justify-center mr-4">
              <CarOutlined className="text-white text-xl" />
            </div>
            {!isCollapsed && (
              <h1 className="text-xl font-bold text-gray-800">ParkingApp</h1>
            )}
          </div>
        </div>

        {/* Navigation Menu */}
        <div className="flex-1 p-4">
          <div className="space-y-2">
            {menuItems.map((item) => (
              <button
                key={item.key}
                onClick={() => navigate(item.path)}
                className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                  isActive(item.path)
                    ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                    : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                }`}
              >
                <span className={`mr-3 text-gray-500 ${
                  isCollapsed ? 'mx-auto' : ''
                }`}>
                  {item.icon}
                </span>
                {!isCollapsed && (
                  <span className="font-medium text-gray-700">
                    {item.label}
                  </span>
                )}
              </button>
            ))}
          </div>
        </div>

        {/* User Section */}
        <div className="p-4 border-t border-gray-200">
          <Dropdown menu={{ items: userMenuItems }} trigger={['click']} placement="topRight">
            <div className="flex items-center p-3 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors duration-200">
              <Avatar 
                size="large" 
                className="bg-blue-600"
              >
                <UserOutlined />
              </Avatar>
              {!isCollapsed && (
                <div className="ml-4 flex-1">
                  <div className="text-gray-800 font-semibold text-lg">{user?.username}</div>
                  <div className="text-gray-500 text-sm capitalize">{user?.role}</div>
                </div>
              )}
            </div>
          </Dropdown>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
