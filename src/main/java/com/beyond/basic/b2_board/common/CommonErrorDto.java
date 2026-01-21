package com.beyond.basic.b2_board.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommonErrorDto {
    private int status_code;
    private String error_message;
}
