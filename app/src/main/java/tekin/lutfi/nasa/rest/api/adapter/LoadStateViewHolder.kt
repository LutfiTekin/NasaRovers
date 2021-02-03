package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import tekin.lutfi.nasa.rest.api.R
import tekin.lutfi.nasa.rest.api.model.Photo

class LoadStateViewHolder internal constructor(
    inflater: LayoutInflater,
    parent: ViewGroup,
    @LayoutRes res: Int,
    private val retryListener: RetryListener
) : RecyclerView.ViewHolder(inflater.inflate(res, parent, false)){

    private val retryButton = itemView.findViewById<Button>(R.id.retryButton)
    private val progress = itemView.findViewById<ProgressBar>(R.id.progress)
    private val errorMessage = itemView.findViewById<TextView>(R.id.errorMessage)

    init {
        retryButton.setOnClickListener {
            retryListener.onRetry()
        }
    }

    fun bind(loadState: LoadState){

        retryButton.isVisible = loadState !is LoadState.Loading
        errorMessage.isVisible = loadState !is LoadState.Loading
        progress.isVisible = loadState is LoadState.Loading

       if (loadState is LoadState.Error){
           errorMessage.text = loadState.error.localizedMessage
       }
    }

    interface RetryListener{
        fun onRetry()
    }

}