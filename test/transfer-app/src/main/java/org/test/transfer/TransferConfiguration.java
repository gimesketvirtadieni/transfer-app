package org.test.transfer;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TransferConfiguration extends Configuration {

    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String liquibaseFile;

    @JsonProperty("database")
    @Valid
    @NotNull
    @Getter
    @Setter
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();
}
