/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.reportmailingjob.data;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Immutable data object representing report mailing job run history data.
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public final class ReportMailingJobRunHistoryData {

    private Long id;
    private Long reportMailingJobId;
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
    private String status;
    private String errorMessage;
    private String errorLog;

    /**
     * creates an instance of the ReportMailingJobRunHistoryData class
     *
     * @return ReportMailingJobRunHistoryData object
     **/
    public static ReportMailingJobRunHistoryData newInstance(Long id, Long reportMailingJobId, ZonedDateTime startDateTime,
            ZonedDateTime endDateTime, String status, String errorMessage, String errorLog) {
        return new ReportMailingJobRunHistoryData().setId(id).setReportMailingJobId(reportMailingJobId).setStartDateTime(startDateTime)
                .setEndDateTime(endDateTime).setStatus(status).setErrorMessage(errorMessage).setErrorLog(errorLog);
    }
}
