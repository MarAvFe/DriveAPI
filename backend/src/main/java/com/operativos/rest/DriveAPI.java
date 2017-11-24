package com.operativos.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.operativos.driveHandler.*;

@Path("/api")
public class DriveAPI {

	@POST
    @Path("/sum")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response getMsg(@FormParam("msg") String msg) {
		String output = "Jersey say : " + msg;
		return Response.status(200).entity(buildJson(output,false)).build();
	}

	@POST
    @Path("/resetDrive")
	@Produces(MediaType.APPLICATION_JSON)
	public Response restResetDrive() {
		FileSystemInterface.createDrive();
		return Response.status(200).entity(buildJson("Success",false)).build();
	}

	@POST
    @Path("/addUser")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addUser(@FormParam("email") String email, @FormParam("pwd") String pwd, @FormParam("driveSize") String driveSize) {
		int size = Integer.parseInt(driveSize);
		String res = FileSystemInterface.addUser(email, pwd, size);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}

	@POST
    @Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("email") String email, @FormParam("pwd") String pwd) {
		boolean res = FileSystemInterface.login(email, pwd);
		return Response.status(200).entity(buildJson("-",!res)).build();
	}

	@POST
    @Path("/createFile")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createFile(@FormParam("email") String email, @FormParam("fileName") String fileName, @FormParam("content") String content) {
		String res = FileSystemInterface.createFile(email, fileName, content);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}

	@POST
    @Path("/modifyFile")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response modifyFile(@FormParam("email") String email, @FormParam("fileName") String fileName, @FormParam("content") String content) {
		String res = FileSystemInterface.modifyFile(email, fileName, content);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}

	@POST
    @Path("/deleteFile")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteFile(@FormParam("email") String email, @FormParam("fileName") String fileName) {
		String res = FileSystemInterface.deleteFile(email, fileName);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}

	@POST
    @Path("/shareFile")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response shareFile(@FormParam("email") String email, @FormParam("fileName") String fileName, @FormParam("theirMail") String theirMail) {
		String res = FileSystemInterface.shareFile(email, fileName, theirMail);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}

	@POST
    @Path("/createDirectory")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createDirectory(@FormParam("email") String email, @FormParam("dirName") String dirName) {
		String res = FileSystemInterface.createDirectory(email, dirName);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}

	@POST
    @Path("/ls")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response ls(@FormParam("email") String email) {
		String res = FileSystemInterface.ls(email);
		return Response.status(200).entity(buildJson(res,res.equals(""))).build();
	}

	@POST
    @Path("/cd")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response cd(@FormParam("email") String email, @FormParam("path") String path) {
		String res = FileSystemInterface.changeWorkingDirectory(email, path);
		return Response.status(200).entity(buildJson(res,!res.equals("Success"))).build();
	}
	
	private String buildJson(String data, boolean error) {
		return "{\"data\":\"" + data + "\"," + "\"error\":" + error + "}";
	}

}
