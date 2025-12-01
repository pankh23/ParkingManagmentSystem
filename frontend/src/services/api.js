import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Parking Lots API
export const getParkingLots = () => api.get('/parking-lots').then(res => res.data);
export const getActiveParkingLots = () => api.get('/parking-lots/active').then(res => res.data);
export const getParkingLotById = (id) => api.get(`/parking-lots/${id}`).then(res => res.data);
export const createParkingLot = (data) => api.post('/parking-lots/create-lot', data).then(res => res.data);
export const updateParkingLot = (id, data) => api.put(`/parking-lots/${id}`, data).then(res => res.data);
export const deleteParkingLot = (id) => api.delete(`/parking-lots/${id}`).then(res => res.data);
export const getParkingLotStatus = (id) => api.get(`/parking-lots/${id}/status`).then(res => res.data);

// Reservations API
export const createReservation = (data) => api.post('/reservations', data).then(res => res.data);
export const getAllReservations = () => api.get('/reservations').then(res => res.data);
export const getReservationsByLot = (lotId) => api.get(`/reservations/lot/${lotId}`).then(res => res.data);
export const getUserReservations = (userId) => api.get(`/reservations/user/${userId}`).then(res => res.data);
export const getReservationById = (id) => api.get(`/reservations/${id}`).then(res => res.data);
export const confirmReservation = (id, data) => api.post(`/reservations/${id}/confirm`, data).then(res => res.data);
export const cancelReservation = (id) => api.post(`/reservations/${id}/cancel`).then(res => res.data);
export const getPaymentLink = (id) => api.get(`/reservations/${id}/payment-link`).then(res => res.data);
export const getActiveReservations = () => api.get('/reservations/active').then(res => res.data);
export const getSlotAvailability = (lotId) => api.get(`/reservations/slots/availability/${lotId}`).then(res => res.data);

// Vehicles API
export const getVehicleById = (id) => api.get(`/vehicles/${id}`).then(res => res.data);
export const createVehicle = (data) => api.post('/vehicles', data).then(res => res.data);
export const getVehicleHistory = (id) => api.get(`/vehicles/${id}/history`).then(res => res.data);
export const getAvailableSlots = (type) => api.get(`/vehicles/available-slots/${type}`).then(res => res.data);

// Parking API
export const parkVehicle = (data) => api.post('/parking/park', data).then(res => res.data);
export const exitVehicle = (data) => api.post('/parking/exit', data).then(res => res.data);
export const getSystemStatus = () => api.get('/parking/status').then(res => res.data);
export const getTransactionHistory = () => api.get('/parking/transactions').then(res => res.data);
export const getLatestVehicleStatus = () => api.get('/parking/latest-status').then(res => res.data);
export const getWaitQueueSize = (type) => api.get(`/parking/wait-queue/${type}`).then(res => res.data);

// Analytics API
export const getAnalyticsSummary = () => api.get('/analytics/summary').then(res => res.data);
export const getPeakHours = () => api.get('/analytics/peak-hours').then(res => res.data);
export const getVehicleTypeDistribution = () => api.get('/analytics/vehicle-type-distribution').then(res => res.data);
export const getDailyOccupancy = () => api.get('/analytics/daily-occupancy').then(res => res.data);
export const getMonthlyRevenue = () => api.get('/analytics/monthly-revenue').then(res => res.data);
export const getTopLots = () => api.get('/analytics/top-lots').then(res => res.data);
export const getBookingTrend = () => api.get('/analytics/booking-trend').then(res => res.data);

export default api;
