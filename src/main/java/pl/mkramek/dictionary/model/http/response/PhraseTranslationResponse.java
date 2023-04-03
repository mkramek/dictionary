package pl.mkramek.dictionary.model.http.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhraseTranslationResponse {
    private String phraseToTranslate;
    private String translatedPhrase;
    private int unknownWordsCount;
}
