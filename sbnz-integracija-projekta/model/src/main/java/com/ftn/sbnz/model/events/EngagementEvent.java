package com.ftn.sbnz.model.events;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Data
@NoArgsConstructor
public class EngagementEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum EngagementType {
        LIKE, COMMENT, SHARE
    }

    private Date timestamp;
    private Long postId;
    private String postCategory;
    private EngagementType type;

    // ✅ THIS IS THE CORRECT CONSTRUCTOR
    public EngagementEvent(Long postId, String postCategory, EngagementType type, Date timestamp) {
        this.postId = postId;
        this.postCategory = postCategory;
        this.type = type;
        this.timestamp = timestamp;
    }
}