package com.timeletter.api.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T>{
    private String error;
    public List<T> data;
    public Page<T> pageData;
}
