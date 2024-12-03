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
package org.apache.fineract.accounting.producttoaccountmapping.service;

import java.util.List;
import java.util.Map;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeOffReasonToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.ChargeToGLAccountMapper;
import org.apache.fineract.accounting.producttoaccountmapping.data.PaymentTypeToGLAccountMapper;

public interface ProductToGLAccountMappingReadPlatformService {

    Map<String, Object> fetchAccountMappingDetailsForLoanProduct(Long loanProductId, Integer accountingType);

    List<PaymentTypeToGLAccountMapper> fetchPaymentTypeToFundSourceMappingsForLoanProduct(Long loanProductId);

    List<ChargeToGLAccountMapper> fetchFeeToGLAccountMappingsForLoanProduct(Long loanProductId);

    List<ChargeToGLAccountMapper> fetchPenaltyToIncomeAccountMappingsForLoanProduct(Long loanProductId);

    Map<String, Object> fetchAccountMappingDetailsForSavingsProduct(Long savingsProductId, Integer accountingType);

    List<PaymentTypeToGLAccountMapper> fetchPaymentTypeToFundSourceMappingsForSavingsProduct(Long savingsProductId);

    List<ChargeToGLAccountMapper> fetchFeeToIncomeAccountMappingsForSavingsProduct(Long savingsProductId);

    List<ChargeToGLAccountMapper> fetchPenaltyToIncomeAccountMappingsForSavingsProduct(Long savingsProductId);

    Map<String, Object> fetchAccountMappingDetailsForShareProduct(Long productId, Integer accountingType);

    List<PaymentTypeToGLAccountMapper> fetchPaymentTypeToFundSourceMappingsForShareProduct(Long productId);

    List<ChargeToGLAccountMapper> fetchFeeToIncomeAccountMappingsForShareProduct(Long productId);

    List<ChargeOffReasonToGLAccountMapper> fetchChargeOffReasonMappingsForLoanProduct(Long loanProductId);
}