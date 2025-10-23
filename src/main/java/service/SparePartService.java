package com.vehicle.spareparts.service;

import com.vehicle.spareparts.dto.SparePartRequest;
import com.vehicle.spareparts.dto.SparePartResponse;
import com.vehicle.spareparts.entity.SparePart;
import com.vehicle.spareparts.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SparePartService {
    
    @Autowired
    private SparePartRepository sparePartRepository;
    
    @Transactional
    public SparePartResponse createSparePart(SparePartRequest request) {
        if (sparePartRepository.findByPartNumber(request.getPartNumber()).isPresent()) {
            throw new RuntimeException("Part number already exists");
        }
        
        SparePart sparePart = new SparePart();
        sparePart.setPartNumber(request.getPartNumber());
        sparePart.setPartName(request.getPartName());
        sparePart.setCategory(request.getCategory());
        sparePart.setBrand(request.getBrand());
        sparePart.setVehicleModel(request.getVehicleModel());
        sparePart.setDescription(request.getDescription());
        sparePart.setPrice(request.getPrice());
        sparePart.setStockQuantity(request.getStockQuantity());
        sparePart.setReorderLevel(request.getReorderLevel());
        sparePart.setWarrantyMonths(request.getWarrantyMonths());
        sparePart.setImageUrl(request.getImageUrl());
        sparePart.setIsActive(request.getIsActive());
        
        SparePart saved = sparePartRepository.save(sparePart);
        return convertToResponse(saved);
    }
    
    @Transactional
    public SparePartResponse updateSparePart(Long id, SparePartRequest request) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spare part not found"));
        
        sparePart.setPartName(request.getPartName());
        sparePart.setCategory(request.getCategory());
        sparePart.setBrand(request.getBrand());
        sparePart.setVehicleModel(request.getVehicleModel());
        sparePart.setDescription(request.getDescription());
        sparePart.setPrice(request.getPrice());
        sparePart.setStockQuantity(request.getStockQuantity());
        sparePart.setReorderLevel(request.getReorderLevel());
        sparePart.setWarrantyMonths(request.getWarrantyMonths());
        sparePart.setImageUrl(request.getImageUrl());
        sparePart.setIsActive(request.getIsActive());
        
        SparePart updated = sparePartRepository.save(sparePart);
        return convertToResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public SparePartResponse getSparePart(Long id) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spare part not found"));
        return convertToResponse(sparePart);
    }
    
    @Transactional(readOnly = true)
    public List<SparePartResponse> getAllSpareParts() {
        return sparePartRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SparePartResponse> getActiveSpareParts() {
        return sparePartRepository.findByIsActive(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SparePartResponse> searchSpareParts(String keyword) {
        return sparePartRepository.searchParts(keyword).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SparePartResponse> getSparePartsByCategory(String category) {
        return sparePartRepository.findByCategory(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SparePartResponse> getLowStockParts() {
        return sparePartRepository.findLowStockParts().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteSparePart(Long id) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spare part not found"));
        sparePart.setIsActive(false);
        sparePartRepository.save(sparePart);
    }
    
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spare part not found"));
        
        int newQuantity = sparePart.getStockQuantity() + quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        sparePart.setStockQuantity(newQuantity);
        sparePartRepository.save(sparePart);
    }
    
    private SparePartResponse convertToResponse(SparePart sparePart) {
        SparePartResponse response = new SparePartResponse();
        response.setId(sparePart.getId());
        response.setPartNumber(sparePart.getPartNumber());
        response.setPartName(sparePart.getPartName());
        response.setCategory(sparePart.getCategory());
        response.setBrand(sparePart.getBrand());
        response.setVehicleModel(sparePart.getVehicleModel());
        response.setDescription(sparePart.getDescription());
        response.setPrice(sparePart.getPrice());
        response.setStockQuantity(sparePart.getStockQuantity());
        response.setReorderLevel(sparePart.getReorderLevel());
        response.setWarrantyMonths(sparePart.getWarrantyMonths());
        response.setImageUrl(sparePart.getImageUrl());
        response.setIsActive(sparePart.getIsActive());
        response.setCreatedAt(sparePart.getCreatedAt());
        response.setUpdatedAt(sparePart.getUpdatedAt());
        return response;
    }
}
