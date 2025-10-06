package com.ftn.sbnz.service.services;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    public KieSession generateRulesFromTable() {
        try {
            // 1. Load template and data from the KJAR resources
            InputStream template = getClass().getResourceAsStream("/rules/template_rec/recommendation-template.drt");
            InputStream data = getClass().getResourceAsStream("/rules/template_rec/recommendation-rules.xlsx");

            if (template == null || data == null) {
                throw new RuntimeException("Template or Excel file not found in resources!");
            }

            // 2. Compile Excel + DRT → DRL
            ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
            String drl = converter.compile(data, template, 2, 1);

            log.info("=== Generated DRL from Template ===\n" + drl);

            Path drlFilePath = Paths.get("kjar/src/main/resources/rules/template_rec/template-generated.drl");
            Files.createDirectories(drlFilePath.getParent());
            Files.writeString(drlFilePath, drl);
            log.info("=== DRL written to: " + drlFilePath.toAbsolutePath() + " ===");

            // 3. Build Kie base from generated DRL
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem();
            kfs.write("src/main/resources/rules/template_rec/template-generated.drl", drl);

            KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
            if (kb.getResults().hasMessages(Message.Level.ERROR)) {
                throw new RuntimeException("Drools build errors:\n" + kb.getResults());
            }

            KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
            KieSession kieSession = kieContainer.newKieSession();

            return kieSession;

        } catch (Exception e) {
            log.error("Error running template rules: ", e);
            throw new RuntimeException("Template rules failed: " + e.getMessage(), e);
        }
    }
}
