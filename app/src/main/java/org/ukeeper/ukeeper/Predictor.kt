package org.ukeeper.ukeeper

import android.content.Context
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.ukeeper.ukeeper.ml.UKeeper

class Predictor(private val context: Context) {
    private val model = UKeeper.newInstance(context)

    public fun predict(floats: FloatArray): Float {
        Log.i("FFF", "SE0")
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 3), DataType.FLOAT32)
        Log.i("FFF", "SE1")
        inputFeature0.loadArray(floats)

        Log.i("FFF", "SE2")
        val outputs = model.process(inputFeature0)
        Log.i("FFF", "SE3")
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        Log.i("FFF", "SE4")
        return outputFeature0.buffer.getFloat(0)
    }

    fun closeModel() {
        model.close()
    }
}