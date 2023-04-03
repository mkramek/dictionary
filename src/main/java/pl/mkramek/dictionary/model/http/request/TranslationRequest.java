package pl.mkramek.dictionary.model.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TranslationRequest implements Serializable {
    @NonNull
    private WordRequest translatable;
    @NonNull
    private WordRequest translation;
}
