package com.fontysio.colleaguetracker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {

    private int statusCode;
    private String message;

    public StatusResponse(String message){
        this.message = message;
    }
}
