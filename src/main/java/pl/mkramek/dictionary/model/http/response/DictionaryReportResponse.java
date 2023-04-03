package pl.mkramek.dictionary.model.http.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mkramek.dictionary.model.http.LanguageBasedInfo;

import java.util.List;

@Data
@NoArgsConstructor
public class DictionaryReportResponse {
    private int untranslatedWordsCount;
    private int totalWordsCount;
    private List<LanguageBasedInfo> languageInfo;
}
