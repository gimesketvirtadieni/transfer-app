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
public class TransferResponse {

    @JsonProperty
    @Getter
    @Setter
    private Long transactionId;

    @JsonProperty
    @Getter
    @Setter
    private String error;
}
