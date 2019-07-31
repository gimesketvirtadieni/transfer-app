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
public class AccountBalance {

    @JsonProperty
    @Getter
    @Setter
    private long id;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private Currency currency;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private double currentBalance;
}
