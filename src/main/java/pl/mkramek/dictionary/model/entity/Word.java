package pl.mkramek.dictionary.model.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
@Entity
@Table(name = "words", schema = "dictionary")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Word {
    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column
    @NonNull
    private String content;

    @NonNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name="tl_mapping",
            joinColumns=@JoinColumn(name="translation_id"),
            inverseJoinColumns=@JoinColumn(name="translatable_id")
    )
    private List<Word> translations = new LinkedList<>();

    @ManyToMany(mappedBy = "translations", fetch = FetchType.EAGER)
    private List<Word> translationOf = new LinkedList<>();
}
