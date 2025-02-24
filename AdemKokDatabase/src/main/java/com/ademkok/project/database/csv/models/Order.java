package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Order {
    private final String name;
    private final OrderType orderType;

    public enum OrderType {
        ASC,
        DESC
    }
}
