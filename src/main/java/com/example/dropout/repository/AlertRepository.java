package com.example.dropout.repository;

import com.example.dropout.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    // 1. Fetch only unread alerts (For the Red Badge count)
    List<Alert> findByIsReadFalseOrderByTimestampDesc(); 
    
    // 2. Fetch all alerts, newest first (For the History Dialog)
    List<Alert> findAllByOrderByTimestampDesc(); 
}