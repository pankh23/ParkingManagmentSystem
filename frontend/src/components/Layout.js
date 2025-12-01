import React, { useState } from 'react';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import AdminSidebar from './AdminSidebar';
import { useAuth } from '../context/AuthContext';

const Layout = ({ children }) => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const { user } = useAuth();

  const handleMenuToggle = () => {
    setIsCollapsed(!isCollapsed);
  };

  // Determine which sidebar to use based on user role
  const isAdmin = user?.role === 'ADMIN'; 
  const SidebarComponent = isAdmin ? AdminSidebar : Sidebar;

  return (
    <div className="h-screen bg-gray-50 flex flex-col">
      {/* Navbar */}
      <Navbar onMenuToggle={handleMenuToggle} isCollapsed={isCollapsed} />

      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar - Different for admin and user */}
        <SidebarComponent isCollapsed={isCollapsed} onMenuToggle={handleMenuToggle} />

        {/* Main Content */}
        <div className="flex-1 overflow-hidden">
          <div className="h-full overflow-y-auto">
            {children}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Layout;