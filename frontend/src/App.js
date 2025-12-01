import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import { Toaster } from 'react-hot-toast';
import './App.css';
import './utils/resizeObserverFix'; // Import ResizeObserver fix
import './date-picker-clean.css'; // Import clean date picker fix
import './calendar-close-button.css'; // Import calendar close button styles

// Components
import Login from './components/Login';
import AdminDashboard from './components/AdminDashboard';
import UserDashboard from './components/UserDashboard';
import Layout from './components/Layout';

// User Components
import BookSlot from './components/user/BookSlot';
import MyReservations from './components/user/MyReservations';
import TestDatePicker from './components/TestDatePicker';
import SimpleDateTest from './components/SimpleDateTest';
import OKButtonTest from './components/OKButtonTest';
import CalendarCloseTest from './components/CalendarCloseTest';
import SimpleRangeTest from './components/SimpleRangeTest';
import BasicRangeTest from './components/BasicRangeTest';
import ForceCloseTest from './components/ForceCloseTest';
import CalendarFixTest from './components/CalendarFixTest';

// Admin Components
import Analytics from './components/admin/Analytics';
import ParkingLotsManagement from './components/admin/ParkingLotsManagement';
import ReservationsManagement from './components/admin/ReservationsManagement';
import VehiclesManagement from './components/admin/VehiclesManagement';

// Context
import { AuthProvider, useAuth } from './context/AuthContext';

function App() {
  // Global error handler for ResizeObserver errors
  useEffect(() => {
    const handleGlobalError = (event) => {
      if (event.error && event.error.message && 
          event.error.message.includes('ResizeObserver loop completed with undelivered notifications')) {
        event.preventDefault();
        return false;
      }
    };

    window.addEventListener('error', handleGlobalError);
    return () => window.removeEventListener('error', handleGlobalError);
  }, []);

  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#3b82f6',
          borderRadius: 8,
          colorBgContainer: '#ffffff',
          colorBgLayout: '#f8fafc',
        },
      }}
    >
      <AuthProvider>
        <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
          <div className="h-screen">
            <AppRoutes />
            <Toaster position="top-right" />
          </div>
        </Router>
      </AuthProvider>
    </ConfigProvider>
  );
}

function AppRoutes() {
  const { user, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [, setBackendConnected] = useState(true);

  // Check backend connection
  useEffect(() => {
    const checkBackendConnection = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/parking-lots', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        
        if (response.ok) {
          setBackendConnected(true);
        } else {
          setBackendConnected(false);
        }
      } catch (error) {
        setBackendConnected(false);
      }
    };

    checkBackendConnection();
  }, []);

  useEffect(() => {
    if (!loading) {
      if (user) {
        // User is logged in, redirect to appropriate dashboard
        if (location.pathname === '/login' || location.pathname === '/') {
          navigate(user.role === 'ADMIN' ? '/admin' : '/user', { replace: true });
        }
      } else {
        // User is not logged in, redirect to login
        if (location.pathname !== '/login') {
          navigate('/login', { replace: true });
        }
      }
    }
  }, [user, loading, navigate, location.pathname]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-lg">Loading...</div>
      </div>
    );
  }

  // Show login if no user
  if (!user) {
    return <Login />;
  }

  // Show appropriate dashboard based on user role
  if (user.role === 'ADMIN') {
    return (
      <Layout>
        <Routes>
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/analytics" element={<Analytics />} />
          <Route path="/admin/parking-lots" element={<ParkingLotsManagement />} />
          <Route path="/admin/reservations" element={<ReservationsManagement />} />
          <Route path="/admin/vehicles" element={<VehiclesManagement />} />
          <Route path="*" element={<AdminDashboard />} />
        </Routes>
      </Layout>
    );
  } else if (user.role === 'USER') {
    return (
      <Layout>
        <Routes>
          <Route path="/user" element={<UserDashboard />} />
          <Route path="/user/book-slot" element={<BookSlot />} />
          <Route path="/user/my-reservations" element={<MyReservations />} />
          <Route path="/user/test-date-picker" element={<TestDatePicker />} />
          <Route path="/user/simple-date-test" element={<SimpleDateTest />} />
          <Route path="/user/ok-button-test" element={<OKButtonTest />} />
          <Route path="/user/calendar-close-test" element={<CalendarCloseTest />} />
          <Route path="/user/simple-range-test" element={<SimpleRangeTest />} />
          <Route path="/user/basic-range-test" element={<BasicRangeTest />} />
          <Route path="/user/force-close-test" element={<ForceCloseTest />} />
          <Route path="/user/calendar-fix-test" element={<CalendarFixTest />} />
          <Route path="*" element={<UserDashboard />} />
        </Routes>
      </Layout>
    );
  }

  // Fallback
  return <Login />;
}


export default App;
