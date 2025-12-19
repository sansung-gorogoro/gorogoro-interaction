package com.example.lxp.common.messaging.domain.model;

public class EventMetadata {

    private String sourceService;
    private String traceId;
    private String messageId;
    private String schemaVersion;
    private EventPriority priority;
    private EventSensitivity sensitivity;

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void setPriority(EventPriority priority) {
        this.priority = priority;
    }

    public EventSensitivity getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(EventSensitivity sensitivity) {
        this.sensitivity = sensitivity;
    }

}
