package fi.ketjusovellus.ketjupilotti.registerphone

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import fi.ketjusovellus.ketjupilotti.R
import kotlinx.android.synthetic.main.fragment_phonenumber_register.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RegisterPhoneFragment : Fragment() {

    private val viewModel : ContagionViewModel by sharedViewModel()

    private val validationObserver = Observer<Boolean> { isValid->
        button_next.isEnabled = isValid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_phonenumber_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone_input.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.validatePhoneNumber(phone_input.text)
                }
            }
        )

        button_next.setOnClickListener {
            viewModel.setPhoneNumber(phone_input.text)
            findNavController(this).navigate(R.id.action_registerPhoneFragment_to_pinVerificationFragment)
        }

        viewModel.phoneNumberValid.observe(viewLifecycleOwner, validationObserver)
    }

}
