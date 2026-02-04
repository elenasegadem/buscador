package com.relatosPapel.operador.data.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class SearchStatement {

    private String key;

    private Object value;

    private SearchOperation operation;
}
