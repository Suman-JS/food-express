package com.suman.foodexpress.restaurants.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDetails {

  @Column(length = 100)
  private String accountHolderName;

  @Column(length = 34)
  private String accountNumber;

  @Column(length = 11)
  private String ifscCode;

  @Column(length = 100)
  private String bankName;
}
