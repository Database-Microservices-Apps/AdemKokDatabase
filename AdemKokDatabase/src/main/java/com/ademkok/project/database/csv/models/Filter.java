package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Filter {
    private final String name;
    private final String value;
}
