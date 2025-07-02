package com.receiptprocessor.backend.receipt.service;

import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.service.calculators.PointsRule;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

  private final List<PointsRule> rules;
  public PointsServiceImpl(List<PointsRule> rules) {
    this.rules = rules;
  }

  public int calculatePoints(Receipt receipt, boolean llmGenerated) {
    return rules.stream()
            .mapToInt(rule -> rule.apply(receipt, llmGenerated))
            .sum();
  }
}
