package com.ftn.sbnz.model.events;

import java.io.Serializable;
import java.util.Date;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
public class HashtagUsageEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private String hashtag;
    private Date timestamp;

    public HashtagUsageEvent(String hashtag) {
        this.hashtag = hashtag;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}