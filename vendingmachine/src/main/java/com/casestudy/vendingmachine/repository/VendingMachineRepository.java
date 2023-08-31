package com.casestudy.vendingmachine.repository;

import com.casestudy.vendingmachine.model.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendingMachineRepository extends JpaRepository<VendingMachine, Integer> {}
