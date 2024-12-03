package org.apache.fineract.portfolio.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.codes.exception.CodeNotFoundException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.service.domain.ServiceAvailabilityRepository;
import org.apache.fineract.portfolio.service.domain.ServiceAvailabilty;
import org.apache.fineract.portfolio.service.domain.ServiceDetails;
import org.apache.fineract.portfolio.service.domain.ServiceMaster;
import org.apache.fineract.portfolio.service.domain.ServiceMasterDetailsRepository;
import org.apache.fineract.portfolio.service.domain.ServiceMasterRepository;
import org.apache.fineract.portfolio.service.serialization.ServiceCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
@RequiredArgsConstructor
public class ServiceMasterWritePlatformServiceImpl  implements ServiceMasterWritePlatformService{
	
	private final static Logger logger = LoggerFactory.getLogger(ServiceMasterWritePlatformServiceImpl.class);
	private final ServiceMasterRepository serviceMasterRepository;
	private final ServiceCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final ServiceMasterDetailsRepository serviceMasterDetailsRepository;
	private final ServiceAvailabilityRepository serviceAvailabilityRepository;
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	

    @Transactional
	@Override
	public CommandProcessingResult createNewService(final JsonCommand command) {
		try {
			   this.fromApiJsonDeserializer.validateForCreate(command.json());
			   ServiceMaster serviceMaster = ServiceMaster.fromJson(command);
			   final JsonArray serviceChildArray = command.arrayOfParameterNamed("serviceArray").getAsJsonArray();
			   serviceMaster = this.assembleDetails(serviceChildArray,serviceMaster);
			   this.serviceMasterRepository.saveAndFlush(serviceMaster);
			   return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(serviceMaster.getId()) //
					.build();
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return  CommandProcessingResult.empty();
		}
	}
    
    private ServiceMaster assembleDetails(JsonArray serviceChildArray, ServiceMaster serviceMaster) {
		
		String[]  childServices = null;
		childServices = new String[serviceChildArray.size()];
		if(serviceChildArray.size() > 0){
			for(int i = 0; i < serviceChildArray.size(); i++){
				childServices[i] = serviceChildArray.get(i).toString();
			}
	
		for (final String childService : childServices) {
			final JsonElement element = fromApiJsonHelper.parse(childService);
			final String paramName = fromApiJsonHelper.extractStringNamed("paramName", element);
			final String paramType = fromApiJsonHelper.extractStringNamed("paramType", element);
			final String paramValue = fromApiJsonHelper.extractStringNamed("paramValue", element);
			final String paramCategory = fromApiJsonHelper.extractStringNamed("paramCategory", element);
			
			ServiceDetails serviceDetails = new ServiceDetails(paramName, paramType,paramValue,paramCategory);
			serviceMaster.addDetails(serviceDetails);
			
		}	 
	}	
	
	return serviceMaster;
}
    
	@Override
	public CommandProcessingResult updateService(final Long serviceId,final JsonCommand command) {
		
		try{
			    this.context.authenticatedUser();
			    this.fromApiJsonDeserializer.validateForCreate(command.json());
		        final ServiceMaster serviceMaster = retrieveCodeBy(serviceId);
		        List<ServiceDetails> details=new ArrayList<>(serviceMaster.getServiceDetails());
		        final JsonArray serviceChildArray = command.arrayOfParameterNamed("serviceArray").getAsJsonArray();
		        String[] service =null;
		        service=new String[serviceChildArray.size()];
			    for(int i=0; i<serviceChildArray.size();i++){
			    	service[i] =serviceChildArray.get(i).toString();
			     }
				 for (String serviceData : service) {
					  
					    final JsonElement element = fromApiJsonHelper.parse(serviceData);
					    final Long id = fromApiJsonHelper.extractLongNamed("id", element);
						final String paramName = fromApiJsonHelper.extractStringNamed("paramName", element);
						final String paramType = fromApiJsonHelper.extractStringNamed("paramType", element);
						final String paramValue = fromApiJsonHelper.extractStringNamed("paramValue", element);
						final String paramCategory = fromApiJsonHelper.extractStringNamed("paramCategory", element);
						if(id != null){
						Optional<ServiceDetails> serviceDetailsOptional =this.serviceMasterDetailsRepository.findById(id);
						if(serviceDetailsOptional.isPresent()){
							ServiceDetails serviceDetails = serviceDetailsOptional.get();
							serviceDetails.setParamName(paramName);
							serviceDetails.setParamType(paramType);
							serviceDetails.setParamValue(paramValue);
							serviceDetails.setParamCategory(paramCategory);
							this.serviceMasterDetailsRepository.saveAndFlush(serviceDetails);
							if(details.contains(serviceDetails)){
							   details.remove(serviceDetails);
							}
						}
						}else {
							ServiceDetails newDetails = new ServiceDetails(paramName, paramType,paramValue,paramCategory);
							serviceMaster.addDetails(newDetails);
						}
						
				  }
				 serviceMaster.getServiceDetails().removeAll(details);
				 
		        final Map<String, Object> changes = serviceMaster.update(command);
	            if (!changes.isEmpty()) {
	                this.serviceMasterRepository.saveAndFlush(serviceMaster);
	            }
	            
         return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(serviceId).with(changes).build();
         
	} catch (DataIntegrityViolationException dve) {
		 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder() //
					.withEntityId(Long.valueOf(-1)) //
					.build();
	}
	}
	 private void handleCodeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
	        final Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("service_code_key")) {
	            final String name = command.stringValueOfParameterNamed("serviceCode");
	            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name'"
	                    + name + "'already exists", "displayName", name);
	        }

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
	    }
	private ServiceMaster retrieveCodeBy(final Long serviceId) {
	        Optional<ServiceMaster>  serviceMasterOptional = this.serviceMasterRepository.findById(serviceId);
	        return serviceMasterOptional.orElseThrow(() ->new CodeNotFoundException(serviceId.toString()));
	    }
	 
	 
	@Override
	public CommandProcessingResult deleteService(final Long serviceId) {
				
		    this.context.authenticatedUser();
	        final ServiceMaster serviceMaster = retrieveCodeBy(serviceId);
	        serviceMaster.delete();
			this.serviceMasterRepository.save(serviceMaster);
	        return new CommandProcessingResultBuilder().withEntityId(serviceId).build();
	    }

	@Override
	public CommandProcessingResult addServiceAvailability(final JsonCommand command ,final Long addressId) {
	
		this.context.authenticatedUser();
		ServiceAvailabilty serviceAvailabilty = null;
		this.jdbcTemplate.update("delete from b_service_availability where level_id = ? and level=?",addressId,command.stringValueOfParameterNamed("addressType"));
		String[] servicesArray = command.arrayValueOfParameterNamed("services");
		for(String service: servicesArray){
			serviceAvailabilty = new ServiceAvailabilty(addressId, Long.valueOf(service), command.stringValueOfParameterNamed("addressType"));
			this.serviceAvailabilityRepository.save(serviceAvailabilty);
		}
		return new CommandProcessingResultBuilder().withEntityId(addressId).build();
	}
	
	
}


