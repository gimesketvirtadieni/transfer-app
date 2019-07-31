package org.test.transfer.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(callSuper=true)
public class TransactionItem {

    @JsonProperty
    @Getter
    @Setter
    private long id;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private AccountBalance accountBalance;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private double debitAmount;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private double creditAmount;
}
