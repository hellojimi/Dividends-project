package zerobase.dividends.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.Dividend;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());


        return new ScrapedResult(
                Company.builder()
                        .ticker(company.getTicker())
                        .name(company.getName())
                        .build(),
                dividendEntities.stream()
                        .map(e -> Dividend.builder()
                                .date(e.getDate())
                                .dividend(e.getDividend())
                                .build())
                        .collect(Collectors.toList())
        );
    }

}
