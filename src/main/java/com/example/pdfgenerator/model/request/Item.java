package com.example.pdfgenerator.model.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Item implements Serializable {
    private String name;
    private String quantity;
    private double rate;
    private double amount;
}
