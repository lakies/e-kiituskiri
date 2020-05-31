package com.vhk.kirjad.utils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LetterParams {
    private String studentId, msg, signature, type, date;
}
