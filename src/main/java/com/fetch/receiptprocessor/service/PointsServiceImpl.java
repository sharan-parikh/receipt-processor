package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.calculators.PointsRule;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

  private List<PointsRule> rules;
  public PointsServiceImpl(List<PointsRule> rules) {
    this.rules = rules;
  }

  public int calculatePoints(Receipt receipt, boolean llmGenerated) {
    return rules.stream()
            .mapToInt(rule -> rule.apply(receipt, llmGenerated))
            .sum();
  }
}
