package com.example.xtrack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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

class ProfileFragment : Fragment() {

    private lateinit var sceneView: SceneView
    private var totalRotationY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sceneView = view.findViewById(R.id.sceneViewv)

        val engine = sceneView.engine  // Get the engine from SceneView
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

            sceneView.setOnTouchListener { _, event ->
                !isTouchEnabled  // Return true to consume and block the touch
            }


//
//            var previousX = 0f
//            var totalRotationY = 0f
//
//            sceneView.setOnTouchListener { _, event ->
//                when (event.action) {
//                    android.view.MotionEvent.ACTION_DOWN -> {
//                        previousX = event.x
//                        true
//                    }
//                    android.view.MotionEvent.ACTION_MOVE -> {
//                        val deltaX = event.x - previousX
//                        previousX = event.x
//
//                        totalRotationY += deltaX * 0.5f
//
//                        modelNode.transform = Transform(
//                            position = modelNode.transform.position,
//                            scale = modelNode.transform.scale,
//                            rotation = Rotation(y = totalRotationY)
//                        )
//                        true
//                    }
//                    else -> false
//                }
//            }
//

            // Add the model node to the scene
            sceneView.addChildNode(modelNode)

            val rotationSpeed = 0.2f  // Adjust this for slower/faster rotation

            // Coroutine to rotate the model continuously
            while (true) {
                delay(5)  // ~60 FPS

                // Update the total rotation on Y-axis
                totalRotationY += rotationSpeed

                // Convert totalRotationY to radians (SceneView expects radians)
                val radians = Math.toRadians(totalRotationY.toDouble()).toFloat()

                // Create a Quaternion for Y-axis rotation
                val rotation = Quaternion.fromEuler(0f, radians, 0f)

                // Apply the new rotation to the model's transform
                modelNode.transform = Transform(
                    position = modelNode.transform.position,
                    scale = modelNode.transform.scale,
                    rotation = Rotation(y = totalRotationY)
                )
            }

        }
        return view
    }

}

