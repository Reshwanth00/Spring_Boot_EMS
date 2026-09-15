package com.tyss.restdemo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AddressDto implements Serializable {

    private String street;

    private String city;

    private String state;

    private String zipCode;
}
