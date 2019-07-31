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
public class Transaction {

    @JsonProperty
    @Getter
    @Setter
    private long id;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private LocalDateTime registrationDate;

    @JsonProperty
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private List<TransactionItem> transactionItems;
}
