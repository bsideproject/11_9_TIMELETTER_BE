package com.timeletter.api.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.timeletter.api.letter.Letter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid",strategy = "uuid")
    private String id;

    private String mimetype;
    private String original_name;

    @Lob
    @Column(columnDefinition="BLOB")
    private byte[] data;
    private String created;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "letter_id")
    private Letter letter;

}
