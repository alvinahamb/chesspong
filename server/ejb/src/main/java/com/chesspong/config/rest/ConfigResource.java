package com.chesspong.config.rest;

import com.chesspong.config.dto.ConfigDTO;
import com.chesspong.config.ejb.ConfigServiceRemote;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {

    @EJB
    private ConfigServiceRemote configService;

    @POST
    public Response create(ConfigDTO config) {
        ConfigDTO created = configService.create(config);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") int id) {
        ConfigDTO config = configService.getOne(id);
        if (config != null) {
            return Response.ok(config).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    public List<ConfigDTO> getAll() {
        return configService.getAll();
    }

    @GET
    @Path("/getLast")
    public Response getLast() {
        ConfigDTO config = configService.getLast();
        if (config != null) {
            return Response.ok(config).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, ConfigDTO config) {
        config.setId(id);
        ConfigDTO updated = configService.update(config);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        boolean deleted = configService.delete(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

