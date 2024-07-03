package zerobase.dividends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.service.FinanceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        ScrapedResult result = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(result);
    }
}
