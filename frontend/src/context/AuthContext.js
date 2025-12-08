import React, { createContext, useContext, useState, useEffect } from 'react';
import { logout as logoutApi } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in (from localStorage)
    const savedUser = localStorage.getItem('user');
    const accessToken = localStorage.getItem('accessToken');
    
    if (savedUser && accessToken) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        // Invalid user data, clear it
        localStorage.removeItem('user');
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      }
    }
    setLoading(false);
  }, []);

  const login = (authResponse) => {
    // Extract user data and tokens from auth response
    const userData = {
      id: authResponse.userId,
      username: authResponse.username,
      email: authResponse.email,
      role: authResponse.role
    };
    
    // Store tokens and user data
    localStorage.setItem('accessToken', authResponse.accessToken);
    if (authResponse.refreshToken) {
      localStorage.setItem('refreshToken', authResponse.refreshToken);
    }
    localStorage.setItem('user', JSON.stringify(userData));
    
    setUser(userData);
  };

  const logout = async () => {
    try {
      // Call logout API to clear server-side cookies
      await logoutApi();
    } catch (error) {
      console.error('Logout API call failed:', error);
    } finally {
      // Clear local storage
      setUser(null);
      localStorage.removeItem('user');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  };

  const value = {
    user,
    login,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
