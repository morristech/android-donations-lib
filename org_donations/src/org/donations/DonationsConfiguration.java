/*
 * Copyright (C) 2011 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.donations;

public class DonationsConfiguration {

	public static final String TAG = "Donations";

	public static final boolean DEBUG = false;

	/** PayPal */

	public static final String PAYPAL_USER = "chris@senab.co.uk";
	public static final String PAYPAL_ITEM_NAME = "photup Donation";
	public static final String PAYPAL_CURRENCY_CODE = "GBP";

	/** Google Play Store In-App Billing */

	// your public key from the google play publisher account
	public static final String GOOGLE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjvKTS4cvWomWPyUU8SHCG58w3YsYbi5LkJyt92UrwxArABGNw1VKPoxDd4usDipExdrwmtiLcslqacc/OAqyuna/ug7Jikk+pL+DJBw3D1kctMjeZmuETUWA32ak3k0Kc5RIFjcKQ6Moi05texXbVgey3rv7NjZIwqAsHnTQDBRPd7XefqrTuKW7mZ6LQmz7BZhsjxR2TltFa7/xNVLlDtEz+DF3BtLCnA/Dl6J4PKCkNA3AufZ39rOJZ9hR6HNx+p+1g2b/QSrev0aXwey9x7WW/amXrXxc/ZsQtC03Ca/OjmfjNLZiGnHRY1UTdpqY+WXAh2lmVZM06mcTMVErZwIDAQAB";

	public static final String[] GOOGLE_CATALOG = new String[] {
			"photup.donation.0", "photup.donation.1", "photup.donation.2",
			"photup.donation.3", "photup.donation.5", "photup.donation.8",
			"photup.donation.10" };
}
