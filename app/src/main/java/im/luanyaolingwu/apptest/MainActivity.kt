package im.luanyaolingwu.apptest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager //用于获取包名，但申请的时候用的是上下文（（（大雾
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import im.luanyaolingwu.apptest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRequestOptimization.setOnClickListener {
            if (!checkBatteryOptimization()) {
                autorequestBatteryOptimization()
            } else {
                userTuneBatteryOptimizationState()
            }
        }

        binding.btnCheckOptimization.setOnClickListener {
            val isOptimized = !checkBatteryOptimization()
            val packageName = getAppPackageName(packageManager)
            binding.txtOptimizationStatus.text = "Optimization Status: \n $packageName - $isOptimized"
        }
    }

    private fun checkBatteryOptimization(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(packageName)
        }
        return true
    }

    private fun userTuneBatteryOptimizationState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            startActivity(intent)
        }
    }

    private fun autorequestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    /*fun getAppPackageName(): String? {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getDeclaredMethod("currentApplication")
            val application = currentApplicationMethod.invoke(null)
            val contextClass = application.javaClass
            val packageNameField = contextClass.getDeclaredField("packageName")
            packageNameField.isAccessible = true
            return packageNameField.get(application) as? String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }*/ //这个方法似乎无效了？

    private fun getAppPackageName(packageManager: PackageManager): String? {
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            return applicationInfo.packageName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

}