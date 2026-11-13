package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.models.StudySpaceFeedback;

@Service 
public class StudySpaceFeedbackService {

    private final double EMPTYLIST = 0.00; 

    // improve later
    public double calculateAverageNoise(List<StudySpaceFeedback> reviews) {
        if(reviews == null || reviews.isEmpty()){
            return EMPTYLIST; 
        }

        double total = 0;
        for( StudySpaceFeedback review : reviews){
            total += review.getNoiseRating(); 
        }

        return (total/reviews.size()); 
    }

    public double calculateAverageAmenitites(List<StudySpaceFeedback> reviews) {
        if(reviews == null || reviews.isEmpty()){
            return EMPTYLIST; 
        }

        double total = 0;
        for( StudySpaceFeedback review : reviews){
            total += review.getAmenitiesRating(); 
        }

        return (total/reviews.size()); 
    }

    public double calculateAverageCleanliness(List<StudySpaceFeedback> reviews) {
        if(reviews == null || reviews.isEmpty()){
            return EMPTYLIST; 
        }

        double total = 0;
        for( StudySpaceFeedback review : reviews){
            total += review.getCleanlinessRating(); 
        }

        return (total/reviews.size()); 
    }
}
