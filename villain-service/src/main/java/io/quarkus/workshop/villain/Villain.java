package io.quarkus.workshop.villain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
public class Villain {

    @Id
    @SequenceGenerator(name = "villainSequence", sequenceName = "villain_seq", allocationSize = 1, initialValue = 10)
    @GeneratedValue(generator = "villainSequence")
    private Long id;

    private String name;

    private String otherName;


    private int level;
    private String picture;

    @Column(columnDefinition = "TEXT")
    public String powers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @Size(min = 3, max = 50) String getName() {
        return name;
    }

    public void setName(@NotNull @Size(min = 3, max = 50) String name) {
        this.name = name;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    @NotNull
    @Min(1)
    public int getLevel() {
        return level;
    }

    public void setLevel(@NotNull @Min(1) int level) {
        this.level = level;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String setPowers() {
        return powers;
    }

    public void setPowers(String powers) {
        this.powers = powers;
    }
}
