package com.lumko.teachme.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lumko.teachme.databinding.ActivityWebViewBinding
import com.lumko.teachme.manager.App


class WebViewActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityWebViewBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        init()
        requestPermissions()
    }

    private fun init() {
        val webView = mBinding.webView
        webView.webViewClient = object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?){
                super.onPageFinished(view, url)
                println("Teachme Webview onPageFinished${url}")
                // Check if the URL is the one that indicates completion
                if (url != null && (url.contains("payments/status"))) {
                    // Finish the WebViewActivity and return to the previous activity
                    finish()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread {
                    request.grant(request.resources)
                }
            }
        }

        val webViewSettings = webView.settings
        webViewSettings.javaScriptCanOpenWindowsAutomatically = true
        webViewSettings.mediaPlaybackRequiresUserGesture = false
        webViewSettings.javaScriptEnabled = true
        webViewSettings.domStorageEnabled = true
        webViewSettings.databaseEnabled = true
        webViewSettings.minimumFontSize = 1
        webViewSettings.minimumLogicalFontSize = 1
        webViewSettings.allowFileAccess = true
        webViewSettings.allowContentAccess = true

        val url = intent.getStringExtra(App.URL)!!
        if (url.contains("<iframe", true)) {
            webView.loadData(url, "text/html; charset=utf-8", "UTF-8")
        } else {
            webView.loadUrl(url)
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }
    override fun onPause() {
        super.onPause()
        mBinding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.webView.destroy()
    }
    override fun onBackPressed() {
        if (mBinding.webView.canGoBack()) {
            mBinding.webView.goBack()
        } else {
            super.onBackPressed()  // This will close the WebViewActivity and return to the previous activity
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.webView.saveState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mBinding.webView.restoreState(savedInstanceState)
    }


}