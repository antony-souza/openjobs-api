package com.antony.openjobs.modules.jobs.usecase.capabilities;

public record FindJobCapabilitiesResponse(boolean canPublish, boolean canManage, boolean canEdit) {}
