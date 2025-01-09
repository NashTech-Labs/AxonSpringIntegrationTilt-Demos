package org.nashtech.reception.preauth.preauth.adapter.preauthmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreAuthMetadata {

    private String uuid;
    private String messageType;
    private String emittedAtDateTime;
    private String source;
    private String messageTypeVersion;
}
