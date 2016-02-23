package com.fimtrus.imageupload.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fimtrus.imageupload.R;
import com.fimtrus.imageupload.util.WebViewImageUploadHelper;
import com.fimtrus.imageupload.util.WebViewInterface;

/**
 * MainActivity.java
 * 
 * 웹뷰를 포함하고 있는 액티비티. 모든 웹페이지는 여기서 보인다.
 * 
 * @auther jong-hyun.jeong
 * @date 2014. 7. 10.
 */
@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	private final static int FILECHOOSER_RESULTCODE = 10001;			//파일 선택 RESULT CODE

	private ValueCallback<Uri> mUploadMessage;							//웹뷰를 통해 파일 업로드를 할때 사용한다.
	
	public File mTempFile;												//openFileChooser를 통해 카메라를 호출했을 때. 저장할 파일 경로.
	private WebView mWebView;
	private ProgressBar mLoadingProgressBar = null;						//웹페이지가 로딩될때 상단의 프로그래스바.
	
	
	private boolean isInitialized = false;

	private WebViewInterface mWebViewInterface;
	// String urlAddress = "http://10.0.0.30:8080/kshybrid/index.jsp";
	// String urlAddress = "http://192.168.43.57:8080/test/index.html";



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.test_activity_main);
		init();
		isInitialized = true;
		ActionBar actionBar = getActionBar();
		actionBar.hide();
	}

	private void init() {
		
		mWebView = (WebView) findViewById(R.id.webview);
		mLoadingProgressBar = (ProgressBar) findViewById(R.id.progressbar_Horizontal);
		initializeWebView(mWebView);
		
	}
	/**
	 * 웹뷰 초기화
	 * @param mWebView2
	 * 
	 */
	private void initializeWebView(WebView mWebView2) {
		
		WebSettings webSettings = mWebView.getSettings();
		// 콜백함수 호출
		mWebView.setWebChromeClient(new CustomWebChromeClient());

		mWebView.setWebViewClient(new AxaWebViewClient());

		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setAppCacheEnabled(false);
		webSettings.setLoadsImagesAutomatically(true);

		// mSettings.setSupportZoom(false);
		// mSettings.setBuiltInZoomControls(false);
		webSettings.setUseWideViewPort(false);

		webSettings.setDomStorageEnabled(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setDatabasePath(getFilesDir() + "/databases/");
		
		
//		setOverICS(webSettings);
		
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			webSettings.setAllowFileAccessFromFileURLs(true);
			webSettings.setAllowUniversalAccessFromFileURLs(true);
		}
		

		// mSettings.setUseWideViewPort(true);
		webSettings.setDefaultTextEncodingName("utf-8");

		// webSettings.setUserAgentString(webSettings.getUserAgentString().replace("Mobile",
		// "MobileApp"));
		webSettings.setUserAgentString(webSettings.getUserAgentString().replace("Android", "MobileApp Android").replace("Chrome", ""));

		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setNetworkAvailable(true);

		mWebViewInterface = new WebViewInterface(this, mWebView, getIntent());
		mWebView.addJavascriptInterface(mWebViewInterface, "Android");
		
		mWebView.loadUrl("http://andychoi.gonetis.com:8888/upex.php");

		// 자바스크립트 인터페이스 설정F
//
//		mWebView.addJavascriptInterface(KHjs, "KSHybrid_Android");

	}

	/**
	 * CustomWebChromeClient.java
	 *
	 * 브라우저 UI에 progress변화나 Javascript alert같은 액션을 받아주는 역할
	 *
	 * @auther jong-hyun.jeong
	 * @date 2014. 7. 10.
	 */
	public class CustomWebChromeClient extends WebChromeClient {

		// 페이지 로딩시 프로그래스바 표시
		@Override
		public void onProgressChanged(WebView view, int progress) {
			super.onProgressChanged(view, progress);
			mLoadingProgressBar.setProgress(progress);
			// 페이지 로딩완료 후 프로그래스바 숨김
			if (progress == 100) {
				mLoadingProgressBar.setVisibility(View.GONE);
			} else {
				mLoadingProgressBar.setVisibility(View.VISIBLE);
			}
		}

		// 자바스크립트 에러 발생 시 로그 출력부
		public boolean onConsoleMessage(ConsoleMessage cm) {
			Log.e("raon", cm.message() + " -- From Line " + cm.lineNumber() + "of" + cm.sourceId());
			return true;
		}
		// For Android > 4.1
		// For Android > 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			openFileChooser(uploadMsg);
		}
		// Andorid 3.0 +
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			openFileChooser(uploadMsg);
		}
		/**
		 * 파일 업로드. input tag를 클릭했을 때 호출된다.<br>
		 * 카메라와 갤러리 리스트를 함께 보여준다.
		 * @param uploadMsg
		 */
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			mUploadMessage = uploadMsg;

			File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "test");

			if (!directory.exists()) {
				directory.mkdir();
			}
			mTempFile = new File(directory, "photo_" + new Date().getTime() + ".jpg");


			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("*/*");
			Intent chooserIntent = Intent.createChooser(i,"File Chooser");

			MainActivity.this.startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
		}
	}


	// content의 랜더링에 대해서 받는 역할을 하고 에러메시지나 form submit을 처리
	public class AxaWebViewClient extends WebViewClient {

		// URL 재가공
		// 웹뷰상에서 접근한 웹페이지의 URL을 재가공할 시 shouldOverrideUrlLoading메소드를
		// 오버라이딩하여 이용함
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("url", url);
			
			view.loadUrl(url);
			
			return super.shouldOverrideUrlLoading(view, url);
		}

		// SSL 예외처리
		// 본 샘플에서는 ssl을 이용하는 https 프로토콜을 이용하는 웹페이지에 대해서,
		// 서버인증서가 검증되지 않은 사설 서버인증서를 이용할 경우에 에라로 처리하지 아니하고
		// 진행하기 위해 아래와 같이 onReceivedSslError메소드를 오버라이드 하였음
		@SuppressWarnings("deprecation")
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
				handler.proceed();
			} else if (error.getPrimaryError() == SslError.SSL_EXPIRED) {
				handler.proceed();
			} else if (error.getPrimaryError() == SslError.SSL_MAX_ERROR) {
				handler.proceed();
			} else if (error.getPrimaryError() == SslError.SSL_NOTYETVALID) {
				handler.proceed();
			} else if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
				handler.proceed();
			} else {
				handler.proceed();
			}
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			
			if (requestCode == FILECHOOSER_RESULTCODE) { //파일 선택.
				if (null == mUploadMessage)
					return;
				Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
				
				if ( mTempFile.exists() ) {
					
					mUploadMessage.onReceiveValue(Uri.fromFile(mTempFile));
					mUploadMessage = null;
					
				} else {
					
					mUploadMessage.onReceiveValue(result);
					mUploadMessage = null;
				}
				
				return;
			} else if ( requestCode == WebViewImageUploadHelper.KITKAT_FILECHOOSER ) { //킷캣.
				Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
				
				WebViewImageUploadHelper.getInstance(this, mWebView).updateContent(result);
				return;
			} else if ( requestCode == WebViewImageUploadHelper.KITKAT_CAMERA) { //킷캣 카메라.
				WebViewImageUploadHelper.getInstance(this, mWebView).updateContent();
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
//		Log.i(getComponentName().toShortString(), "MainActivity onRestart");
//		mAppIronHelper.start();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("debug", "onSaveInstanceState");
	}
}
