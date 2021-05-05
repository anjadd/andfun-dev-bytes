/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.devbyteviewer.R
import com.example.android.devbyteviewer.databinding.FragmentDevByteBinding
import com.example.android.devbyteviewer.domain.Video
import com.example.android.devbyteviewer.viewmodels.DevByteViewModel

/**
 * Show a list of DevBytes on screen.
 */
class DevByteFragment : Fragment() {

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    private val viewModel: DevByteViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(this, DevByteViewModel.Factory(activity.application))
                .get(DevByteViewModel::class.java)

    }

    /**
     * RecyclerView Adapter for converting a list of Video to cards.
     */
    private var viewModelAdapter: DevByteAdapter? = null

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playlist.observe(viewLifecycleOwner, { videos ->
            videos?.apply {
                viewModelAdapter?.videos = videos
            }
        })
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentDevByteBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_dev_byte,
                container,
                false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        /**
         * Create and initialize the RecyclerView Adapter for converting a list of Video to cards.
         */
        viewModelAdapter = DevByteAdapter(VideoClick { video -> viewModel.showVideo(video) })

        binding.recyclerView.adapter = viewModelAdapter

        viewModel.navigateToVideoLink.observe(viewLifecycleOwner) { video ->
            video?.let {
                val packageManager = context?.packageManager ?: return@let

                // Try to generate a direct intent to the YouTube app
                var intent = Intent(Intent.ACTION_VIEW, it.launchUri)
                if (intent.resolveActivity(packageManager) == null) {
                    // YouTube app isn't found, use the web url
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                }

                startActivity(intent)

                viewModel.onShowVideoNavigationDone()
            }
        }
        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

        return binding.root
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    /**
     * Helper method to generate YouTube app links
     */
    private val Video.launchUri: Uri
        get() {
            val httpUri = Uri.parse(url)
            return Uri.parse("vnd.youtube:" + httpUri.getQueryParameter("v"))
        }
}