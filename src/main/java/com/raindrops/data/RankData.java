package com.raindrops.data;

import java.io.Serializable;

/**
 * RankData
 *
 * @author raindrops
 */
public class RankData implements Serializable {

    private Integer score;

    private String name;

    private String date;

    public RankData(Integer score, String name, String date) {
        this.score = score;
        this.name = name;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.join(",", score.toString(), name, date);
    }

    public RankData() {
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}