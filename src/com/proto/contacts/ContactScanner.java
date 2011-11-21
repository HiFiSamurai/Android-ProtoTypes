package com.proto.contacts;

import java.util.ArrayList;
import java.util.List;

import com.proto.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class ContactScanner extends Activity {

	private ListView contactList;
	private List<String> emails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.contact_layout);
		
		contactList = (ListView) findViewById(R.id.contactList);
		
		scanEmails();
	}
	
	private void scanEmails() {
		ContentResolver cr = getContentResolver();
        Cursor emailCursor = cr.query(Email.CONTENT_URI,null,null,null,null);
		startManagingCursor(emailCursor);
		emailCursor.moveToFirst();
		
		emails = new ArrayList<String>();
		while (!emailCursor.isAfterLast()) {
			emails.add(emailCursor.getString(emailCursor.getColumnIndex(Email.DATA)));
			emailCursor.moveToNext();
		}
		
		contactList.setAdapter(new ContactListAdapter(this,emails));
	}
}
