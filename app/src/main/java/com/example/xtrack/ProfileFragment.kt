package com.example.xtrack

import android.R.attr.typeface
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
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

data class DailyPerformance(val date: LocalDate, val performance: Int)

class ProfileFragment : Fragment() {

    private lateinit var sceneView: SceneView
    private var totalRotationY = 0f
    private lateinit var userNameText: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)

        userNameText = view.findViewById(R.id.UserN)

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
//            lineChart.setViewPortOffsets(0f, 0f, 0f, 0f) // Edge-to-edge look (adjust as needed)

            lineChart.setDrawBorders(false)          // No border
            lineChart.setBackgroundColor(Color.TRANSPARENT)
            lineWidth = 2.5f
            lineChart.axisLeft.setDrawGridLines(false)
//
//            setDrawCircles(false)  // ❌ Disable circle (dot) on each entry
//            setDrawCircleHole(false) // Just to be safe

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

}
