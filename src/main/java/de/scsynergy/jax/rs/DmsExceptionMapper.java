package de.scsynergy.jax.rs;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author rf
 */
@Provider
public class DmsExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(DmsExceptionMapper.class.getName());

    @Override
    public Response toResponse(final Exception ex) {
        LOG.log(Level.SEVERE, null, ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
