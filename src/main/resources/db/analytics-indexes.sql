-- Analytics Performance Optimization Indexes
-- Run this script to improve analytics query performance

-- Indexes for reservations table
CREATE INDEX IF NOT EXISTS idx_reservations_status_start_time 
ON reservations(status, start_time);

CREATE INDEX IF NOT EXISTS idx_reservations_created_at 
ON reservations(created_at);

CREATE INDEX IF NOT EXISTS idx_reservations_status_created_at 
ON reservations(status, created_at);

CREATE INDEX IF NOT EXISTS idx_reservations_start_end_time 
ON reservations(start_time, end_time);

-- Indexes for payments table
CREATE INDEX IF NOT EXISTS idx_payments_status_paid_at 
ON payments(status, paid_at);

CREATE INDEX IF NOT EXISTS idx_payments_status_created_at 
ON payments(status, created_at);

-- Indexes for slots table (for occupancy calculations)
CREATE INDEX IF NOT EXISTS idx_slots_parking_lot_id 
ON slots(parking_lot_id);

CREATE INDEX IF NOT EXISTS idx_slots_occupied 
ON slots(occupied);

-- Composite index for reservation-slot-lot queries
CREATE INDEX IF NOT EXISTS idx_reservations_slot_lot 
ON reservations(slot_id);

-- Index for vehicle type queries
CREATE INDEX IF NOT EXISTS idx_vehicles_type 
ON vehicles(type);

-- Analyze tables after creating indexes
ANALYZE reservations;
ANALYZE payments;
ANALYZE slots;
ANALYZE vehicles;
ANALYZE parking_lots;

