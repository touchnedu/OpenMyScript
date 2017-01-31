package com.touchnedu.gradea.studya.math.modules;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountInfo {
	
	public static String getAccountInfo(Context context) {
		AccountManager am = AccountManager.get(context);
		Account[] accounts = am.getAccounts();
		Account account = null;
		final int count = accounts.length;
		String accountInfo = "";

		for(int i = 0; i < count; i++) {
			account = accounts[i];
			if(account.type.equals("com.google")) {
//				SharedPreferencesManager.getInstance().setGoogleAccount(accounts[i].name);
//				SharedPreferencesManager.getInstance().saveProperties(context);
				accountInfo = account.name;
			}
		}
		return accountInfo;
	}
	
}
