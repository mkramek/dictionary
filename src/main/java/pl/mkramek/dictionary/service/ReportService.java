package pl.mkramek.dictionary.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.http.LanguageBasedInfo;
import pl.mkramek.dictionary.model.http.WordsLengthSummary;
import pl.mkramek.dictionary.model.http.response.DictionaryReportResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.mkramek.dictionary.support.pdf.PDFComponents.*;

@Service
public class ReportService {
    private final WordService wordService;
    private final LanguageService languageService;

    public ReportService(WordService wordService, LanguageService languageService) {
        this.wordService = wordService;
        this.languageService = languageService;
    }

    public DictionaryReportResponse getFullReport() {
        DictionaryReportResponse response = new DictionaryReportResponse();
        int untranslated = (int) wordService.getAll().stream().filter(dto -> dto.getTranslations().isEmpty() && dto.getTranslationOf().isEmpty()).count();
        int total = wordService.getAll().size();
        List<LanguageDTO> languages = languageService.getAll();
        response.setUntranslatedWordsCount(untranslated);
        response.setTotalWordsCount(total);
        response.setLanguageInfo(buildLanguageBasedInfoList(languages));
        return response;
    }

    private List<LanguageBasedInfo> buildLanguageBasedInfoList(List<LanguageDTO> langList) {
        List<LanguageBasedInfo> infoList = new ArrayList<>();
        langList.forEach(langDTO -> {
            LanguageBasedInfo info = createLangBasedInfo(langDTO);
            if (info != null) {
                infoList.add(info);
            }
        });
        return infoList;
    }

    private LanguageBasedInfo createLangBasedInfo(LanguageDTO languageDTO) {
        LanguageBasedInfo info = new LanguageBasedInfo();
        String langName = languageDTO.getName();
        info.setLanguage(langName);
        List<WordDTO> words = wordService.getAllByLanguage(langName);
        if (words.isEmpty()) {
            return null;
        }
        AtomicInteger totalLength = new AtomicInteger(0);
        words.forEach(word -> totalLength.addAndGet(word.getContent().length()));
        info.setAverageWordLength(Math.round((float) totalLength.get() / words.size()));
        info.setWordsLengthSummaries(createWordsLengthSummaries(words));
        return info;
    }

    private List<WordsLengthSummary> createWordsLengthSummaries(List<WordDTO> words) {
        List<WordsLengthSummary> summaries = new ArrayList<>();
        words.forEach(word -> {
            WordsLengthSummary summary = new WordsLengthSummary();
            if (summaries.stream().noneMatch(smry -> smry.getLength() == word.getContent().length())) {
                summary.setLength(word.getContent().length());
                summary.setWordCount(summary.getWordCount() + 1);
                summaries.add(summary);
            } else {
                summaries.stream()
                        .filter(smry -> smry.getLength() == word.getContent().length())
                        .findFirst()
                        .ifPresent(smry -> {
                            smry.setWordCount(smry.getWordCount() + 1);
                        });
            }
        });
        return summaries;
    }

    public void generateDocument(HttpServletResponse response) throws DocumentException {
        try (Document document = new Document(PageSize.A4)) {
            DictionaryReportResponse reportResponse = getFullReport();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            document.add(documentTitle());

            PdfPTable generalTable = table();
            generalTable.addCell(tableHeaderCell("General"));
            generalTable.addCell(tableLeftCell("All words"));
            generalTable.addCell(tableRightCell("%s".formatted(reportResponse.getTotalWordsCount())));
            generalTable.addCell(tableLeftCell("Untranslated words"));
            generalTable.addCell(tableRightCell("%s".formatted(reportResponse.getUntranslatedWordsCount())));
            document.add(generalTable);

            reportResponse.getLanguageInfo().forEach(info -> {
                document.add(subtitle("Language: %s".formatted(info.getLanguage())));
                PdfPTable infoTable = table();
                infoTable.addCell(tableHeaderCell("Words"));
                infoTable.addCell(tableLeftCell("Average length"));
                infoTable.addCell(tableRightCell("%s".formatted(info.getAverageWordLength())));
                List<WordsLengthSummary> summaries = info.getWordsLengthSummaries();
                summaries.sort(Comparator.comparingInt(WordsLengthSummary::getLength));
                summaries.forEach(summary -> {
                    infoTable.addCell(tableLeftCell("%s-letter word count".formatted(summary.getLength())));
                    infoTable.addCell(tableRightCell("%s".formatted(summary.getWordCount())));
                });
                document.add(infoTable);
            });
            //  TODO: untranslatedWordsCount;
            //  TODO: totalWordsCount;
            //  TODO: List<LanguageBasedInfo> languageInfo;
            //      TODO: language;
            //      TODO: averageWordLength;
            //      TODO: List<WordsLengthSummary> wordsLengthSummaries;
            //          TODO: length
            //          TODO: wordCount
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }
}
