package com.apc.parking.service;

import com.apc.parking.model.*;
import com.apc.parking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ParkingService {

    @Autowired
    private SlotDao slotDao;

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private WaitQueueDao waitQueueDao;

    private Queue<Vehicle> twoWQueue = new LinkedList<>();
    private Queue<Vehicle> fourWQueue = new LinkedList<>();

    // =================== ADMIN FUNCTIONS ===================

    public void initializeSlots(int twoWCount, int fourWCount) {
        for (int i = 0; i < twoWCount; i++) {
            Slot slot = new Slot();
            slot.setType("2W");
            slot.setOccupied(false);
            slotDao.save(slot);
        }
        for (int i = 0; i < fourWCount; i++) {
            Slot slot = new Slot();
            slot.setType("4W");
            slot.setOccupied(false);
            slotDao.save(slot);
        }
    }

    // =================== PARK VEHICLE ===================

    public String parkVehicle(Vehicle vehicle) {
        List<Slot> slots = slotDao.findAll();
        Optional<Slot> emptySlot = slots.stream()
                .filter(s -> !s.isOccupied() && s.getType().equals(vehicle.getType()))
                .findFirst();

        if (emptySlot.isPresent()) {
            Slot slot = emptySlot.get();
            slot.setOccupied(true);
            slot.setVehicle(vehicle); // link vehicle
            slotDao.save(slot); // or update depending on DAO method

            vehicle.setParked(true);
            vehicleDao.save(vehicle); // or update

            Transaction tx = new Transaction();
            tx.setVehicle(vehicle);
            tx.setSlot(slot);
            tx.setAction("PARKED");
            tx.setTimestamp(System.currentTimeMillis());
            transactionDao.save(tx);

            return "Vehicle parked at slot ID: " + slot.getId();
        } else {
            WaitQueue wq = new WaitQueue();
            wq.setVehicle(vehicle);
            wq.setSlotType(vehicle.getType());
            wq.setTimestamp(System.currentTimeMillis());
            waitQueueDao.save(wq);

            if (vehicle.getType().equals("2W")) twoWQueue.add(vehicle);
            else fourWQueue.add(vehicle);

            return "No slots available. Vehicle added to wait queue.";
        }
    }

    // =================== EXIT VEHICLE ===================

    public String exitVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleDao.findById(vehicleId).orElse(null);
        if (vehicle == null || !vehicle.isParked()) {
            return "Vehicle not parked.";
        }

        List<Slot> slots = slotDao.findAll();
        Slot slot = slots.stream()
                .filter(s -> s.getVehicle() != null && s.getVehicle().getId().equals(vehicleId))
                .findFirst().orElse(null);

        if (slot != null) {
            slot.setOccupied(false);
            slot.setVehicle(null);
            slotDao.save(slot); // or update

            vehicle.setParked(false);
            vehicleDao.save(vehicle); // or update

            Transaction tx = new Transaction();
            tx.setVehicle(vehicle);
            tx.setSlot(slot);
            tx.setAction("EXITED");
            tx.setTimestamp(System.currentTimeMillis());
            transactionDao.save(tx);

            // Park next vehicle from queue if exists
            if (vehicle.getType().equals("2W") && !twoWQueue.isEmpty()) {
                Vehicle nextVehicle = twoWQueue.poll();
                parkVehicle(nextVehicle);
            } else if (vehicle.getType().equals("4W") && !fourWQueue.isEmpty()) {
                Vehicle nextVehicle = fourWQueue.poll();
                parkVehicle(nextVehicle);
            }

            return "Vehicle exited from slot ID: " + slot.getId();
        }

        return "Error: Slot not found.";
    }

    // =================== GET WAIT QUEUE ===================

    public List<WaitQueue> getWaitQueue(String type) {
        List<WaitQueue> all = waitQueueDao.findAll();
        List<WaitQueue> filtered = new ArrayList<>();
        for (WaitQueue wq : all) {
            if (wq.getSlotType().equals(type)) filtered.add(wq);
        }
        return filtered;
    }

    // =================== TRANSACTION HISTORY ===================

    public List<Transaction> getTransactionHistory() {
        return transactionDao.findAll();
    }

    public Vehicle searchVehicle(Long vehicleId) {
        return vehicleDao.findById(vehicleId).orElse(null);
    }

    public List<Slot> searchAvailableSlots(String type) {
        List<Slot> slots = slotDao.findAll();
        List<Slot> available = new ArrayList<>();
        for (Slot s : slots) {
            if (!s.isOccupied() && s.getType().equals(type)) available.add(s);
        }
        return available;
    }

    public List<Transaction> getUserTransactionHistory(Long userId) {
        List<Transaction> allTx = transactionDao.findAll();
        List<Transaction> userTx = new ArrayList<>();
        for (Transaction tx : allTx) {
            if (tx.getVehicle().getUser().getId().equals(userId)) {
                userTx.add(tx);
            }
        }
        return userTx;
    }
}
