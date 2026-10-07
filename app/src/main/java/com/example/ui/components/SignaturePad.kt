package com.example.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignaturePad(
    initialSignaturePoints: String,
    onSignatureChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // List of stroke paths (each stroke is a list of Offset)
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // Parse initial points if strokes is empty
    LaunchedEffect(initialSignaturePoints) {
        if (strokes.isEmpty() && initialSignaturePoints.isNotBlank()) {
            val parsedStrokes = parseSvgOrPoints(initialSignaturePoints)
            if (parsedStrokes.isNotEmpty()) {
                strokes.addAll(parsedStrokes)
            }
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .pointerInteropFilter { motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            currentStroke = listOf(Offset(motionEvent.x, motionEvent.y))
                            true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            currentStroke = currentStroke + Offset(motionEvent.x, motionEvent.y)
                            true
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            if (currentStroke.isNotEmpty()) {
                                strokes.add(currentStroke)
                                currentStroke = emptyList()
                                onSignatureChanged(encodeStrokes(strokes))
                            }
                            true
                        }
                        else -> false
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw existing strokes
                for (stroke in strokes) {
                    if (stroke.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke[0].x, stroke[0].y)
                            for (i in 1 until stroke.size) {
                                lineTo(stroke[i].x, stroke[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1B5E20),
                            style = Stroke(
                                width = 3.5f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    } else if (stroke.size == 1) {
                        drawCircle(
                            color = Color(0xFF1B5E20),
                            radius = 2.5f,
                            center = stroke[0]
                        )
                    }
                }

                // Draw currently active stroke
                if (currentStroke.size > 1) {
                    val path = Path().apply {
                        moveTo(currentStroke[0].x, currentStroke[0].y)
                        for (i in 1 until currentStroke.size) {
                            lineTo(currentStroke[i].x, currentStroke[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1B5E20),
                        style = Stroke(
                            width = 3.5f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (strokes.isEmpty() && currentStroke.isEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Draw,
                        contentDescription = "Tanda Tangan",
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "Sentuh & bubuhkan paraf di sini",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {
                    strokes.clear()
                    currentStroke = emptyList()
                    onSignatureChanged("")
                },
                modifier = Modifier.testTag("clear_signature_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Hapus Paraf", modifier = Modifier.padding(end = 4.dp))
                Text("Hapus Paraf", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// Simple compact point serialization format: "x1,y1;x2,y2|x3,y3;x4,y4"
fun encodeStrokes(strokes: List<List<Offset>>): String {
    return strokes.joinToString("|") { stroke ->
        stroke.joinToString(";") { "${it.x.toInt()},${it.y.toInt()}" }
    }
}

fun parseSvgOrPoints(data: String): List<List<Offset>> {
    val result = mutableListOf<List<Offset>>()
    if (data.isBlank()) return result
    try {
        val strokeStrings = data.split("|")
        for (str in strokeStrings) {
            val pointStrings = str.split(";")
            val points = mutableListOf<Offset>()
            for (p in pointStrings) {
                val coords = p.split(",")
                if (coords.size == 2) {
                    val x = coords[0].toFloatOrNull()
                    val y = coords[1].toFloatOrNull()
                    if (x != null && y != null) {
                        points.add(Offset(x, y))
                    }
                }
            }
            if (points.isNotEmpty()) {
                result.add(points)
            }
        }
    } catch (_: Exception) {
        // fallback
    }
    return result
}
