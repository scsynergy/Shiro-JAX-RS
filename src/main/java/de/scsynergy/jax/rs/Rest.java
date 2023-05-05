package de.scsynergy.jax.rs;

import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import static org.jboss.resteasy.util.HttpHeaderNames.EXPECT;

/**
 *
 * @author
 */
@Path(Rest.ROOT)
public class Rest {

    @Inject
    private Subject subject;
    public static final String AREA = "_";
    protected static final String ROOT = "webdav";
    public static final String BUCKET = "bucket";
    public static final String BUCKETPARAM = "/{" + BUCKET + "}";
    public static final String DOCID = "docId";
    public static final String DOCPATH = "/{" + DOCID + ":.*}";

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Consumes(MediaType.WILDCARD)
    public Response get() {
        getUser();
        return Response.ok("HTTP GET").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Consumes(MediaType.WILDCARD)
    @Path(BUCKETPARAM)
    public Response getBucket(
            @PathParam(BUCKET) String bucket,
            InputStream body,
            @HeaderParam(CONTENT_LENGTH) long contentLength,
            @HeaderParam(CONTENT_TYPE) String contentType,
            @HeaderParam("filename") String filename,
            @HeaderParam(EXPECT) String expect,
            @Context UriInfo uriInfo) {
        getUser();
        return Response.ok("HTTP GET bucket " + bucket).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Consumes(MediaType.WILDCARD)
    @Path(BUCKETPARAM + DOCPATH)
    public Response getBucketDoc(
            @PathParam(BUCKET) String bucket,
            @PathParam(DOCID) PathSegment docName,
            InputStream body,
            @HeaderParam(CONTENT_LENGTH) long contentLength,
            @HeaderParam(CONTENT_TYPE) String contentType,
            @HeaderParam("filename") String filename,
            @HeaderParam(EXPECT) String expect,
            @Context UriInfo uriInfo) {
        getUser();
        return Response.ok("HTTP GET bucket " + bucket + " / " + docName).build();
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML})
    @Consumes(MediaType.WILDCARD)
    public Response put(
            InputStream body,
            @HeaderParam(CONTENT_LENGTH) long contentLength,
            @HeaderParam(CONTENT_TYPE) String contentType,
            @HeaderParam("filename") String filename,
            @HeaderParam(EXPECT) String expect,
            @Context UriInfo uriInfo) {
        getUser();
        try {
            return Response.status(Response.Status.CREATED).entity("HTTP PUT " + body.readAllBytes().length).build();
        } catch (IOException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NullPointerException ex) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML})
    @Consumes(MediaType.WILDCARD)
    @Path(BUCKETPARAM)
    public Response putBucket(
            @PathParam(BUCKET) String bucket,
            InputStream body,
            @HeaderParam(CONTENT_LENGTH) long contentLength,
            @HeaderParam(CONTENT_TYPE) String contentType,
            @HeaderParam("filename") String filename,
            @HeaderParam(EXPECT) String expect,
            @Context UriInfo uriInfo) {
        getUser();
        try {
            return Response.status(Response.Status.CREATED).entity("HTTP PUT " + body.readAllBytes().length + " bucket " + bucket).build();
        } catch (IOException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NullPointerException ex) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML})
    @Consumes(MediaType.WILDCARD)
    @Path(BUCKETPARAM + DOCPATH)
    public Response putBucketDoc(
            @PathParam(BUCKET) String bucket,
            @PathParam(DOCID) PathSegment docName,
            InputStream body,
            @HeaderParam(CONTENT_LENGTH) long contentLength,
            @HeaderParam(CONTENT_TYPE) String contentType,
            @HeaderParam("filename") String filename,
            @HeaderParam(EXPECT) String expect,
            @Context UriInfo uriInfo) {
        getUser();
        try {
            if (uriInfo.getPathSegments().size() > 3) {
                PathSegment parentPath = uriInfo.getPathSegments().get(uriInfo.getPathSegments().size() - 2);
                return Response.status(Response.Status.CREATED).entity("HTTP PUT " + body.readAllBytes().length + " bucket " + bucket + " / " + parentPath + " / " + docName).build();
            }
            return Response.status(Response.Status.CREATED).entity("HTTP PUT " + body.readAllBytes().length + " bucket " + bucket + " / " + docName).build();
        } catch (IOException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NullPointerException ex) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    public void getUser() {
        if (!subject.isAuthenticated() || subject.getPrincipal() == null) {
            throw new UnauthenticatedException();
        }
    }
}
