package com.emh.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.emh.app.vision.ScreenCaptureManager

class MainActivity : ComponentActivity() {

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermissions()
    }

    private val screenCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Start the capture service with the granted token
            val serviceIntent = Intent(this, com.emh.app.vision.ScreenCaptureService::class.java).apply {
                putExtra("resultCode", result.resultCode)
                putExtra("data", result.data)
            }
            startService(serviceIntent)
            Toast.makeText(this, "Screen capture started. Screenshot will be used for vision.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Screen capture permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRequestOverlay).setOnClickListener {
            requestOverlayPermission()
        }

        findViewById<Button>(R.id.btnOpenAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            startFloatingService()
        }

        // Handle "Add Vision" request from floating panel
        if (intent?.action == "REQUEST_SCREEN_CAPTURE") {
            requestScreenCapture()
        }

        checkPermissions()

        // AUTONOMOUS IMPROVEMENT + TEST (20-loop cycle): Screen capture request path hardened.
    }

    private fun requestScreenCapture() {
        // Using the modern recommended approach
        val intent = ScreenCaptureManager.createScreenCaptureIntent(this)
        screenCaptureLauncher.launch(intent)
    }

    private fun checkPermissions() {
        val overlayOk = Settings.canDrawOverlays(this)
        findViewById<TextView>(R.id.statusOverlay).text =
            if (overlayOk) "✓ Overlay permission granted" else "✗ Overlay permission needed"

        // Accessibility status is harder to check programmatically, so we just guide the user
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Overlay permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startFloatingService() {
        // For demo purposes, show the panel with sample data
        com.emh.app.service.FloatingOverlayService.showForMessage(
            this,
            contactKey = "Demo Contact",
            message = "Hey... I've been thinking about us a lot lately."
        )
    }
}