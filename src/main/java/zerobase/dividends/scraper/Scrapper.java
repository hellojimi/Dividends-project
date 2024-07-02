package zerobase.dividends.scraper;

import zerobase.dividends.model.Company;
import zerobase.dividends.model.ScrapedResult;

public interface Scrapper {
    ScrapedResult scrap(Company company);

    Company scrapCompanyByTicker(String ticker);
}
