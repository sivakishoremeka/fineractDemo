package org.apache.fineract.portfolio.service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.tenant.SearchSqlQuery;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.service.data.ServiceMasterData;
import org.apache.fineract.portfolio.service.data.ServiceMasterOptionsData;
import org.apache.fineract.portfolio.service.service.ServiceMasterReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/servicemasters")
@Component
@Tag(name = "Service", description = "This defines the Service Components")
@RequiredArgsConstructor
public class ServiceMasterApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","serviceType","serviceCode","serviceDescription","serviceTypes",
			"serviceUnitTypes","serviceUnitTypes","isOptional","status","serviceStatus","isAutoProvision","serviceCategory"));
        private final static String RESOURCENAMEFORPERMISSIONS = "SERVICE";
        private final static  String RESOURCE_TYPE = "SERVICE";
	    private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<ServiceMasterOptionsData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	    private final DefaultToApiJsonSerializer<ServiceMasterData> toApiJsonSerializer2;
		
	/**
	 * using this method posting  service data 
	 */	 
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Create a new Service", description = "Create a new Service\n\n"
			+ "Mandatory Fields: name, percentage\n\n"
			+ "Optional Fields: debitAccountType, debitAcountId, creditAccountType, creditAcountId, startDate")
	//@RequestBody(required = true, content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.PostTaxesComponentsRequest.class)))
	//@ApiResponses({
	//		@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = TaxComponentApiResourceSwagger.PostTaxesComponentsResponse.class))) })
	public String createNewService(@Parameter(hidden = true) final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createService().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer2.serialize(result);
	}
	
	/**
	 * using this method getting  all services data 
	 */	
	 	@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllService(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
				@QueryParam("offset") final Integer offset,@QueryParam("serviceCategory") final String serviceCategory) {
	 		
		   this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
			//final Collection<ServiceMasterOptionsData> masterOptionsDatas = this.serviceMasterReadPlatformService.retrieveServices();
		   final SearchSqlQuery searchCodes =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		   final Page<ServiceMasterOptionsData> masterOptionsDatas = this.serviceMasterReadPlatformService.retrieveServices(searchCodes,serviceCategory);
		   return this.toApiJsonSerializer.serialize(masterOptionsDatas);
		}

	     /**
		 * using this method getting  template data for service
		 */	
	    @GET
	    @Path("template")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveTempleteInfo(@Context final UriInfo uriInfo) {
		 context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		 final ServiceMasterOptionsData masterOptionsData=handleTemplateData(null);
		 final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings, masterOptionsData, RESPONSE_DATA_PARAMETERS);
		}
	    
	    
	 /**
	 * this method for getting  service data using by id
	 */	
	    @GET
		@Path("{serviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveSingleServiceDetails(@PathParam("serviceId") final Long serviceId, @Context final UriInfo uriInfo) {
			this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
			ServiceMasterOptionsData serviceMasterOptionsData = this.serviceMasterReadPlatformService.retrieveIndividualService(serviceId);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			if(serviceMasterOptionsData != null){
				serviceMasterOptionsData.setServiceDetailData(this.serviceMasterReadPlatformService.retrieveServiceDetails(serviceId,null));
				if(settings.isTemplate()){
					serviceMasterOptionsData = this.handleTemplateData(serviceMasterOptionsData);
				}
			}
	        return this.toApiJsonSerializer.serialize(settings, serviceMasterOptionsData, RESPONSE_DATA_PARAMETERS);
		}
	    
	    private ServiceMasterOptionsData handleTemplateData(ServiceMasterOptionsData serviceMasterOptionsData) {
			 if(serviceMasterOptionsData == null){
				 serviceMasterOptionsData = new ServiceMasterOptionsData();
			 }
			 //serviceMasterOptionsData.setServiceTypes(this.enumReadplaformService.getEnumValues("service_type"));
			 //serviceMasterOptionsData.setStatus(this.planReadPlatformService.retrieveNewStatus());
			// serviceMasterOptionsData.setServiceParamsData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SERVICE_PARAMS));
			 serviceMasterOptionsData.setServiceCategorys(this.serviceMasterReadPlatformService.retriveServices("S"));
			 return serviceMasterOptionsData;
			
		 }
	
	 /**
	 * using this method editing single service data 
	 */	
	    @PUT
		@Path("{serviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String updateServiceData(@PathParam("serviceId") final Long serviceId, final String apiRequestBodyAsJson){

		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateService(serviceId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
		}
	 
	 /**
	 * using this method deleting single service data 
	 */	
 	    @DELETE
		@Path("{serviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String deleteServiceData(@PathParam("serviceId") final Long serviceId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteService(serviceId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);

		}
 	    
 	   @GET
 	   @Path("{serviceId}/{paramCategory}")
 	   @Consumes({MediaType.APPLICATION_JSON})
 	   @Produces({MediaType.APPLICATION_JSON})
 	   public String retrieveServiceDetails(@PathParam("serviceId") final Long serviceId,
 	  @PathParam("paramCategory") final String paramCategory,@Context final UriInfo uriInfo) {
 		   
 	  this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
 	  ServiceMasterOptionsData serviceMasterOptionsData = new  ServiceMasterOptionsData();
 	  serviceMasterOptionsData.setServiceDetailData(this.serviceMasterReadPlatformService.retrieveServiceDetailsAgainestMasterIdandParamCategory(serviceId,paramCategory));
 	  
 	  final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
 	  return this.toApiJsonSerializer.serialize(settings, serviceMasterOptionsData, RESPONSE_DATA_PARAMETERS);
   }
 	   
 	   
 	   
 		@GET
 		@Path("services/{serialNumber}")
 		@Consumes({ MediaType.APPLICATION_JSON })
 		@Produces({ MediaType.APPLICATION_JSON })
 		public String retriveServicesByUsingSerailNumbers(@PathParam("serialNumber") final String serialNumber, @QueryParam("serviceType") final String serviceType,@QueryParam("serviceParamName") final String serviceParamName,@Context final UriInfo uriInfo){
 			/*this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);*/
 			ServiceMasterOptionsData serviceMasterOptionsData = this.serviceMasterReadPlatformService.retriveServicesByUsingSerailNumber(serialNumber,serviceType ,serviceParamName);
 			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
 			return this.toApiJsonSerializer.serialize(serviceMasterOptionsData);
 			
 		}
 	   
}

	   
