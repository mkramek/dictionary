package pl.mkramek.dictionary.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.mkramek.dictionary.model.http.response.DictionaryReportResponse;
import pl.mkramek.dictionary.service.ReportService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/api/v1")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report/view")
    public ResponseEntity<DictionaryReportResponse> getFullReport() {
        return ResponseEntity.ok(reportService.getFullReport());
    }

    @GetMapping("/report/download")
    public void exportPDF(HttpServletResponse response) {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=report_%s.pdf".formatted(currentDateTime);
        response.setHeader(headerKey, headerValue);
        reportService.generateDocument(response);
    }
}
