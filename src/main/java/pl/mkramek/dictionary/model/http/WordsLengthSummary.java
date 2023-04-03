package pl.mkramek.dictionary.model.http;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordsLengthSummary {
    private int length;
    private int wordCount = 0;
}
