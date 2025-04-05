package org.wire.core.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
	@Schema(example = "40010EV1") String errorCode,
	@Schema(example = "Event Not Found") String message
){
}
