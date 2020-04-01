package com.uwaterloo.watodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskHolder> {
    private OnItemClickListener listener;

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDdlDay() == newItem.getDdlDay() &&
                    oldItem.getDdlMonth() == newItem.getDdlMonth() &&
                    oldItem.getDdlYear() == newItem.getDdlYear() &&
                    oldItem.getCompleteness() == newItem.getCompleteness() &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.textViewTitle.setText(currentTask.getTitle());
        int ddlYear = currentTask.getDdlYear();
        int ddlMonth = currentTask.getDdlMonth();
        int ddlDay = currentTask.getDdlDay();
        String ddl;
        if (ddlYear != 0 && ddlMonth != 0 && ddlDay != 0) {
            ddl = currentTask.getDdlYear() + "." + currentTask.getDdlMonth() + "." + currentTask.getDdlDay();
        } else {
            ddl = "No Deadline";
        }
        holder.textViewDeadline.setText(ddl);
        holder.completenessProgressBar.setProgress(currentTask.getCompleteness());
        holder.textRatingBar.setRating(currentTask.getPriority());
    }

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDeadline;
        private ProgressBar completenessProgressBar;
        private RatingBar textRatingBar;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDeadline = itemView.findViewById(R.id.text_view_deadline);
            completenessProgressBar = itemView.findViewById(R.id.progress_bar_completeness);
            textRatingBar = itemView.findViewById(R.id.text_view_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
