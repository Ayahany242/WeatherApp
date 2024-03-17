/*
package com.example.weather.view.alerts.alertViewModel


import android.content.Context
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.model.RemoteDataSource.RemoteDataSourceImp
import com.example.weather.model.localDataSource.LocalDataSourceImpl
import com.example.weather.model.repository.RepositoryImp
import com.example.weather.view.alerts.AlertKind
import com.example.weather.view.alerts.sendNotification

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar


const val ID = "ID"

class Worker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repo = RepositoryImp.getInstance(
            RemoteDataSourceImp.getInstance(),
            LocalDataSourceImpl.getInstance(applicationContext))
        val appContext = applicationContext

        val id = inputData.getString(ID)

        return withContext(Dispatchers.IO) {
            if (id != null) {
                try {
                    val alertPojo = repo.getAlertWithId(id)
                    val response = repo.getWeather("${alertPojo.lat}", "${alertPojo.lon}")
                    delay(alertPojo.end - alertPojo.start)
                    response.catch {
                        when (alertPojo.kind) {

                            AlertKind.ALARM -> runBlocking {
                                createAlarm(
                                    appContext,
                                    appContext.getString(R.string.Alert)
                                )
                            }
                            AlertKind.NOTIFICATION -> sendNotification(
                                appContext,
                                appContext.getString(R.string.Alert)
                            )
                        }
                    }
                        .collect{
                            when (alertPojo.kind) {

                                AlertKind.ALARM -> runBlocking {
                                    createAlarm(
                                        appContext,
                                        "${it.current?.weather?.get(0)?.description.toString()} , In ${getMarkerAddress(appContext,alertPojo.lat,alertPojo.lon)}"
                                    )
                                }
                                AlertKind.NOTIFICATION -> sendNotification(
                                    appContext,
                                    "${it.current?.weather?.get(0)?.description.toString()} , In ${getMarkerAddress(appContext,alertPojo.lat,alertPojo.lon)}"
                                )
                            }
                            Log.i("TAG", "doWork: ${it.current?.weather?.get(0)?.description}")
                        }
                    removeFromDataBaseAndDismiss(repo, alertPojo,appContext)
                    Result.success()
                } catch (e: Exception) {
                    e.printStackTrace()
                    sendNotification(
                        appContext,
                        appContext.getString(R.string.some_thing_want_rong)
                    )
                    Result.failure()

                }
            } else {
                sendNotification(
                    appContext,
                    appContext.getString(R.string.some_thing_want_rong)
                )
                Result.failure()
            }
        }

    }

    private suspend fun removeFromDataBaseAndDismiss(
        repo: WeatherRepo,
        alertPojo: AlertPojo,
        appContext: Context
    ) {
        val _Day_TIME_IN_MILLISECOND = 24*60*60*1000L
        val now = Calendar.getInstance().timeInMillis
        if((alertPojo.end -  now)  < _Day_TIME_IN_MILLISECOND){
            WorkManager.getInstance(appContext).cancelAllWorkByTag(alertPojo.id)
            repo.delAlertLocation(alertPojo)
        }

    }


}


val LAYOUT_FLAG =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    else WindowManager.LayoutParams.TYPE_PHONE

private suspend fun createAlarm(context: Context, message: String) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.an)

    val view: View = LayoutInflater.from(context).inflate(R.layout., null, false)
    val dismissBtn = view.findViewById<Button>(R.id.button_dismiss)
    val textView = view.findViewById<TextView>(R.id.textViewMessage)
    val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        LAYOUT_FLAG,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )
    layoutParams.gravity = Gravity.CENTER


    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    withContext(Dispatchers.Main) {
        windowManager.addView(view, layoutParams)
        view.visibility = View.VISIBLE
        textView.text = message
    }

    mediaPlayer.start()
    mediaPlayer.isLooping = true
    dismissBtn.setOnClickListener {
        mediaPlayer?.release()
        windowManager.removeView(view)
    }
}
*/
