package com.example.gdsc_demo_app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectAdapter(private val subjects: MutableList<Subject>,
                     private val onSubjectsUpdated: (List<Subject>) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
        val tvAtClass:TextView=itemView.findViewById(R.id.tvAtClasses)
        val tvTotalClass:TextView=itemView.findViewById(R.id.tvTotalClass)
        val btnYes: Button = itemView.findViewById(R.id.btnYes)
        val btnNo: Button = itemView.findViewById(R.id.btnNo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.tvSubjectName.text = subject.name
        updatePercentage(holder.tvAtClass,holder.tvTotalClass,holder.tvPercentage, subject)

        // Increment attendance and update UI
        holder.btnYes.setOnClickListener {
            subject.attendedClasses++
            subject.totalClasses++
            notifyItemChanged(position) // Notify changes for the specific item
            updatePercentage(holder.tvAtClass,holder.tvTotalClass,holder.tvPercentage, subject)
            onSubjectsUpdated(subjects) // Notify parent of the update
        }

        // Increment total classes without attendance
        holder.btnNo.setOnClickListener {
            subject.totalClasses++
            notifyItemChanged(position)
            updatePercentage(holder.tvAtClass,holder.tvTotalClass,holder.tvPercentage, subject)
            onSubjectsUpdated(subjects)
        }

    }

    /**
     * Update the attendance percentage displayed for a subject.
     */
    @SuppressLint("SetTextI18n")
    private fun updatePercentage(tvAttendClass:TextView, tvTotalClass:TextView, tvPercentage: TextView, subject: Subject) {
        val attendClasses=subject.attendedClasses
        val totalClasses=subject.totalClasses
        val percentage:Float = if (totalClasses > 0) {
            (attendClasses * 100 * 1f) / totalClasses
        } else {
            0f
        }
        tvAttendClass.text="$attendClasses"
        tvTotalClass.text="$totalClasses"
        tvPercentage.text = "$percentage%"
    }

    override fun getItemCount(): Int = subjects.size
}
