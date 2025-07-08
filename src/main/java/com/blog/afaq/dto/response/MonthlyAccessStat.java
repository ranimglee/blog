package com.blog.afaq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyAccessStat {
    private String month;
    private long count;



}
