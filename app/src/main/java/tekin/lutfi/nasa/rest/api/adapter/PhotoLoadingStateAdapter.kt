package tekin.lutfi.nasa.rest.api.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import tekin.lutfi.nasa.rest.api.R

class PhotoLoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadStateViewHolder>(), LoadStateViewHolder.RetryListener {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LayoutInflater.from(parent.context),
            parent,
            R.layout.list_item_loading,
            this
        )
    }

    override fun onRetry() {
        retry.invoke()
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }



}