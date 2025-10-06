package com.ftn.sbnz.model.events;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPublishedEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date timestamp;
    private Long postId;
    private String category;

    public PostPublishedEvent(long postId, String saturatedCategory) {
    }
}