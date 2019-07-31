package org.test.transfer.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

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
public class TransferRequest {

    @JsonProperty
    @Getter
    @Setter
    private String fromAccount;

    @JsonProperty
    @Getter
    @Setter
    private String toAccount;

    @JsonProperty
    @Getter
    @Setter
    private double amount;

    @JsonProperty
    @Getter
    @Setter
    private String currencyISOCode;
}
