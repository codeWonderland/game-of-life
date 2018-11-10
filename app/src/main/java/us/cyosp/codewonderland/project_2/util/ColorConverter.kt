package us.cyosp.codewonderland.project_2.util

import android.graphics.Color
import android.util.Log
import us.cyosp.codewonderland.project_2.R

/*
THIS CODE WAS NOTE WRITTEN BY US
Author: Hemant Chand
URL: https://github.com/duggu-hcd/TransparentColorCode/blob/master/ColorTransparentUtils.java

Modified by: Alice Easter
Date: Nov 9, 2018
Desc: Converted to kotlin and restructured system
 */
class ColorConverter {

    companion object {
        // This default color int
        private const val defaultColor = R.color.colorAccent

        /**
         * This method convert numver into hexa number or we can say transparent code
         * @param trans number of transparency you want
         * @return it return hex decimal number or transparency code
         */
        private fun convert(trans: Int): String {
            val hexString = Integer.toHexString(Math.round((255 * trans / 100).toFloat()))
            return (if (hexString.length < 2) "0" else "") + hexString
        }

        /**
         * Convert color code into transparent color code
         * @param colorCode color code
         * @param transCode transparent number
         * @return transparent color code
         */
        fun convertIntoColor(colorCode: Int, transCode: Int): Int {
            // convert color code into hex string and remove starting 2 digit
            val color = Integer.toHexString(colorCode).toUpperCase().substring(2)

            return if (!color.isEmpty() && transCode <= 100) {
                Color.parseColor("#${convert(transCode)}$color")
            } else {
                Log.d("ColonyConverter", "issue parsing color")
                Color.parseColor("#$defaultColor")
            }
        }
    }
}