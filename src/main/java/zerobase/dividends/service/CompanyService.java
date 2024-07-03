package zerobase.dividends.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;
import zerobase.dividends.scraper.Scrapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final Scrapper yahooFinanceScrapper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Transactional
    public Company save(String ticker) {
        boolean exist = companyRepository.existsByTicker(ticker);
        if (exist) {
            throw new RuntimeException("already exist ticker -> " + ticker);
        }

        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Transactional
    private Company storeCompanyAndDividend(String ticker) {
        Company company = yahooFinanceScrapper.scrapCompanyByTicker(ticker);

        ScrapedResult scrapedResult = yahooFinanceScrapper.scrap(company);

        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities =
                scrapedResult.getDividends().stream()
                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                        .collect(Collectors.toList());
        dividendRepository.saveAll(dividendEntities);

        return company;
    }
}
