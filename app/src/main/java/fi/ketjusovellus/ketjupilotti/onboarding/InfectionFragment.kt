package fi.ketjusovellus.ketjupilotti.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import fi.ketjusovellus.ketjupilotti.R
import org.koin.android.viewmodel.ext.android.sharedViewModel

class InfectionFragment : Fragment() {

    private val onboardingViewModel : OnboardingViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_onboarding_infection, container, false)

        root.findViewById<AppCompatButton>(R.id.button_next)?.setOnClickListener {
            onboardingViewModel.setOnboardingCompleted(true)
            root.findNavController().navigate(R.id.action_infectionFragment_to_mainActivity)
            activity?.finish()
        }
        return root
    }

}
