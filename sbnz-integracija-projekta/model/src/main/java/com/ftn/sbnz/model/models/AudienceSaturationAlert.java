package com.ftn.sbnz.model.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudienceSaturationAlert implements Serializable {

    private static final long serialVersionUID = 1L;
    private String message;
    private String saturatedCategory;

}