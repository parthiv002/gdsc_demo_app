package com.example.gdsc_demo_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectAdapter(
    private val subjects: MutableList<Subject>,
    private val onSubjectsUpdated: (List<Subject>) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
        val btnYes: Button = itemView.findViewById(R.id.btnYes)
        val btnNo: Button = itemView.findViewById(R.id.btnNo)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.tvSubjectName.text = subject.name
        updatePercentage(holder.tvPercentage, subject)

        // Increment attendance and update UI
        holder.btnYes.setOnClickListener {
            subject.attendedClasses++
            subject.totalClasses++
            notifyItemChanged(position) // Notify changes for the specific item
            updatePercentage(holder.tvPercentage, subject)
            onSubjectsUpdated(subjects) // Notify parent of the update
        }

        // Increment total classes without attendance
        holder.btnNo.setOnClickListener {
            subject.totalClasses++
            notifyItemChanged(position)
            updatePercentage(holder.tvPercentage, subject)
            onSubjectsUpdated(subjects)
        }

        // Delete a subject and notify parent
        holder.btnDelete.setOnClickListener {
            subjects.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, subjects.size)
            onSubjectsUpdated(subjects)
        }
    }

    /**
     * Update the attendance percentage displayed for a subject.
     */
    private fun updatePercentage(tvPercentage: TextView, subject: Subject) {
        val percentage = if (subject.totalClasses > 0) {
            (subject.attendedClasses * 100) / subject.totalClasses
        } else {
            0
        }
        tvPercentage.text = "$percentage%"
    }

    override fun getItemCount(): Int = subjects.size
}
