/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.apache.fineract.portfolio.service.handler;

import org.apache.fineract.portfolio.service.service.ServiceMasterWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


@Service
@CommandType(entity = "SERVICE", action = "CREATE")
public class CreateServiceCommandHandler implements NewCommandSourceHandler {

    private final ServiceMasterWritePlatformService writePlatformService;

    @Autowired
    public CreateServiceCommandHandler(final ServiceMasterWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.createNewService(command);
    }
}