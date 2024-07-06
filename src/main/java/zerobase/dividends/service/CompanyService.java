package zerobase.dividends.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zerobase.dividends.exception.impl.AlreadyExistCompanyException;
import zerobase.dividends.exception.impl.NoCompanyException;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;
import zerobase.dividends.scraper.Scraper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScrapper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Transactional
    public Company save(String ticker) {
        boolean exist = companyRepository.existsByTicker(ticker);
        if (exist) {
            throw new AlreadyExistCompanyException();
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

    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        // trie 내 회사명 지우기
        deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
