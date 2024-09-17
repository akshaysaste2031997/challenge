package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TransferDTO {

  @NotNull
  @NotEmpty
  private final String accountFromId;
  
  @NotNull
  @NotEmpty
  private final String accountToId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal amount;

  @JsonCreator
  public TransferDTO(@JsonProperty("accountFromId") String accountFromId, @JsonProperty("accountToId") String accountToId,
    @JsonProperty("amount") BigDecimal amount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.amount = amount;
  }
}
