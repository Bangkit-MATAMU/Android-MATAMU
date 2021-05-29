package com.akih.matarak.history

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class ItemTouchHelperCallback(
        private val adapter: HistoryAdapter,
        private val view: View,
        private val viewModel: HistoryViewModel
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder) = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val history = adapter.differ.currentList[position]
//        viewModel.deleteHistory(history)
        Snackbar.make(view, "Successfully deleted this history", Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
//                viewModel.saveHistory(history)
            }
            show()
        }
    }
}