package com.career.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a professional career path suitable for B.Tech CSE students.
 */
public class Career {
    private int id;
    private String name;
    private String shortDescription;
    private String responsibilities;
    private String skillsRequired;
    private String toolsTechnologies;
    private String learningSuggestions;
    private boolean active;
    private Timestamp createdAt;
    private List<CareerQuestionMapping> mappings = new ArrayList<>();

    public Career() {}

    public Career(int id, String name, String shortDescription, String responsibilities,
                  String skillsRequired, String toolsTechnologies, String learningSuggestions, boolean active) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.responsibilities = responsibilities;
        this.skillsRequired = skillsRequired;
        this.toolsTechnologies = toolsTechnologies;
        this.learningSuggestions = learningSuggestions;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getToolsTechnologies() {
        return toolsTechnologies;
    }

    public void setToolsTechnologies(String toolsTechnologies) {
        this.toolsTechnologies = toolsTechnologies;
    }

    public String getLearningSuggestions() {
        return learningSuggestions;
    }

    public void setLearningSuggestions(String learningSuggestions) {
        this.learningSuggestions = learningSuggestions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<CareerQuestionMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<CareerQuestionMapping> mappings) {
        this.mappings = mappings;
    }
}
