package com.ftn.sbnz.model.models;

import java.util.List;

public class InterestMatcher {

    public static boolean matchesInterest(List<String> interests, String hashtag) {
        if (interests == null || interests.isEmpty() || hashtag == null) {
            return false;
        }

        String cleanHashtag = hashtag.toLowerCase().replace("#", "").trim();

        for (String interest : interests) {
            String cleanInterest = interest.toLowerCase().trim();

            if (cleanHashtag.contains(cleanInterest) || cleanInterest.contains(cleanHashtag)) {
                return true;
            }
        }

        return false;
    }
}