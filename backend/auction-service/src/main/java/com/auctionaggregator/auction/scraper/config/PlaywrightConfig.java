package com.auctionaggregator.auction.scraper.config;

import com.microsoft.playwright.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PlaywrightConfig {
    
    private Playwright playwright;
    private Browser browser;
    
    @PostConstruct
    public void init() {
        log.info("Initializing Playwright");
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(true)
            .setArgs(java.util.List.of(
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-setuid-sandbox",
                "--disable-gpu"
            ))
        );
        log.info("Playwright browser initialized");
    }
    
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up Playwright resources");
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @Bean
    public Browser playwrightBrowser() {
        return browser;
    }
    
    public BrowserContext createContext() {
        return browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAcceptDownloads(false)
            .setIgnoreHTTPSErrors(true)
        );
    }
}