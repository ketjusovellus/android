package fi.ketjusovellus.ketjupilotti.registerphone

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import fi.ketjusovellus.ketjupilotti.R
import fi.ketjusovellus.ketjupilotti.utils.DialogUtils
import fi.ketjusovellus.ketjupilotti.utils.PreferencesConstants
import kotlinx.android.synthetic.main.fragment_phonenumber_pin.*
import org.dpppt.android.sdk.DP3T
import org.dpppt.android.sdk.backend.ResponseCallback
import org.dpppt.android.sdk.backend.models.ExposeeAuthMethodJson
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PinVerificationFragment : Fragment() {

    private val viewModel : ContagionViewModel by sharedViewModel()

    private val validationObserver = Observer<Boolean> { isValid->
        button_next.isEnabled = isValid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_phonenumber_pin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_next.setOnClickListener { sendIamInfected() }
        otp_input.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.validateOTP(otp_input.text)
                }
            }
        )

        viewModel.otpValid.observe(viewLifecycleOwner, validationObserver)
    }

    private fun sendIamInfected() {
        DP3T.sendIAmInfected(requireContext(), viewModel.getContagionDate(),
            ExposeeAuthMethodJson(viewModel.getExposeeAuthCode()), object : ResponseCallback<Void> {
                override fun onSuccess(response: Void?) {
                    val args = bundleOf(PreferencesConstants.PREF_INFECTED_KEY to true)
                    findNavController().navigate(R.id.action_pinVerificationFragment_to_homeFragment, args)
                }

                override fun onError(throwable: Throwable?) {
                    DialogUtils.showOkDialog(activity,
                        getString(R.string.contagion_share_contacts_error_title),
                        getString(R.string.contagion_share_contacts_error_description),
                        null
                    )
                }
            })
    }
}
