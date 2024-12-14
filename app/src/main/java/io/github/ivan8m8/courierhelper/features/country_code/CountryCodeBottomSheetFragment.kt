package io.github.ivan8m8.courierhelper.features.country_code

import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.github.ivan8m8.courierhelper.R
import io.github.ivan8m8.courierhelper.core.common.ui.BaseModalBottomSheetFragment
import io.github.ivan8m8.courierhelper.data.utils.BasicDiffUtilItemCallback
import io.github.ivan8m8.courierhelper.data.utils.dp
import io.github.ivan8m8.courierhelper.databinding.FragmentCountryCodesBinding
import io.github.ivan8m8.courierhelper.ui.utils.addSystemBottomPadding
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
                tieSearchToScroll(searchTextInputLayout, searchTextInputHeight)
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
        searchTextInputLayout: TextInputLayout,
        searchTextInputHeight: Int
    ) {
        val minTranslationY = - searchTextInputHeight * 2f
        val maxTranslationY = 0f
        var isSearchVisible = true
        var currentAnimator: ViewPropertyAnimator? = null

        fun createAnimation(show: Boolean) = searchTextInputLayout
            .animate()
            .translationY(if (show) maxTranslationY else minTranslationY)
            .setDuration(300)
            .withEndAction {
                currentAnimator = null
            }

        setOnScrollChangeListener { _, _, _, _, deltaY ->
            if (deltaY < 0 && isSearchVisible) {
                currentAnimator?.cancel()
                isSearchVisible = false
                currentAnimator = createAnimation(false).apply { start() }
            } else if (deltaY > 0 && !isSearchVisible) {
                currentAnimator?.cancel()
                isSearchVisible = true
                currentAnimator = createAnimation(true).apply { start() }
            }
        }
    }

    companion object {
        fun newInstance() = CountryCodesBottomSheetFragment()
    }
}