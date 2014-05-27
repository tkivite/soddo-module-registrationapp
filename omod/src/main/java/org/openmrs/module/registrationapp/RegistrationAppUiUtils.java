/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.registrationapp;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.springframework.validation.BindingResult;

public class RegistrationAppUiUtils {
	
	//In general, the latitude and longitude values can have any number of decimal places 
	//but optional, can start with an optional + or -, can't start with a decimal point,
	//If it has a decimal point, require at least one decimal place
	
	//Whole numbers valid ranges are 0-89 or 90
	public static final String DEFAULT_LATITUDE_REGEX = "[+-]?((([0-8]?[0-9])(\\.\\d+)?)|90(\\.0+)?)";
	
	//Whole numbers valid ranges are 0-179 or 180
	public static final String DEFAULT_LONGITUDE_REGEX = "[+-]?((((1?[0-7]?|[0-9]?)[0-9])(\\.\\d+)?)|180(\\.0+)?)";
	
	/**
	 * Gets the person attribute value for the specified person for the getPersonAttributeTypeByUuid
	 * that matches the specified uuid
	 * 
	 * @return the attribute value
	 */
	public String getAttribute(Person person, String attributeTypeUuid) {
		if (person != null) {
			PersonAttribute attr = person.getAttribute(Context.getPersonService().getPersonAttributeTypeByUuid(
			    attributeTypeUuid));
			if (attr != null) {
				return attr.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Validates the specified latitude value against the default regex
	 * #DEFAULT_LATITUDE_REGEX_FORMAT
	 * 
	 * @param latitude
	 * @return true if the latitude value if true otherwise false
	 * @should pass for a valid latitude value
	 * @should fail for an invalid latitude value
	 */
	public static boolean isValidLatitude(String latitude) {
		return isValid(latitude, DEFAULT_LATITUDE_REGEX);
	}
	
	/**
	 * Validates the specified longitude value against the default regex
	 * #DEFAULT_LONGITUDE_REGEX_FORMAT
	 * 
	 * @param longitude
	 * @return true if the latitude value if true otherwise false
	 * @should pass for a valid longitude value
	 * @should fail for an invalid longitude value
	 */
	public static boolean isValidLongitude(String longitude) {
		return isValid(longitude, DEFAULT_LONGITUDE_REGEX);
	}
	
	private static boolean isValid(String value, String regex) {
		return Pattern.compile(regex).matcher(value).matches();
	}
	
	public static void validateLatitudeAndLongitudeIfNecessary(PersonAddress address, BindingResult errors) {
		if (address != null) {
			AddressTemplate template = AddressSupport.getInstance().getAddressTemplate().get(0);
			if (StringUtils.isNotBlank(address.getLatitude())) {
				if (template.getElementRegex() != null && StringUtils.isBlank(template.getElementRegex().get("latitude"))) {
					if (!RegistrationAppUiUtils.isValidLatitude(address.getLatitude())) {
						errors.reject("registrationapp.latitude.invalid");
					}
				}
			}
			
			if (StringUtils.isNotBlank(address.getLongitude())) {
				if (template.getElementRegex() != null && StringUtils.isBlank(template.getElementRegex().get("longitude"))) {
					if (!RegistrationAppUiUtils.isValidLongitude(address.getLongitude())) {
						errors.reject("registrationapp.longitude.invalid");
					}
				}
			}
		}
	}
}
