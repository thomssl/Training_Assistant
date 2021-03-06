package com.trainingapp.personaltrainingassistant.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.trainingapp.personaltrainingassistant.R
import com.trainingapp.personaltrainingassistant.enumerators.ExerciseType
import com.trainingapp.personaltrainingassistant.objects.Exercise
import java.util.*
import kotlin.collections.ArrayList

/**
 * Custom array adapter for Adding an ExerciseSession to an exercise. Allows for search methods beyond text match
 * @param context Used to initialize the super class
 * @param resource Layout resource used for each list item
 * @param exercises Full list of exercises to search through
 */
class SearchForExerciseSession(context: Context, private val resource: Int, val exercises: ArrayList<Exercise>) : ArrayAdapter<String>(context, resource) {

    var resultData = ArrayList<Exercise>()//used to fill the FilterResults object

    override fun getCount(): Int = resultData.size

    override fun getItem(position: Int): String = resultData[position].name

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return if (convertView == null){//if item view happens to be null, fill with expected values
            val view = LayoutInflater.from(context).inflate(resource, parent, false)
            val txtName = view.findViewById<TextView>(R.id.txtSimpleAutocompleteItem)
            txtName.text = getItem(position)
            view
        } else {
            val txtName = convertView.findViewById<TextView>(R.id.txtSimpleAutocompleteItem)
            txtName.text = getItem(position)
            convertView
        }
    }

    override fun getFilter(): Filter {
        return ListFilter()
    }

    inner class ListFilter : Filter(){

        override fun performFiltering(input: CharSequence): FilterResults {
            val filterResults = FilterResults()
            resultData.clear()
            if (input.isBlank()){//if input has no characters, fill the list with entire library of exercises
                resultData = ArrayList(exercises.toList())
                filterResults.values = resultData
                filterResults.count = resultData.size
            } else {
                val isMobility = input == "Mobility"
                val isStability = input == "Stability"
                exercises.forEach {
                    //for each exercise, if the input can be found within the name or the input can be found in the primary movers name add the exercise to the results
                    //special cases, if the input is mobility or stability, add any exercise with the respective ExerciseType to the results
                    if (it.name.toLowerCase(Locale.ROOT).contains(input.toString().toLowerCase(Locale.ROOT)) ||
                        it.primaryMover.name.toLowerCase(Locale.ROOT).contains(input.toString().toLowerCase(Locale.ROOT)) ||
                        (isMobility && it.type == ExerciseType.MOBILITY) ||
                        (isStability && it.type == ExerciseType.STABILITY))
                        resultData.add(it)
                }
                filterResults.values = resultData
                filterResults.count = resultData.size
            }
            return filterResults
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults) {
            if (results.count > 0){
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}