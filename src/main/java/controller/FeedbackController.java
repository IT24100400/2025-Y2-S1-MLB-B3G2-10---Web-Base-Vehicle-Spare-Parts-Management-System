package com.vehicle.spareparts.controller;

import com.vehicle.spareparts.entity.Feedback;
import com.vehicle.spareparts.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    // CREATE - Customer submits feedback
    @PostMapping("/create")
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback created = feedbackService.createFeedback(feedback);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // READ - Get all feedback (Store owner)
    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // READ - Get customer's own feedback
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Feedback>> getCustomerFeedback(@PathVariable Long customerId) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackByCustomer(customerId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // READ - Get single feedback
    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedback(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE - Store owner deletes feedback
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // UPDATE - Mark as read
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Feedback> markAsRead(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.markAsRead(id);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
