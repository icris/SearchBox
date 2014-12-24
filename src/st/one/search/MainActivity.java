package st.one.search;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.libs.zxing.CaptureActivity;

public class MainActivity extends Activity {
	private BroadcastReceiver receiver;
	private EditText et_bar_keyword;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		setupActionBar();
		setupEditText();
		setupBackEvent();

	}

	private void setupActionBar() {
		ActionBar actionBar = getActionBar();
		if (actionBar == null)
			return;
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.action_bar_custom);
	}

	private void setupEditText() {
		et_bar_keyword = (EditText) findViewById(R.id.et_bar_keyword);
		et_bar_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Editable text = et_bar_keyword.getText();
				if ((actionId == 3) && (text != null)) {
					MainActivity.this.search(text.toString().trim());
					return true;
				}
				return false;
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			et_bar_keyword.setText(extras.getString(Intent.EXTRA_TEXT));
			et_bar_keyword.selectAll();
		}

	}

	private void setupBackEvent() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				MainActivity.this.finish();
			}
		};
		registerReceiver(receiver, new IntentFilter("st.one.icris.finish"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_scanner:
			startActivityForResult(new Intent(this, CaptureActivity.class), 0);
			break;

		case R.id.action_search:
			if (et_bar_keyword.getText() == null)
				return false;
			search(et_bar_keyword.getText().toString().trim());
			return true;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String stringExtra = data.getStringExtra("text");
			if (stringExtra != null) {
				et_bar_keyword.setText(stringExtra);
			}
		}

	}

	private void search(String str) {
		if (TextUtils.isEmpty(str)) {
			openUrl("http://www.baidu.com");
		} else if (str.matches(":set\\s+.*")) {
			set(str.replaceFirst(":set\\s+", ""));
		} else if ((str.matches("https?://.+")) || (str.matches("ftp://.+"))) {
			openUrl(str);
		} else if (str.matches(".*\\.(com|cn|co|hk|tw|me|org|edu|jp|tk|net|gov|info|name|pro).*")) {
			openUrl("http://" + str);
		} else if (str.matches("@[Bb]\\s+.*")) {
			openUrl("abilisearch://" + str.replaceFirst("@[Bb]", "").trim());
		} else if (str.matches("@.*")) {
			openUrl("http://www.google.com/search?q=" + str.replaceFirst("@", "").trim());
		} else {
			searchByDefualt(str);
		}

		this.finish();
	}

	private void set(String str) {
		if (str.equalsIgnoreCase("google")) {
			sp.edit().putString("prefix", "http://www.google.com/search?q=").commit();
		} else if (str.equalsIgnoreCase("baidu")) {
			sp.edit().putString("prefix", "http://www.baidu.com/s?wd=").commit();
		} else {
			sp.edit().putString("prefix", str).commit();
		}
		Toast.makeText(getApplicationContext(), getString(R.string.toast_defualt_prefix) + str,
				Toast.LENGTH_LONG).show();
	}

	private void searchByDefualt(String str) {
		String prefix = sp.getString("prefix", "http://www.baidu.com/s?wd=");
		openUrl(prefix + str.trim());
	}

	private void openUrl(String url) {
		startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
	}

	public void finish(View view) {
		this.finish();
	}

	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm.isActive())
			imm.hideSoftInputFromWindow(et_bar_keyword.getApplicationWindowToken(), 0);
	}

	@Override
	public void finish() {
		hideKeyboard();
		unregisterReceiver(receiver);
		super.finish();
	}

}
