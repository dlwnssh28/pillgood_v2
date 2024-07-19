package com.pillgood.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "deficiencies")
public class Deficiency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deficiency_id")
    private int deficiencyId;

    @Column(name = "deficiency_name")
    private String deficiencyName;

    @Override
    public String toString() {
        return "Deficiency{" +
                "deficiencyId=" + deficiencyId +
                ", deficiencyName='" + deficiencyName + '\'' +
                '}';
    }

    @ManyToMany(mappedBy = "deficiencies")
    private List<Product> products;

    @OneToMany(mappedBy = "deficiency")
    private List<DeficiencyNutrient> deficiencyNutrients;

    // Getters and Setters
    public int getDeficiencyId() {
        return deficiencyId;
    }

    public void setDeficiencyId(int deficiencyId) {
        this.deficiencyId = deficiencyId;
    }

    public String getDeficiencyName() {
        return deficiencyName;
    }

    public void setDeficiencyName(String deficiencyName) {
        this.deficiencyName = deficiencyName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<DeficiencyNutrient> getDeficiencyNutrients() {
        return deficiencyNutrients;
    }

    public void setDeficiencyNutrients(List<DeficiencyNutrient> deficiencyNutrients) {
        this.deficiencyNutrients = deficiencyNutrients;
    }
}
