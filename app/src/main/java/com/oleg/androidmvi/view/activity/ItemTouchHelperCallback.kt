package com.oleg.androidmvi.view.activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.oleg.androidmvi.R
import com.oleg.androidmvi.view.TouchHelper

class ItemTouchHelperCallback(private val context: Context, private val helper: TouchHelper) :
    ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {

    private val iconDelete = ContextCompat.getDrawable(context, R.drawable.ic_white_delete_sweep_24)
    private var iconAction = ContextCompat.getDrawable(context, R.drawable.ic_white_check_24)
    private var background = ColorDrawable(Color.RED)

    fun setActionIcon(icon: Int) {
        iconAction = ContextCompat.getDrawable(context, icon)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == LEFT) {
            helper.removeMovieAtPosition(viewHolder.absoluteAdapterPosition)
        }
        if (direction == RIGHT) {
            helper.handleRightSwipe(viewHolder.absoluteAdapterPosition)
        }
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val iconHeight = iconDelete?.intrinsicHeight ?: 0
        val iconWidth = iconDelete?.intrinsicWidth ?: 0
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset =
            20 //so background is behind the rounded corners of itemView
        val iconMargin: Int = (itemView.height - iconHeight) / 3
        val iconTop: Int = itemView.top + (itemView.height - iconHeight) / 2
        val iconBottom: Int = iconTop + iconHeight

        when {
            dX > 0 -> { // Swiping right, add to constructor to support
                val iconLeft: Int =
                    itemView.left + iconMargin
                val iconRight: Int =
                    itemView.left + iconMargin + iconWidth
                iconAction?.setBounds(
                    iconLeft,
                    iconTop,
                    iconRight,
                    iconBottom
                )
                background = ColorDrawable(Color.MAGENTA)
                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
                background.draw(c)
                iconAction?.draw(c)
            }
            dX < 0 -> { // Swiping left
                val iconLeft: Int = itemView.right - iconMargin - iconWidth
                val iconRight: Int = itemView.right - iconMargin
                iconDelete?.setBounds(
                    iconLeft,
                    iconTop,
                    iconRight,
                    iconBottom
                )
                background = ColorDrawable(Color.RED)
                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)
                iconDelete?.draw(c)
            }
            else -> { // no swipe happened yet
                background.setBounds(0, 0, 0, 0)
                background.draw(c)
            }
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.7f
}