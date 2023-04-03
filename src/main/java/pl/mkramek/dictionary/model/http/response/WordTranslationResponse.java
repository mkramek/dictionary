package pl.mkramek.dictionary.model.http.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordTranslationResponse {
    private String wordToTranslate;
    private String translation;
}
