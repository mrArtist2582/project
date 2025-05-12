package com.musterdekho.dtos;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String headline;
    private String about;
    private String profileImage;
}
