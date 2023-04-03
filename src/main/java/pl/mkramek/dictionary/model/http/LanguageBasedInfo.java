package pl.mkramek.dictionary.model.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LanguageBasedInfo {
    private String language;
    private int averageWordLength;
    @JsonProperty("wordsStats")
    private List<WordsLengthSummary> wordsLengthSummaries;
}
