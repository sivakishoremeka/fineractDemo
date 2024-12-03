package org.apache.fineract.portfolio.service.service;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ServiceMasterWritePlatformService {

	 CommandProcessingResult updateService(Long serviceId, JsonCommand command);

	 CommandProcessingResult deleteService(Long serviceId);

	 CommandProcessingResult createNewService(JsonCommand command);

	CommandProcessingResult addServiceAvailability(JsonCommand command,
			Long addressId);

}
