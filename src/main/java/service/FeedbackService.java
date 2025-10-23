package com.vehicle.spareparts.service;

import com.vehicle.spareparts.entity.Feedback;
import com.vehicle.spareparts.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Transactional
    public Feedback createFeedback(Feedback feedback) {
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setStatus("PENDING");
        return feedbackRepository.save(feedback);
    }
    
    @Transactional(readOnly = true)
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Feedback> getFeedbackByCustomer(Long customerId) {
        return feedbackRepository.findByCustomerId(customerId);
    }
    
    @Transactional(readOnly = true)
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }
    
    @Transactional
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(id);
    }
    
    @Transactional
    public Feedback markAsRead(Long id) {
        Feedback feedback = getFeedbackById(id);
        feedback.setStatus("READ");
        return feedbackRepository.save(feedback);
    }
}
