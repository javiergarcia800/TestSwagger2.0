package com.autentia.tutorial.rest;

import com.autentia.tutorial.rest.data.Message;
import com.autentia.tutorial.rest.data.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users/")
//@Api(value = "/users", description = "Operaciones con usuarios")
@Produces(MediaType.APPLICATION_JSON)
public class UserService /*extends JavaHelp*/ {

    private static final Map<Integer, User> USERS = new HashMap<Integer, User>();
    static {
        USERS.put(1, new User(1, "Juan"));
        USERS.put(2, new User(2, "Pepe"));
        USERS.put(3, new User(3, "Antonio"));
    }

    private static final Response NOT_FOUND = Response.status(Response.Status.NOT_FOUND).
            entity(new Message("El usuario no existe")).
            build();

    private static final Response USER_ALREADY_EXISTS = Response.status(Response.Status.BAD_REQUEST).
            entity(new Message("El usuario ya existe")).
            build();

    private static final Response OK = Response.ok().
            entity(new Message("Operacion realizada corretamente")).
                    build();

    @GET
    @Path("/find/all")
    @Operation(	summary     = "Devuelve todos los usuarios",
    			description = "Devuelve todos los usuarios del sistema")
    public Response findAll() {
    	List<User> users = new ArrayList<User>(USERS.values());
    	GenericEntity< List<User> > entity  = new GenericEntity< List< User > >( users ) { };
    	
    	return Response.ok(entity).build();
    }

    @GET
    @Path("/find/{id}")
    @Operation(	summary = "Busca un usuario por ID",
    			responses = {
    						@ApiResponse(description = "El usuario",
    									 content     = @Content(mediaType = "application/json",
    														    schema    = @Schema(implementation = User.class))),
    						@ApiResponse(responseCode = "400", description = "User not found")})
    public Response findById(@PathParam("id") int id) {
    	User user = USERS.get(id);
    	if (user == null){
    		return NOT_FOUND;
    	}
    	
        return Response.ok().entity(user).build();
    }

    @POST
    @Path("/add")
    @Consumes({MediaType.APPLICATION_JSON})
    /*@ApiOperation(
            value = "Da de alta un nuevo usuario",
            notes = "Crea un nuevo usuario a partir de un ID y un nombre. El usuario no debe existir"
    )*/
    public Response addUser(User newUser) {
    	if (USERS.get(newUser.getId()) != null) {
    		return USER_ALREADY_EXISTS;
    	}
        
    	USERS.put(newUser.getId(), newUser);
        return OK;
    }

    @PUT
    @Path("/update/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Operation(summary = "Actualiza los datos de un usuario",
    			  tags = {"users"},
    			  responses = {
    					  @ApiResponse(
    							  content = @Content(mediaType = "application/json",
    							  schema  = @Schema(implementation = User.class))),
    					  @ApiResponse(responseCode = "400", description = "ID invalido"),
    					  @ApiResponse(responseCode = "404", description = "El usuario no existe"),
    					  @ApiResponse(responseCode = "405", description = "Validation exception") }
    )
    public Response updateUser(@PathParam("id") int id, @RequestBody(description = "Objeto Usuario para ser agregado", required = true,
            														 content     = @Content(
                 schema = @Schema(implementation = User.class)))User userToUpdate) {
    	if (USERS.get(userToUpdate.getId()) == null) {
    		return NOT_FOUND;
    	}
    	
    	USERS.put(userToUpdate.getId(), userToUpdate);
        return OK;
    }
    

    @DELETE
    @Path("/delete/{id}")
    @Operation(summary = "Elimina un usuario")
    public Response removeUser(@Parameter(description = "ID del usuario a eliminar.", required = true) @PathParam("id") int id) {
    	if (USERS.get(id) == null) {
    		return NOT_FOUND;
    	}
    	
    	USERS.remove(id);
        return OK;
    }

}