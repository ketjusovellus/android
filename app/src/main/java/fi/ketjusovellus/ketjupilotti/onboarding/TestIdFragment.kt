package fi.ketjusovellus.ketjupilotti.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import fi.ketjusovellus.ketjupilotti.R
import kotlinx.android.synthetic.main.fragment_onboarding_test_id.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TestIdFragment : Fragment() {

    val onboardingViewModel : OnboardingViewModel by sharedViewModel()

    private val validationObserver = Observer<Boolean> { isValid->
        button_next.isEnabled = isValid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_onboarding_test_id, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_next.setOnClickListener {
            findNavController().navigate(R.id.action_testIdFragment_to_landingFragment)
        }

        test_id_input.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    onboardingViewModel.validateID(test_id_input.text)
                }
            }
        )

        onboardingViewModel.idValid.observe(viewLifecycleOwner, validationObserver)
    }

}
