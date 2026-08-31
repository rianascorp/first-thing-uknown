package com.rianascorp.objects;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cats",schema = "cats")
public class Cats {
    @Column(name = "dob")
    private LocalDate dob;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne
    @JoinColumn(name = "furr_id")
    private Furr furr;

    @Column(name = "photo", columnDefinition = "BYTEA")
    private byte[] photo;


    @Column(name = "thumbnail", columnDefinition = "BYTEA")
    private byte[] thumbnail;

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Cats() {
    }

    public Cats(String name, Gender gender, Race race, Furr furr, LocalDate dob, byte[] photo) {
        this.dob = dob;
        this.name = name;
        this.gender = gender;
        this.race = race;
        this.furr = furr;
        this.photo = photo;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Furr getFurr() {
        return furr;
    }

    public void setFurr(Furr furr) {
        this.furr = furr;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }}
