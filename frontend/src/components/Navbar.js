import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { MenuOutlined, UserOutlined, LogoutOutlined, CarOutlined } from '@ant-design/icons';
import { Dropdown, Avatar } from 'antd';
import { useAuth } from '../context/AuthContext';

const Navbar = ({ onMenuToggle, isCollapsed }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  
  const isAdmin = user?.role === 'ADMIN';

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

  const getPageTitle = () => {
    const path = location.pathname;
    
    // User routes
    if (path === '/user') return 'Dashboard';
    if (path === '/user/book-slot') return 'Book Parking Slot';
    if (path === '/user/my-reservations') return 'My Reservations';
    
    // Admin routes
    if (path === '/admin') return 'Admin Dashboard';
    if (path === '/admin/parking-lots') return 'Parking Lots Management';
    if (path === '/admin/reservations') return 'Reservations Management';
    if (path === '/admin/analytics') return 'Analytics Dashboard';
    if (path === '/admin/users') return 'Users Management';
    if (path === '/admin/settings') return 'Settings';
    
    return 'Parking Management';
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-white border-b border-gray-200">
      <div className="flex justify-between items-center px-6 py-4">
        {/* Left side - Logo and Menu Toggle */}
        <div className="flex items-center">
          {/* Desktop Menu Toggle */}
          <button
            onClick={onMenuToggle}
            className="hidden lg:block mr-6 p-2 hover:bg-gray-100 rounded-lg text-gray-600 hover:text-blue-600 transition-colors duration-200"
          >
            <MenuOutlined className="text-lg" />
          </button>

          {/* Mobile Menu Toggle */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="lg:hidden mr-4 p-2 hover:bg-gray-100 rounded-lg text-gray-600 hover:text-blue-600 transition-colors duration-200"
          >
            <MenuOutlined className="text-lg" />
          </button>

          {/* Logo */}
          <div className="flex items-center">
            <div className="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center mr-3">
              <CarOutlined className="text-white text-lg" />
            </div>
            <h1 className="text-xl font-bold text-gray-800 hidden sm:block">
              ParkingApp
            </h1>
          </div>
        </div>

        {/* Center - Page Title (Desktop) */}
        <div className="hidden md:block">
          <h2 className="text-xl font-bold text-gray-800">
            {getPageTitle()}
          </h2>
        </div>

        {/* Right side - User Menu */}
        <div className="flex items-center gap-4">
          <Dropdown menu={{ items: userMenuItems }} trigger={['click']} placement="bottomRight">
            <button className="flex items-center p-2 hover:bg-gray-100 rounded-lg transition-colors duration-200">
              <Avatar size="small" className="bg-blue-600 mr-2">
                <UserOutlined />
              </Avatar>
              <div className="hidden sm:block text-left">
                <div className="font-semibold text-gray-800 text-sm">{user?.username}</div>
                <div className="text-xs text-gray-500 capitalize">{user?.role}</div>
              </div>
            </button>
          </Dropdown>
        </div>
      </div>

      {/* Mobile Menu Overlay */}
      {mobileMenuOpen && (
        <div className="lg:hidden fixed inset-0 z-40 bg-black/50" onClick={() => setMobileMenuOpen(false)}>
          <div className="absolute right-0 top-0 h-full w-80 bg-white shadow-xl">
            <div className="p-6">
              <div className="flex items-center justify-between mb-8">
                <div className="flex items-center">
                  <div className="w-12 h-12 bg-blue-600 rounded-lg flex items-center justify-center mr-4">
                    <CarOutlined className="text-white text-xl" />
                  </div>
                  <h1 className="text-2xl font-bold text-gray-800">ParkingApp</h1>
                </div>
                <button
                  onClick={() => setMobileMenuOpen(false)}
                  className="p-2 hover:bg-gray-100 rounded-lg"
                >
                  <MenuOutlined className="text-lg" />
                </button>
              </div>
              
              <div className="space-y-2">
                {isAdmin ? (
                  // Admin mobile menu
                  <>
                    <button
                      onClick={() => {
                        navigate('/admin');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/admin'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">📊</span>
                      <span className="font-medium text-gray-700">Dashboard</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/admin/parking-lots');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/admin/parking-lots'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">🏢</span>
                      <span className="font-medium text-gray-700">Parking Lots</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/admin/reservations');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/admin/reservations'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">📈</span>
                      <span className="font-medium text-gray-700">Reservations</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/admin/analytics');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/admin/analytics'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">📊</span>
                      <span className="font-medium text-gray-700">Analytics</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/admin/users');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/admin/users'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">👥</span>
                      <span className="font-medium text-gray-700">Users</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/admin/settings');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/admin/settings'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">⚙️</span>
                      <span className="font-medium text-gray-700">Settings</span>
                    </button>
                  </>
                ) : (
                  // User mobile menu
                  <>
                    <button
                      onClick={() => {
                        navigate('/user');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/user'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">📊</span>
                      <span className="font-medium text-gray-700">Dashboard</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/user/book-slot');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/user/book-slot'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">📅</span>
                      <span className="font-medium text-gray-700">Book Slot</span>
                    </button>
                    
                    <button
                      onClick={() => {
                        navigate('/user/my-reservations');
                        setMobileMenuOpen(false);
                      }}
                      className={`w-full flex items-center p-3 rounded-lg transition-colors duration-200 ${
                        location.pathname === '/user/my-reservations'
                          ? 'bg-blue-100 text-blue-700 font-semibold border-l-4 border-blue-600'
                          : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
                      }`}
                    >
                      <span className="mr-3 text-gray-500">🚗</span>
                      <span className="font-medium text-gray-700">My Reservations</span>
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
