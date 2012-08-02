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

import org.donations.google.BillingService;
import org.donations.google.BillingService.RequestPurchase;
import org.donations.google.BillingService.RestoreTransactions;
import org.donations.google.Consts;
import org.donations.google.Consts.PurchaseState;
import org.donations.google.Consts.ResponseCode;
import org.donations.google.PurchaseObserver;
import org.donations.google.ResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DonationsActivity extends Activity {
	private DonatePurchaseObserver mDonatePurchaseObserver;
	private Handler mHandler;

	private Spinner mGoogleAndroidMarketSpinner;

	private BillingService mBillingService;

	private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 1;

	/** An array of product list entries for the products that can be purchased. */
	private static final String[] CATALOG = DonationsConfiguration.GOOGLE_CATALOG;

	private static final String[] CATALOG_DEBUG = new String[] {
			"android.test.purchased", "android.test.canceled",
			"android.test.refunded", "android.test.item_unavailable" };

	/**
	 * A {@link PurchaseObserver} is used to get callbacks when Android Market
	 * sends messages to this application so that we can update the UI.
	 */
	private class DonatePurchaseObserver extends PurchaseObserver {
		public DonatePurchaseObserver(Handler handler) {
			super(DonationsActivity.this, handler);
		}

		@Override
		public void onBillingSupported(boolean supported) {
			Log.d(DonationsConfiguration.TAG, "supported: " + supported);
			if (!supported) {
				showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
			}
		}

		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState,
				String itemId, final String orderId, long purchaseTime,
				String developerPayload) {
			Log.d(DonationsConfiguration.TAG,
					"onPurchaseStateChange() itemId: " + itemId + " "
							+ purchaseState);
		}

		@Override
		public void onRequestPurchaseResponse(RequestPurchase request,
				ResponseCode responseCode) {
			Log.d(DonationsConfiguration.TAG, request.mProductId + ": "
					+ responseCode);
			if (responseCode == ResponseCode.RESULT_OK) {
				Log.d(DonationsConfiguration.TAG,
						"purchase was successfully sent to server");
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						DonationsActivity.this);
				dialog.setIcon(android.R.drawable.ic_dialog_info);
				dialog.setTitle(R.string.donations__thanks_dialog_title);
				dialog.setMessage(R.string.donations__thanks_dialog);
				dialog.setCancelable(true);
				dialog.setNeutralButton(R.string.donations__button_close,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog.show();
			} else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
				Log.d(DonationsConfiguration.TAG, "user canceled purchase");
			} else {
				Log.d(DonationsConfiguration.TAG, "purchase failed");
			}
		}

		@Override
		public void onRestoreTransactionsResponse(RestoreTransactions request,
				ResponseCode responseCode) {
			if (responseCode == ResponseCode.RESULT_OK) {
				Log.d(DonationsConfiguration.TAG,
						"completed RestoreTransactions request");
			} else {
				Log.d(DonationsConfiguration.TAG, "RestoreTransactions error: "
						+ responseCode);
			}
		}
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.donations__activity);

		// choose donation amount
		mGoogleAndroidMarketSpinner = (Spinner) findViewById(R.id.donations__google_android_market_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.donations__google_android_market_promt_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mGoogleAndroidMarketSpinner.setAdapter(adapter);
		mGoogleAndroidMarketSpinner.setSelection(2);

		mHandler = new Handler();
		mDonatePurchaseObserver = new DonatePurchaseObserver(mHandler);
		mBillingService = new BillingService();
		mBillingService.setContext(this);
	}

	/**
	 * Donate button executes donations based on selection in spinner
	 * 
	 * @param view
	 */
	public void donateGoogleOnClick(View view) {
		final int index;
		index = mGoogleAndroidMarketSpinner.getSelectedItemPosition();
		Log.d(DonationsConfiguration.TAG, "selected item in spinner: " + index);

		if (!Consts.DEBUG) {
			if (!mBillingService.requestPurchase(CATALOG[index], null)) {
				showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
			}
		} else {
			// when debugging, choose android.test.x item
			if (!mBillingService.requestPurchase(CATALOG_DEBUG[0], null)) {
				showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
			}
		}
	}

	/**
	 * Donate button with PayPal by opening browser with defined URL For
	 * possible parameters see:
	 * https://cms.paypal.com/us/cgi-bin/?cmd=_render-content
	 * &content_ID=developer/e_howto_html_Appx_websitestandard_htmlvariables
	 * 
	 * @param view
	 */
	public void donatePayPalOnClick(View view) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme("https").authority("www.paypal.com")
				.path("cgi-bin/webscr");
		uriBuilder.appendQueryParameter("cmd", "_donations");
		uriBuilder.appendQueryParameter("business",
				DonationsConfiguration.PAYPAL_USER);
		uriBuilder.appendQueryParameter("lc", "US");
		uriBuilder.appendQueryParameter("item_name",
				DonationsConfiguration.PAYPAL_ITEM_NAME);
		uriBuilder.appendQueryParameter("no_note", "1");
		// uriBuilder.appendQueryParameter("no_note", "0");
		// uriBuilder.appendQueryParameter("cn", "Note to the developer");
		uriBuilder.appendQueryParameter("no_shipping", "1");
		uriBuilder.appendQueryParameter("currency_code",
				DonationsConfiguration.PAYPAL_CURRENCY_CODE);
		// uriBuilder.appendQueryParameter("bn",
		// "PP-DonationsBF:btn_donate_LG.gif:NonHosted");
		Uri payPalUri = uriBuilder.build();

		if (DonationsConfiguration.DEBUG) {
			Log.d(DonationsConfiguration.TAG,
					"Opening the browser with the url: " + payPalUri.toString());
		}

		// Start your favorite browser
		Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
		startActivity(viewIntent);
	}

	/**
	 * Called when this activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		ResponseHandler.register(mDonatePurchaseObserver);
	}

	/**
	 * Called when this activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		ResponseHandler.unregister(mDonatePurchaseObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBillingService.unbind();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_BILLING_NOT_SUPPORTED_ID:
			return createDialog(
					getString(R.string.donations__google_android_market_not_supported_title),
					getString(R.string.donations__google_android_market_not_supported));
		default:
			return null;
		}
	}

	/**
	 * Build dialog based on strings
	 */
	private Dialog createDialog(String string, String string2) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(string)
				.setIcon(android.R.drawable.stat_sys_warning)
				.setMessage(string2)
				.setCancelable(false)
				.setPositiveButton(R.string.donations__button_close,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		return builder.create();
	}

}
