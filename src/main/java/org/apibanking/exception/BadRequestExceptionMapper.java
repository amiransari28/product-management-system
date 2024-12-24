package org.apibanking.exception;

import org.apibanking.dto.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

//	@Override
//	public Response toResponse(BadRequestException exception) {
//		// Custom 400 response for InvalidProductIdException
//		return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(exception.getMessage())).build();
//	}
	
	@Override
    public Response toResponse(BadRequestException exception) {
        // Return a custom error message, not including the stack trace
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(errorResponse)
                       .build();
    }
}
