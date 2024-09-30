package com.example.HealthcareManager.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "habit")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 
    @Column(name = "alcohol")
    private boolean alcohol;
    @Column(name = "cigarette")
    private boolean cigarette;
    @Column(name = "areca")
    private boolean areca;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public boolean isAlcohol() {
        return alcohol;
    }
    public void setAlcohol(boolean alcohol) {
        this.alcohol = alcohol;
    }
    public boolean isCigarette() {
        return cigarette;
    }
    public void setCigarette(boolean cigarette) {
        this.cigarette = cigarette;
    }
    public boolean isAreca() {
        return areca;
    }
    public void setAreca(boolean areca) {
        this.areca = areca;
    }

}
