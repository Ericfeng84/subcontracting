package com.example.subcontracting.repository;

import com.example.subcontracting.model.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, String> {
    Optional<BillOfMaterial> findByFinishedGoodId(String finishedGoodId);
}