package com.example.xtrack

import android.R.attr.typeface
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.filament.utils.Quaternion
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.math.Transform
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.jvm.java

data class DailyPerformance(val date: LocalDate, val performance: Int)

class ProfileFragment : Fragment() {

    private lateinit var sceneView: SceneView
    private var totalRotationY = 0f
    private lateinit var userNameText: TextView
    private lateinit var settingsCard: CardView
    private lateinit var filterC: LinearLayout


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        userNameText = view.findViewById(R.id.UserN)

        var settingsCard: CardView = view.findViewById(R.id.settingsCard)
        val dimBackground: View = view.findViewById(R.id.dimBackground)
        val filterIcon: ImageView = view.findViewById(R.id.filterIcon)

        settingsCard = view.findViewById(R.id.settingsCard)
        filterC = view.findViewById(R.id.filterContainer)
        val closeIcon = view.findViewById<ImageView>(R.id.closeIcon)

        closeIcon.setOnClickListener {
            hideSettingsCard()
        }

        filterIcon.setOnClickListener {
            showSettingsCard()
        }

        dimBackground.setOnClickListener {
            hideSettingsCard()
        }

        view.findViewById<TextView>(R.id.reminderTime).setOnClickListener {
            showTimePickerDialog()
        }

//        filterC.setOnClickListener {
//            if (settingsCard.visibility == View.GONE) {
//                settingsCard.visibility = View.VISIBLE
//            } else {
//                settingsCard.visibility = View.GONE
//            }
//        }

        // Get the passed username
        val username = arguments?.getString("username") ?: "User"
        userNameText.text = username

        // SceneView setup
        sceneView = view.findViewById(R.id.sceneViewv)

        val lineChart = view.findViewById<LineChart>(R.id.performanceLineChart)
        setupPerformanceLineChart(lineChart)


        val engine = sceneView.engine
        val modelLoader = ModelLoader(context = requireContext(), engine = engine)

        lifecycleScope.launch {
            val modelFile = "male.glb"
            val modelInstance = modelLoader.createModelInstance(modelFile)

            val modelNode = ModelNode(
                modelInstance = modelInstance,
                scaleToUnits = 2.0f
            ).apply {
                scale = Scale(0.042f)
            }

            var isTouchEnabled = false
            sceneView.setOnTouchListener { _, _ -> !isTouchEnabled }

            // Add the model node to the scene
            sceneView.addChildNode(modelNode)

            val rotationSpeed = 0.2f

            while (true) {
                delay(5)
                totalRotationY += rotationSpeed
                val radians = Math.toRadians(totalRotationY.toDouble()).toFloat()
                val rotation = Quaternion.fromEuler(0f, radians, 0f)

                modelNode.transform = Transform(
                    position = modelNode.transform.position,
                    scale = modelNode.transform.scale,
                    rotation = Rotation(y = totalRotationY)
                )
            }
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPerformanceLineChart(lineChart: LineChart) {
        val entries = getMockPerformanceData().mapIndexed { index, dp ->
            Entry(index.toFloat(), dp.performance.toFloat())
        }
        val typeface = Typeface.createFromAsset(requireContext().assets, "fonts/orbitron_medium.ttf")

        val lineDataSet = LineDataSet(entries, "Performance").apply {
            color = ContextCompat.getColor(requireContext(), R.color.LGreen)
            valueTextColor = Color.WHITE
            setDrawCircles(true)
            circleRadius = 4f
            circleHoleColor = Color.BLACK
            circleColors = listOf(Color.YELLOW)
            setDrawValues(false)
            lineChart.description.isEnabled = false  // Remove default description label
            lineChart.legend.isEnabled = false       // Hide legend if not needed
            valueTypeface = typeface
            lineChart.setTouchEnabled(false)         // Optional: disable interaction
            lineChart.setViewPortOffsets(80f, 0f, 0f, 0f) // Edge-to-edge look (adjust as needed)

            lineChart.setDrawBorders(false)          // No border
            lineChart.setBackgroundColor(Color.TRANSPARENT)
            lineWidth = 2.5f
            lineChart.axisLeft.setDrawGridLines(false)
//
            setDrawCircles(false)  // ❌ Disable circle (dot) on each entry
            setDrawCircleHole(false) // Just to be safe

            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient_fill)
        }

        lineChart.apply {
            data = LineData(lineDataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setPinchZoom(false)
            setScaleEnabled(false)
            xAxis.isEnabled = false
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
            setBackgroundColor(Color.BLACK)
            xAxis.typeface = typeface
            axisLeft.typeface = typeface
            axisRight.typeface = typeface
            animateX(1500, Easing.EaseInOutQuad)
            invalidate()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMockPerformanceData(): List<DailyPerformance> {
        val today = LocalDate.now()
        return (0..10).map { i ->
            DailyPerformance(today.minusDays(i.toLong()), (10..100).random())
        }.reversed()
    }

    private fun showSettingsCard() {
        val settingsCard: CardView = requireView().findViewById(R.id.settingsCard)
        val dimBackground: View = requireView().findViewById(R.id.dimBackground)

        dimBackground.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate().alpha(1f).setDuration(300).start()
        }

        settingsCard.apply {
            visibility = View.VISIBLE
            scaleX = 0.8f
            scaleY = 0.8f
            alpha = 0f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start()
        }
    }

    private fun hideSettingsCard() {
        val settingsCard: CardView = requireView().findViewById(R.id.settingsCard)
        val dimBackground: View = requireView().findViewById(R.id.dimBackground)

        settingsCard.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(300)
            .withEndAction {
                settingsCard.visibility = View.GONE
            }.start()

        dimBackground.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                dimBackground.visibility = View.GONE
            }.start()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            view?.findViewById<TextView>(R.id.reminderTime)?.text = formattedTime
            scheduleDailyReminder(selectedHour, selectedMinute)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    @SuppressLint("ServiceCast")
    private fun scheduleDailyReminder(hour: Int, minute: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // schedule for next day if time has already passed
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(requireContext(), "Daily reminder set at $hour:$minute", Toast.LENGTH_SHORT).show()
    }




}
