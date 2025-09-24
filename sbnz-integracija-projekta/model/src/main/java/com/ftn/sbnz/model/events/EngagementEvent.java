package com.ftn.sbnz.model.events;

import java.io.Serializable;
import java.util.Date;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
public class EngagementEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private double engagementRate;
    private Date timestamp;

    public EngagementEvent(double engagementRate) {
        this.engagementRate = engagementRate;
        this.timestamp = new Date();
    }

    public double getEngagementRate() {
        return engagementRate;
    }

    public void setEngagementRate(double engagementRate) {
        this.engagementRate = engagementRate;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}