package com.qeema.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Response implements Serializable {
    /**
     *
     *
     */
    private static final long serialVersionUID = -8524185686815426024L;
    private String responseCode;
    private String responseMsg;
}
