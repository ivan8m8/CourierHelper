package io.github.ivan8m8.courierhelper.features.country_code

import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.core.common.ui.BaseModalBottomSheetFragment
import io.github.ivan8m8.courierhelper.data.utils.BasicDiffUtilItemCallback
import io.github.ivan8m8.courierhelper.data.utils.dp
import io.github.ivan8m8.courierhelper.databinding.FragmentCountryCodesBinding
import io.github.ivan8m8.courierhelper.ui.utils.addSystemBottomPadding
import io.github.ivan8m8.courierhelper.ui.utils.hideKeyboard
import io.github.ivan8m8.courierhelper.ui.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CountryCodesBottomSheetFragment : BaseModalBottomSheetFragment(
    R.layout.fragment_country_codes
) {

    private val binding by viewBinding(FragmentCountryCodesBinding::bind)
    private val viewModel: CountryCodeViewModel by viewModel()
    private val countryDataAdapter = AsyncListDifferDelegationAdapter(
        BasicDiffUtilItemCallback(),
        CountryCodeAdapterDelegate.countryCodeDelegate(),
    )
    private var isSearchVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior.skipCollapsed = true
        with(binding) {
            with(countriesRecycler) {
                val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                searchTextInputLayout.measure(spec, spec)
                val searchTextInputHeight = searchTextInputLayout.measuredHeight
                updatePadding(top = searchTextInputHeight + 16.dp)
                addSystemBottomPadding()
                tieSearchToScroll(binding, searchTextInputHeight, savedInstanceState)
                adapter = countryDataAdapter
                setHasFixedSize(false)
            }
            searchEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.searchInputChanges(text?.toString())
            }
        }
        with(viewModel) {
            countryCodesLiveData.observe(viewLifecycleOwner) { countries ->
                countryDataAdapter.items = countries
            }
        }
    }

    private fun RecyclerView.tieSearchToScroll(
        binding: FragmentCountryCodesBinding,
        searchTextInputHeight: Int,
        savedInstanceState: Bundle?,
    ) {
        val minTranslationY = - searchTextInputHeight * 2f
        val maxTranslationY = 0f
        var currentAnimator: ViewPropertyAnimator? = null

        isSearchVisible = savedInstanceState?.getBoolean(IS_SEARCH_VISIBLE) ?: isSearchVisible
        binding.searchTextInputLayout.translationY =
            if (isSearchVisible) maxTranslationY else minTranslationY

        fun createAnimation(isShow: Boolean) = binding.searchTextInputLayout
            .animate()
            .translationY(if (isShow) maxTranslationY else minTranslationY)
            .setDuration(300)
            .withEndAction {
                currentAnimator = null
            }

        fun toggleAnimation(isShow: Boolean) {
            currentAnimator?.cancel()
            isSearchVisible = isShow
            currentAnimator = createAnimation(isShow).apply { start() }
        }

        val threshold = 8
        setOnScrollChangeListener { _, _, _, _, deltaY ->
            val scrollOffset = computeVerticalScrollOffset()
            if (scrollOffset <= searchTextInputHeight && !isSearchVisible) {
                toggleAnimation(true)
            } else if (deltaY < -threshold && isSearchVisible) {
                toggleAnimation(false)
                hideKeyboard()
                binding.searchEditText.clearFocus()
            } else if (deltaY > threshold && !isSearchVisible) {
                toggleAnimation(true)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_SEARCH_VISIBLE, isSearchVisible)
    }

    companion object {
        private const val IS_SEARCH_VISIBLE = "IS_SEARCH_VISIBLE"
        fun newInstance() = CountryCodesBottomSheetFragment()
    }
}